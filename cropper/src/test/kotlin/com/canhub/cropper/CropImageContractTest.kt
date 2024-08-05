@file:Suppress("DEPRECATION")

package com.canhub.cropper

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CropImageContractTest {

  @Test
  fun `WHEN providing invalid options THEN cropping should crash`() {
    // GIVEN
    var result: Exception? = null
    val expected: IllegalArgumentException = IllegalArgumentException()
    var fragment: ContractTestFragment? = null
    val testRegistry = object : ActivityResultRegistry() {
      override fun <I, O> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?,
      ) {
        dispatchResult(requestCode, Activity.RESULT_CANCELED, Intent())
      }
    }

    with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
      onFragment { fragment = it }
    }
    // WHEN
    try {
      fragment?.cropImageIntent(CropImageContractOptions(null, CropImageOptions().copy(maxZoom = -10)))
    } catch (e: Exception) {
      result = e
    }
    // THEN
    assertEquals(expected.javaClass, result?.javaClass)
  }

  @Test
  fun `WHEN cropping is cancelled by user, THEN result should be cancelled`() {
    // GIVEN
    val expected = CropImage.CancelledResult
    var fragment: ContractTestFragment? = null
    val testRegistry = object : ActivityResultRegistry() {
      override fun <I, O> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?,
      ) {
        dispatchResult(requestCode, Activity.RESULT_CANCELED, Intent())
      }
    }

    with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
      onFragment { fragment = it }
    }
    // WHEN
    fragment?.cropImage(CropImageContractOptions(null, CropImageOptions()))
    // THEN
    assertEquals(expected, fragment?.cropResult)
  }

  @Test
  fun `WHEN cropping succeeds, THEN result should be successful`() {
    // GIVEN
    var fragment: ContractTestFragment? = null
    val expected = CropImage.ActivityResult(
      originalUri = "content://original".toUri(),
      uriContent = "content://content".toUri(),
      error = null,
      cropPoints = floatArrayOf(),
      cropRect = Rect(1, 2, 3, 4),
      rotation = 45,
      wholeImageRect = Rect(10, 20, 0, 0),
      sampleSize = 0,
    )
    val testRegistry = object : ActivityResultRegistry() {
      override fun <I, O> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?,
      ) {
        val intent = Intent()
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, expected)

        dispatchResult(requestCode, CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE, intent)
      }
    }

    with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
      onFragment { fragment = it }
    }
    // WHEN
    fragment?.cropImage(CropImageContractOptions(null, CropImageOptions()))
    // THEN
    assertEquals(expected, fragment?.cropResult)
  }

  @Test
  fun `WHEN starting crop with all options, THEN intent should contain these options`() {
    // GIVEN
    var cropImageIntent: Intent? = null
    val expectedClassName = CropImageActivity::class.java.name
    val expectedSource = "file://testInput".toUri()
    val options = CropImageContractOptions(
      expectedSource,
      CropImageOptions(
        cropShape = CropImageView.CropShape.OVAL,
        snapRadius = 1f,
        touchRadius = 2f,
        guidelines = CropImageView.Guidelines.ON_TOUCH,
        scaleType = CropImageView.ScaleType.CENTER,
        showCropOverlay = true,
        autoZoomEnabled = false,
        multiTouchEnabled = true,
        centerMoveEnabled = false,
        maxZoom = 17,
        initialCropWindowPaddingRatio = 0.2f,
        fixAspectRatio = true,
        aspectRatioX = 3,
        aspectRatioY = 4,
        borderLineThickness = 3f,
        borderLineColor = Color.GREEN,
        borderCornerThickness = 5f,
        borderCornerOffset = 6f,
        borderCornerLength = 7f,
        borderCornerColor = Color.MAGENTA,
        guidelinesThickness = 8f,
        guidelinesColor = Color.RED,
        backgroundColor = Color.BLUE,
        minCropWindowWidth = 5,
        minCropWindowHeight = 5,
        minCropResultWidth = 10,
        minCropResultHeight = 10,
        maxCropResultWidth = 5000,
        maxCropResultHeight = 5000,
        activityTitle = "Test Activity Title",
        activityMenuIconColor = Color.BLACK,
        customOutputUri = null,
        outputCompressFormat = Bitmap.CompressFormat.JPEG,
        outputCompressQuality = 85,
        outputRequestWidth = 25,
        outputRequestHeight = 30,
        outputRequestSizeOptions = CropImageView.RequestSizeOptions.NONE,
        noOutputImage = false,
        initialCropWindowRectangle = Rect(4, 5, 6, 7),
        initialRotation = 13,
        allowRotation = true,
        allowFlipping = false,
        allowCounterRotation = true,
        rotationDegrees = 4,
        flipHorizontally = true,
        flipVertically = false,
        cropMenuCropButtonTitle = "Test Button Title",
        cropMenuCropButtonIcon = R.drawable.ic_rotate_left_24,
      ),
    )

    val testRegistry = object : ActivityResultRegistry() {
      override fun <I, O> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?,
      ) {
      }
    }
    // WHEN
    with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
      onFragment { fragment -> cropImageIntent = fragment.cropImageIntent(options) }
    }

    val bundle = cropImageIntent?.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
    // THEN
    assertEquals(expectedClassName, cropImageIntent?.component?.className)
    assertEquals(expectedSource, bundle?.parcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE))
  }

  @Test
  fun `WHEN cropping fails, THEN result should be unsuccessful`() {
    // GIVEN
    var fragment: ContractTestFragment? = null
    val testRegistry = object : ActivityResultRegistry() {
      override fun <I, O> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?,
      ) {
        val result = CropImage.ActivityResult(
          originalUri = null,
          uriContent = null,
          error = Exception("Error!"),
          cropPoints = floatArrayOf(),
          cropRect = null,
          rotation = 0,
          wholeImageRect = null,
          sampleSize = 0,
        )
        val intent = Intent()
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result)

        dispatchResult(requestCode, CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE, intent)
      }
    }

    with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
      onFragment { fragment = it }
    }
    // WHEN
    fragment?.cropImage(CropImageContractOptions(null, CropImageOptions()))
    // THEN
    assertEquals(false, fragment?.cropResult?.isSuccessful)
  }
}
