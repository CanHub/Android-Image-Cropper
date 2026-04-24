package com.canhub.cropper

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test suite for ParcelableUtils extension functions.
 *
 * Covers:
 * - Bundle.parcelable<T>() extraction
 * - Intent.parcelable<T>() extraction
 * - Type safety and safe casting
 * - Edge cases (null values, wrong types, non-existent keys)
 */
@RunWith(RobolectricTestRunner::class)
class ParcelableUtilsTest {

  // Test data class for Parcelable testing
  @Parcelize
  data class TestParcelable(
    val value: String,
    val number: Int,
  ) : Parcelable

  // ==================== Bundle.parcelable() Tests ====================

  @Test
  fun `WHEN bundle contains parcelable with key THEN parcelable extracted correctly`() {
    // GIVEN
    val bundle = Bundle()
    val testData = TestParcelable("test", 42)
    bundle.putParcelable("test_key", testData)

    // WHEN
    val result = bundle.parcelable<TestParcelable>("test_key")

    // THEN
    assertNotNull(result)
    assertEquals("test", result?.value)
    assertEquals(42, result?.number)
  }

  @Test
  fun `WHEN bundle contains CropImageOptions THEN extracted correctly`() {
    // GIVEN
    val bundle = Bundle()
    val options = CropImageOptions(maxZoom = 8, aspectRatioX = 16, aspectRatioY = 9)
    bundle.putParcelable("options", options)

    // WHEN
    val result = bundle.parcelable<CropImageOptions>("options")

    // THEN
    assertNotNull(result)
    assertEquals(8, result?.maxZoom)
    assertEquals(16, result?.aspectRatioX)
    assertEquals(9, result?.aspectRatioY)
  }

  @Test
  fun `WHEN bundle contains Android Rect THEN extracted correctly`() {
    // GIVEN
    val bundle = Bundle()
    val rect = Rect(10, 20, 100, 200)
    bundle.putParcelable("rect", rect)

    // WHEN
    val result = bundle.parcelable<Rect>("rect")

    // THEN
    assertNotNull(result)
    assertEquals(10, result?.left)
    assertEquals(20, result?.top)
    assertEquals(100, result?.right)
    assertEquals(200, result?.bottom)
  }

  @Test
  fun `WHEN bundle contains Uri THEN extracted correctly`() {
    // GIVEN
    val bundle = Bundle()
    val uri = Uri.parse("content://com.example/image.jpg")
    bundle.putParcelable("uri", uri)

    // WHEN
    val result = bundle.parcelable<Uri>("uri")

    // THEN
    assertNotNull(result)
    assertEquals("content", result?.scheme)
    assertEquals("com.example", result?.authority)
  }

