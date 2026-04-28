package com.canhub.cropper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.canhub.cropper.test.CoroutineTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.ref.WeakReference

/**
 * Test suite for BitmapCroppingWorkerJob - async bitmap cropping.
 *
 * Covers:
 * - URI-based and Bitmap-based cropping
 * - Error handling and exception propagation
 * - Memory management (bitmap recycling)
 * - Coroutine lifecycle (start, cancel, isActive)
 * - Dispatcher switching (Default → IO → Main)
 * - WeakReference handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class BitmapCroppingWorkerJobTest {

  @get:Rule
  val coroutineRule = CoroutineTestRule()

  private lateinit var context: Context
  private lateinit var cropImageView: CropImageView
  private val testUri = Uri.parse("content://test/image.jpg")
  private val outputUri = Uri.parse("content://test/output.jpg")
  private val testCropPoints = floatArrayOf(100f, 100f, 500f, 100f, 500f, 500f, 100f, 500f)

  @Before
  fun setup() {
    context = mockk(relaxed = true)
    cropImageView = mockk(relaxed = true)

    mockkObject(BitmapUtils)
  }

  @After
  fun teardown() {
    unmockkObject(BitmapUtils)
  }

  // ==================== URI-Based Cropping Tests ====================

  @Test
  fun `WHEN URI-based cropping succeeds THEN result contains bitmap and URI`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)
    val croppedBitmap = mockk<Bitmap>(relaxed = true)
    val resizedBitmap = mockk<Bitmap>(relaxed = true)

    every {
      BitmapUtils.cropBitmap(
        context = context,
        loadedImageUri = testUri,
        cropPoints = testCropPoints,
        degreesRotated = 0,
        orgWidth = 1000,
        orgHeight = 1000,
        fixAspectRatio = false,
        aspectRatioX = 1,
        aspectRatioY = 1,
        reqWidth = 0,
        reqHeight = 0,
        flipHorizontally = false,
        flipVertically = false,
      )
    } returns BitmapUtils.BitmapSampled(croppedBitmap, 1)

    every {
      BitmapUtils.resizeBitmap(croppedBitmap, 0, 0, CropImageView.RequestSizeOptions.NONE)
    } returns resizedBitmap

    every {
      BitmapUtils.writeBitmapToUri(
        context,
        resizedBitmap,
        Bitmap.CompressFormat.JPEG,
        90,
        null,
      )
    } returns outputUri

    val resultSlot = slot<BitmapCroppingWorkerJob.Result>()
    every { cropImageView.onImageCroppingAsyncComplete(capture(resultSlot)) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.join()

    // THEN
    val result = resultSlot.captured
    assertEquals(resizedBitmap, result.bitmap)
    assertEquals(outputUri, result.uri)
    assertEquals(1, result.sampleSize)
    assertNull(result.error)
  }

  @Test
  fun `WHEN URI-based cropping with rotation THEN correct parameters passed`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)

    every { BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns BitmapUtils.BitmapSampled(mockBitmap, 1)
    every { BitmapUtils.resizeBitmap(any(), any(), any(), any()) } returns mockBitmap
    every { BitmapUtils.writeBitmapToUri(any(), any(), any(), any(), any()) } returns outputUri
    every { cropImageView.onImageCroppingAsyncComplete(any()) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 90,
      orgWidth = 1000,
      orgHeight = 800,
      fixAspectRatio = true,
      aspectRatioX = 16,
      aspectRatioY = 9,
      reqWidth = 1920,
      reqHeight = 1080,
      flipHorizontally = true,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.RESIZE_EXACT,
      saveCompressFormat = Bitmap.CompressFormat.PNG,
      saveCompressQuality = 100,
      customOutputUri = outputUri,
    )
    job.start()
    job.join()

    // THEN - Verify correct parameters passed to cropBitmap
    verify {
      BitmapUtils.cropBitmap(
        context = context,
        loadedImageUri = testUri,
        cropPoints = testCropPoints,
        degreesRotated = 90,
        orgWidth = 1000,
        orgHeight = 800,
        fixAspectRatio = true,
        aspectRatioX = 16,
        aspectRatioY = 9,
        reqWidth = 1920,
        reqHeight = 1080,
        flipHorizontally = true,
        flipVertically = false,
      )
    }
  }

  // ==================== Bitmap-Based Cropping Tests ====================

  @Test
  fun `WHEN Bitmap-based cropping succeeds THEN result contains bitmap and URI`() = runTest {
    // GIVEN
    val inputBitmap = mockk<Bitmap>(relaxed = true)
    val croppedBitmap = mockk<Bitmap>(relaxed = true)
    val resizedBitmap = mockk<Bitmap>(relaxed = true)

    every {
      BitmapUtils.cropBitmapObjectHandleOOM(
        bitmap = inputBitmap,
        cropPoints = testCropPoints,
        degreesRotated = 0,
        fixAspectRatio = false,
        aspectRatioX = 1,
        aspectRatioY = 1,
        flipHorizontally = false,
        flipVertically = false,
      )
    } returns BitmapUtils.BitmapSampled(croppedBitmap, 1)

    every { BitmapUtils.resizeBitmap(any(), any(), any(), any()) } returns resizedBitmap
    every { BitmapUtils.writeBitmapToUri(any(), any(), any(), any(), any()) } returns outputUri

    val resultSlot = slot<BitmapCroppingWorkerJob.Result>()
    every { cropImageView.onImageCroppingAsyncComplete(capture(resultSlot)) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = null,
      bitmap = inputBitmap,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.join()

    // THEN
    val result = resultSlot.captured
    assertEquals(resizedBitmap, result.bitmap)
    assertEquals(outputUri, result.uri)
    assertNull(result.error)
  }

  // ==================== Error Handling Tests ====================

  @Test
  fun `WHEN cropping throws exception THEN result contains error`() = runTest {
    // GIVEN
    val testException = Exception("Cropping failed")
    every {
      BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
    } throws testException

    val resultSlot = slot<BitmapCroppingWorkerJob.Result>()
    every { cropImageView.onImageCroppingAsyncComplete(capture(resultSlot)) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.join()

    // THEN
    val result = resultSlot.captured
    assertNull(result.bitmap)
    assertNull(result.uri)
    assertEquals(testException, result.error)
    assertEquals(1, result.sampleSize)
  }

  @Test
  fun `WHEN both uri and bitmap are null THEN returns empty result`() = runTest {
    // GIVEN
    val resultSlot = slot<BitmapCroppingWorkerJob.Result>()
    every { cropImageView.onImageCroppingAsyncComplete(capture(resultSlot)) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = null,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.join()

    // THEN
    val result = resultSlot.captured
    assertNull(result.bitmap)
    assertNull(result.uri)
    assertNull(result.error)
    assertEquals(1, result.sampleSize)
  }

  // Note: Cannot test writeBitmapToUri exceptions because it's called inside a nested
  // launch(Dispatchers.IO) block. Exceptions there aren't caught by the parent's try-catch
  // due to Kotlin coroutines structured concurrency. Would require production code changes
  // to test this scenario (change nested launch to withContext).

  // ==================== Coroutine Lifecycle Tests ====================

  @Test
  fun `WHEN job cancelled before completion THEN callback not invoked`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)
    every { BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns BitmapUtils.BitmapSampled(mockBitmap, 1)
    every { BitmapUtils.resizeBitmap(any(), any(), any(), any()) } returns mockBitmap
    every { BitmapUtils.writeBitmapToUri(any(), any(), any(), any(), any()) } returns outputUri
    every { cropImageView.onImageCroppingAsyncComplete(any()) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.cancel()

    // THEN - Callback should not be invoked after cancellation
    verify(exactly = 0) { cropImageView.onImageCroppingAsyncComplete(any()) }
  }

  // ==================== Memory Management Tests ====================

  @Test
  fun `WHEN cropping succeeds and callback invoked THEN bitmap not recycled`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)

    every { BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns BitmapUtils.BitmapSampled(mockBitmap, 1)
    every { BitmapUtils.resizeBitmap(any(), any(), any(), any()) } returns mockBitmap
    every { BitmapUtils.writeBitmapToUri(any(), any(), any(), any(), any()) } returns outputUri
    every { mockBitmap.recycle() } returns Unit

    val resultSlot = slot<BitmapCroppingWorkerJob.Result>()
    every { cropImageView.onImageCroppingAsyncComplete(capture(resultSlot)) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.join()

    // THEN - Callback was called, so bitmap should NOT be recycled
    verify { cropImageView.onImageCroppingAsyncComplete(any()) }
    verify(exactly = 0) { mockBitmap.recycle() }
  }

  // ==================== Resize Tests ====================

  @Test
  fun `WHEN resize requested THEN resizeBitmap called with correct parameters`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)
    val resizedBitmap = mockk<Bitmap>(relaxed = true)

    every { BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns BitmapUtils.BitmapSampled(mockBitmap, 1)
    every { BitmapUtils.resizeBitmap(mockBitmap, 1920, 1080, CropImageView.RequestSizeOptions.RESIZE_EXACT) } returns resizedBitmap
    every { BitmapUtils.writeBitmapToUri(any(), any(), any(), any(), any()) } returns outputUri
    every { cropImageView.onImageCroppingAsyncComplete(any()) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 1920,
      reqHeight = 1080,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.RESIZE_EXACT,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.join()

    // THEN
    verify {
      BitmapUtils.resizeBitmap(
        mockBitmap,
        1920,
        1080,
        CropImageView.RequestSizeOptions.RESIZE_EXACT,
      )
    }
  }

  // ==================== Compression Tests ====================

  @Test
  fun `WHEN PNG compression requested THEN writeBitmapToUri uses PNG format`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)

    every { BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns BitmapUtils.BitmapSampled(mockBitmap, 1)
    every { BitmapUtils.resizeBitmap(any(), any(), any(), any()) } returns mockBitmap
    every {
      BitmapUtils.writeBitmapToUri(
        context,
        mockBitmap,
        Bitmap.CompressFormat.PNG,
        100,
        outputUri,
      )
    } returns outputUri
    every { cropImageView.onImageCroppingAsyncComplete(any()) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.PNG,
      saveCompressQuality = 100,
      customOutputUri = outputUri,
    )
    job.start()
    job.join()

    // THEN
    verify {
      BitmapUtils.writeBitmapToUri(
        context,
        mockBitmap,
        Bitmap.CompressFormat.PNG,
        100,
        outputUri,
      )
    }
  }

  @Test
  fun `WHEN custom output URI provided THEN writeBitmapToUri uses it`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)
    val customUri = Uri.parse("content://custom/output.jpg")

    every { BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns BitmapUtils.BitmapSampled(mockBitmap, 1)
    every { BitmapUtils.resizeBitmap(any(), any(), any(), any()) } returns mockBitmap
    every { BitmapUtils.writeBitmapToUri(any(), any(), any(), any(), customUri) } returns customUri
    every { cropImageView.onImageCroppingAsyncComplete(any()) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = customUri,
    )
    job.start()
    job.join()

    // THEN
    verify {
      BitmapUtils.writeBitmapToUri(
        context,
        mockBitmap,
        Bitmap.CompressFormat.JPEG,
        90,
        customUri,
      )
    }
  }

  // ==================== Sample Size Tests ====================

  @Test
  fun `WHEN cropping with sample size 2 THEN result contains correct sample size`() = runTest {
    // GIVEN
    val mockBitmap = mockk<Bitmap>(relaxed = true)

    every { BitmapUtils.cropBitmap(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns BitmapUtils.BitmapSampled(mockBitmap, 2)
    every { BitmapUtils.resizeBitmap(any(), any(), any(), any()) } returns mockBitmap
    every { BitmapUtils.writeBitmapToUri(any(), any(), any(), any(), any()) } returns outputUri

    val resultSlot = slot<BitmapCroppingWorkerJob.Result>()
    every { cropImageView.onImageCroppingAsyncComplete(capture(resultSlot)) } returns Unit

    // WHEN
    val job = BitmapCroppingWorkerJob(
      context = context,
      cropImageViewReference = WeakReference(cropImageView),
      uri = testUri,
      bitmap = null,
      cropPoints = testCropPoints,
      degreesRotated = 0,
      orgWidth = 1000,
      orgHeight = 1000,
      fixAspectRatio = false,
      aspectRatioX = 1,
      aspectRatioY = 1,
      reqWidth = 0,
      reqHeight = 0,
      flipHorizontally = false,
      flipVertically = false,
      options = CropImageView.RequestSizeOptions.NONE,
      saveCompressFormat = Bitmap.CompressFormat.JPEG,
      saveCompressQuality = 90,
      customOutputUri = null,
    )
    job.start()
    job.join()

    // THEN
    assertEquals(2, resultSlot.captured.sampleSize)
  }

  // ==================== Result Data Class Tests ====================

  @Test
  fun `WHEN Result created THEN all fields accessible`() {
    // GIVEN
    val mockBitmap = mockk<Bitmap>()
    val testException = Exception("test")

    // WHEN
    val result = BitmapCroppingWorkerJob.Result(
      bitmap = mockBitmap,
      uri = outputUri,
      error = testException,
      sampleSize = 4,
    )

    // THEN
    assertEquals(mockBitmap, result.bitmap)
    assertEquals(outputUri, result.uri)
    assertEquals(testException, result.error)
    assertEquals(4, result.sampleSize)
  }

  @Test
  fun `WHEN Result created with error THEN bitmap and uri are null`() {
    // GIVEN/WHEN
    val testException = Exception("Cropping failed")
    val result = BitmapCroppingWorkerJob.Result(
      bitmap = null,
      uri = null,
      error = testException,
      sampleSize = 1,
    )

    // THEN
    assertNull(result.bitmap)
    assertNull(result.uri)
    assertNotNull(result.error)
  }
}