  @Test
  fun `WHEN bundle does not contain key THEN returns null`() {
    // GIVEN
    val bundle = Bundle()

    // WHEN
    val result = bundle.parcelable<TestParcelable>("non_existent_key")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN bundle contains different type THEN returns null due to safe cast`() {
    // GIVEN
    val bundle = Bundle()
    bundle.putString("string_key", "not a parcelable")

    // WHEN
    val result = bundle.parcelable<TestParcelable>("string_key")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN bundle contains null parcelable THEN returns null`() {
    // GIVEN
    val bundle = Bundle()
    bundle.putParcelable("null_key", null)

    // WHEN
    val result = bundle.parcelable<TestParcelable>("null_key")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN bundle contains wrong Parcelable type THEN safe cast returns null`() {
    // GIVEN
    val bundle = Bundle()
    val rect = Rect(0, 0, 10, 10)
    bundle.putParcelable("rect", rect)

    // WHEN - Try to extract as TestParcelable instead of Rect
    val result = bundle.parcelable<TestParcelable>("rect")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN bundle has multiple parcelables THEN each extracted with correct type`() {
    // GIVEN
    val bundle = Bundle()
    val testData = TestParcelable("data", 123)
    val uri = Uri.parse("content://test")
    val rect = Rect(1, 2, 3, 4)

    bundle.putParcelable("test", testData)
    bundle.putParcelable("uri", uri)
    bundle.putParcelable("rect", rect)

    // WHEN
    val testResult = bundle.parcelable<TestParcelable>("test")
    val uriResult = bundle.parcelable<Uri>("uri")
    val rectResult = bundle.parcelable<Rect>("rect")

    // THEN
    assertNotNull(testResult)
    assertEquals("data", testResult?.value)
    assertNotNull(uriResult)
    assertEquals("content", uriResult?.scheme)
    assertNotNull(rectResult)
    assertEquals(1, rectResult?.left)
  }

  // ==================== Intent.parcelable() Tests ====================

  @Test
  fun `WHEN intent contains parcelable extra THEN parcelable extracted correctly`() {
    // GIVEN
    val intent = Intent()
    val testData = TestParcelable("intent test", 99)
    intent.putExtra("test_extra", testData)

    // WHEN
    val result = intent.parcelable<TestParcelable>("test_extra")

    // THEN
    assertNotNull(result)
    assertEquals("intent test", result?.value)
    assertEquals(99, result?.number)
  }

  @Test
  fun `WHEN intent contains CropImageOptions extra THEN extracted correctly`() {
    // GIVEN
    val intent = Intent()
    val options = CropImageOptions(
      cropShape = CropImageView.CropShape.OVAL,
      fixAspectRatio = true,
      aspectRatioX = 4,
      aspectRatioY = 3,
    )
    intent.putExtra("crop_options", options)

    // WHEN
    val result = intent.parcelable<CropImageOptions>("crop_options")

    // THEN
    assertNotNull(result)
    assertEquals(CropImageView.CropShape.OVAL, result?.cropShape)
    assertEquals(true, result?.fixAspectRatio)
    assertEquals(4, result?.aspectRatioX)
    assertEquals(3, result?.aspectRatioY)
  }

  @Test
  fun `WHEN intent contains Uri extra THEN extracted correctly`() {
    // GIVEN
    val intent = Intent()
    val uri = Uri.parse("file:///storage/image.png")
    intent.putExtra("image_uri", uri)

    // WHEN
    val result = intent.parcelable<Uri>("image_uri")

    // THEN
    assertNotNull(result)
    assertEquals("file", result?.scheme)
    assertTrue(result?.path!!.contains("image.png"))
  }

  @Test
  fun `WHEN intent does not contain extra key THEN returns null`() {
    // GIVEN
    val intent = Intent()

    // WHEN
    val result = intent.parcelable<TestParcelable>("missing_extra")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN intent contains different type extra THEN returns null due to safe cast`() {
    // GIVEN
    val intent = Intent()
    intent.putExtra("string_extra", "not a parcelable")

    // WHEN
    val result = intent.parcelable<TestParcelable>("string_extra")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN intent contains null parcelable extra THEN returns null`() {
    // GIVEN
    val intent = Intent()
    intent.putExtra("null_extra", null as TestParcelable?)

    // WHEN
    val result = intent.parcelable<TestParcelable>("null_extra")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN intent contains wrong Parcelable type THEN safe cast returns null`() {
    // GIVEN
    val intent = Intent()
    val uri = Uri.parse("content://test")
    intent.putExtra("uri", uri)

    // WHEN - Try to extract as Rect instead of Uri
    val result = intent.parcelable<Rect>("uri")

    // THEN
    assertNull(result)
  }

  @Test
  fun `WHEN intent has multiple parcelable extras THEN each extracted with correct type`() {
    // GIVEN
    val intent = Intent()
    val testData = TestParcelable("multi", 456)
    val uri = Uri.parse("content://multi-test")
    val options = CropImageOptions(maxZoom = 10)

    intent.putExtra("test", testData)
    intent.putExtra("uri", uri)
    intent.putExtra("options", options)

    // WHEN
    val testResult = intent.parcelable<TestParcelable>("test")
    val uriResult = intent.parcelable<Uri>("uri")
    val optionsResult = intent.parcelable<CropImageOptions>("options")

    // THEN
    assertNotNull(testResult)
    assertEquals("multi", testResult?.value)
    assertNotNull(uriResult)
    assertEquals("content", uriResult?.scheme)
    assertNotNull(optionsResult)
    assertEquals(10, optionsResult?.maxZoom)
  }

  // ==================== Type Safety Tests ====================

  @Test
  fun `WHEN using reified type parameter THEN correct type extracted`() {
    // GIVEN
    val bundle = Bundle()
    val options = CropImageOptions(rotationDegrees = 180)
    bundle.putParcelable("options", options)

    // WHEN - Reified type parameter allows us to specify type
    val result = bundle.parcelable<CropImageOptions>("options")

    // THEN - Type is inferred correctly
    assertNotNull(result)
    assertEquals(180, result?.rotationDegrees)
    // No explicit cast needed due to reified type
  }

  @Test
  fun `WHEN safe cast fails THEN returns null instead of throwing ClassCastException`() {
    // GIVEN
    val bundle = Bundle()
    bundle.putParcelable("uri", Uri.parse("content://test"))

    // WHEN - Try to cast Uri to CropImageOptions
    val result = bundle.parcelable<CropImageOptions>("uri")

    // THEN - Returns null, doesn't throw exception
    assertNull(result)
  }

  @Test
  fun `WHEN parcelable extraction used in when expression THEN type narrowing works`() {
    // GIVEN
    val bundle = Bundle()
    bundle.putParcelable("data", TestParcelable("test", 1))

    // WHEN
    val result = bundle.parcelable<TestParcelable>("data")

    // THEN - Can use result in when with smart casting
    when (result) {
      null -> throw AssertionError("Should not be null")
      else -> {
        assertEquals("test", result.value)
        assertEquals(1, result.number)
      }
    }
  }

  // ==================== Edge Cases ====================

  @Test
  fun `WHEN empty bundle THEN all extractions return null`() {
    // GIVEN
    val bundle = Bundle()

    // WHEN/THEN
    assertNull(bundle.parcelable<TestParcelable>("any"))
    assertNull(bundle.parcelable<Uri>("any"))
    assertNull(bundle.parcelable<Rect>("any"))
    assertNull(bundle.parcelable<CropImageOptions>("any"))
  }

  @Test
  fun `WHEN empty intent THEN all extractions return null`() {
    // GIVEN
    val intent = Intent()

    // WHEN/THEN
    assertNull(intent.parcelable<TestParcelable>("any"))
    assertNull(intent.parcelable<Uri>("any"))
    assertNull(intent.parcelable<Rect>("any"))
    assertNull(intent.parcelable<CropImageOptions>("any"))
  }

  @Test
  fun `WHEN bundle key is empty string THEN extraction works`() {
    // GIVEN
    val bundle = Bundle()
    val testData = TestParcelable("empty key test", 0)
    bundle.putParcelable("", testData)

    // WHEN
    val result = bundle.parcelable<TestParcelable>("")

    // THEN
    assertNotNull(result)
    assertEquals("empty key test", result?.value)
  }

  @Test
  fun `WHEN intent extra key is empty string THEN extraction works`() {
    // GIVEN
    val intent = Intent()
    val uri = Uri.parse("content://empty-key")
    intent.putExtra("", uri)

    // WHEN
    val result = intent.parcelable<Uri>("")

    // THEN
    assertNotNull(result)
    assertEquals("content", result?.scheme)
  }

  @Test
  fun `WHEN bundle contains complex nested parcelable THEN extracted correctly`() {
    // GIVEN
    val bundle = Bundle()
    val options = CropImageOptions(
      customOutputUri = Uri.parse("content://output"),
      initialCropWindowRectangle = Rect(5, 10, 50, 100),
      aspectRatioX = 21,
      aspectRatioY = 9,
    )
    bundle.putParcelable("complex", options)

    // WHEN
    val result = bundle.parcelable<CropImageOptions>("complex")

    // THEN - Verify nested Parcelables are preserved
    assertNotNull(result)
    assertNotNull(result?.customOutputUri)
    assertEquals("content://output", result?.customOutputUri.toString())
    assertNotNull(result?.initialCropWindowRectangle)
    assertEquals(5, result?.initialCropWindowRectangle?.left)
    assertEquals(21, result?.aspectRatioX)
  }
}
