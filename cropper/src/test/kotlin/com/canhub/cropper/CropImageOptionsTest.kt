package com.canhub.cropper

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test suite for CropImageOptions validation and configuration.
 *
 * Covers:
 * - Init block validation (all IllegalArgumentException cases)
 * - Boundary value testing
 * - Default values
 * - Parcelable serialization/deserialization
 * - Copy functionality
 */
@RunWith(RobolectricTestRunner::class)
class CropImageOptionsTest {

  // ==================== Validation Success Tests ====================

  @Test
  fun `WHEN default constructor called THEN creates valid options with defaults`() {
    // WHEN
    val options = CropImageOptions()

    // THEN - All defaults are valid
    assertNotNull(options)
    assertEquals(true, options.imageSourceIncludeGallery)
    assertEquals(true, options.imageSourceIncludeCamera)
    assertEquals(CropImageView.CropShape.RECTANGLE, options.cropShape)
    assertEquals(CropImageView.Guidelines.ON, options.guidelines)
    assertEquals(4, options.maxZoom)
    assertEquals(0.0f, options.initialCropWindowPaddingRatio)
    assertEquals(false, options.fixAspectRatio)
    assertEquals(1, options.aspectRatioX)
    assertEquals(1, options.aspectRatioY)
    assertEquals(90, options.rotationDegrees)
  }

  @Test
  fun `WHEN all valid boundary values provided THEN creates valid options`() {
    // GIVEN
    val validOptions = CropImageOptions(
      maxZoom = 0, // valid minimum
      initialCropWindowPaddingRatio = 0.49f, // valid maximum (< 0.5)
      aspectRatioX = 1, // valid minimum
      aspectRatioY = 1, // valid minimum
      rotationDegrees = 360, // valid maximum
      minCropResultWidth = 10,
      maxCropResultWidth = 1000, // >= minCropResultWidth
      minCropResultHeight = 10,
      maxCropResultHeight = 1000, // >= minCropResultHeight
    )

    // THEN
    assertNotNull(validOptions)
    assertEquals(0, validOptions.maxZoom)
    assertEquals(0.49f, validOptions.initialCropWindowPaddingRatio)
    assertEquals(360, validOptions.rotationDegrees)
  }

  @Test
  fun `WHEN copy() called THEN creates new instance with same values`() {
    // GIVEN
    val original = CropImageOptions(
      maxZoom = 8,
      fixAspectRatio = true,
      aspectRatioX = 16,
      aspectRatioY = 9,
      rotationDegrees = 270,
    )

    // WHEN
    val copy = original.copy(maxZoom = 10) // Change one value

    // THEN
    assertEquals(10, copy.maxZoom) // Changed value
    assertEquals(true, copy.fixAspectRatio) // Preserved
    assertEquals(16, copy.aspectRatioX) // Preserved
    assertEquals(9, copy.aspectRatioY) // Preserved
    assertEquals(270, copy.rotationDegrees) // Preserved
  }

  // ==================== Validation Failure Tests ====================

  @Test
  fun `WHEN maxZoom is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(maxZoom = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set max zoom"))
  }

  @Test
  fun `WHEN touchRadius is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(touchRadius = -1f)
    }
    assertTrue(exception.message!!.contains("Cannot set touch radius"))
  }

  @Test
  fun `WHEN initialCropWindowPaddingRatio is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(initialCropWindowPaddingRatio = -0.1f)
    }
    assertTrue(exception.message!!.contains("Cannot set initial crop window padding"))
  }

  @Test
  fun `WHEN initialCropWindowPaddingRatio is 0_5 THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(initialCropWindowPaddingRatio = 0.5f)
    }
    assertTrue(exception.message!!.contains("Cannot set initial crop window padding"))
  }

  @Test
  fun `WHEN initialCropWindowPaddingRatio is greater than 0_5 THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(initialCropWindowPaddingRatio = 0.6f)
    }
    assertTrue(exception.message!!.contains("Cannot set initial crop window padding"))
  }

  @Test
  fun `WHEN aspectRatioX is zero THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(aspectRatioX = 0)
    }
    assertTrue(exception.message!!.contains("Cannot set aspect ratio value"))
  }

  @Test
  fun `WHEN aspectRatioX is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(aspectRatioX = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set aspect ratio value"))
  }

  @Test
  fun `WHEN aspectRatioY is zero THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(aspectRatioY = 0)
    }
    assertTrue(exception.message!!.contains("Cannot set aspect ratio value"))
  }

  @Test
  fun `WHEN aspectRatioY is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(aspectRatioY = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set aspect ratio value"))
  }

  @Test
  fun `WHEN borderLineThickness is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(borderLineThickness = -1f)
    }
    assertTrue(exception.message!!.contains("Cannot set line thickness"))
  }

  @Test
  fun `WHEN borderCornerThickness is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(borderCornerThickness = -1f)
    }
    assertTrue(exception.message!!.contains("Cannot set corner thickness"))
  }

  @Test
  fun `WHEN guidelinesThickness is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(guidelinesThickness = -1f)
    }
    assertTrue(exception.message!!.contains("Cannot set guidelines thickness"))
  }

  @Test
  fun `WHEN minCropWindowHeight is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(minCropWindowHeight = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set min crop window height"))
  }

  @Test
  fun `WHEN minCropResultWidth is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(minCropResultWidth = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set min crop result width"))
  }

  @Test
  fun `WHEN minCropResultHeight is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(minCropResultHeight = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set min crop result height"))
  }

  @Test
  fun `WHEN maxCropResultWidth is less than minCropResultWidth THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(
        minCropResultWidth = 100,
        maxCropResultWidth = 50,
      )
    }
    assertTrue(exception.message!!.contains("Cannot set max crop result width to smaller value than min"))
  }

  @Test
  fun `WHEN maxCropResultHeight is less than minCropResultHeight THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(
        minCropResultHeight = 100,
        maxCropResultHeight = 50,
      )
    }
    assertTrue(exception.message!!.contains("Cannot set max crop result height to smaller value than min"))
  }

  @Test
  fun `WHEN outputRequestWidth is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(outputRequestWidth = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set request width"))
  }

  @Test
  fun `WHEN outputRequestHeight is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(outputRequestHeight = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set request height"))
  }

  @Test
  fun `WHEN rotationDegrees is negative THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(rotationDegrees = -1)
    }
    assertTrue(exception.message!!.contains("Cannot set rotation degrees"))
  }

  @Test
  fun `WHEN rotationDegrees is greater than 360 THEN throws IllegalArgumentException`() {
    // WHEN/THEN
    val exception = assertThrows(IllegalArgumentException::class.java) {
      CropImageOptions(rotationDegrees = 361)
    }
    assertTrue(exception.message!!.contains("Cannot set rotation degrees"))
  }

  // ==================== Boundary Value Tests ====================

  @Test
  fun `WHEN maxZoom is 0 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(maxZoom = 0)

    // THEN
    assertEquals(0, options.maxZoom)
  }

  @Test
  fun `WHEN touchRadius is 0 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(touchRadius = 0f)

    // THEN
    assertEquals(0f, options.touchRadius)
  }

  @Test
  fun `WHEN initialCropWindowPaddingRatio is 0 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(initialCropWindowPaddingRatio = 0f)

    // THEN
    assertEquals(0f, options.initialCropWindowPaddingRatio)
  }

  @Test
  fun `WHEN initialCropWindowPaddingRatio is 0_49 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(initialCropWindowPaddingRatio = 0.49f)

    // THEN
    assertEquals(0.49f, options.initialCropWindowPaddingRatio)
  }

  @Test
  fun `WHEN aspectRatioX is 1 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(aspectRatioX = 1)

    // THEN
    assertEquals(1, options.aspectRatioX)
  }

  @Test
  fun `WHEN aspectRatioY is 1 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(aspectRatioY = 1)

    // THEN
    assertEquals(1, options.aspectRatioY)
  }

  @Test
  fun `WHEN rotationDegrees is 0 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(rotationDegrees = 0)

    // THEN
    assertEquals(0, options.rotationDegrees)
  }

  @Test
  fun `WHEN rotationDegrees is 360 THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(rotationDegrees = 360)

    // THEN
    assertEquals(360, options.rotationDegrees)
  }

  @Test
  fun `WHEN maxCropResultWidth equals minCropResultWidth THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(
      minCropResultWidth = 100,
      maxCropResultWidth = 100,
    )

    // THEN
    assertEquals(100, options.minCropResultWidth)
    assertEquals(100, options.maxCropResultWidth)
  }

  @Test
  fun `WHEN maxCropResultHeight equals minCropResultHeight THEN creates valid options`() {
    // WHEN
    val options = CropImageOptions(
      minCropResultHeight = 100,
      maxCropResultHeight = 100,
    )

    // THEN
    assertEquals(100, options.minCropResultHeight)
    assertEquals(100, options.maxCropResultHeight)
  }

  // ==================== Parcelable Tests ====================

  @Test
  fun `WHEN options parceled and unparceled THEN all fields preserved`() {
    // GIVEN
    val original = CropImageOptions(
      imageSourceIncludeGallery = false,
      imageSourceIncludeCamera = true,
      cropShape = CropImageView.CropShape.OVAL,
      cornerShape = CropImageView.CropCornerShape.OVAL,
      guidelines = CropImageView.Guidelines.ON_TOUCH,
      scaleType = CropImageView.ScaleType.CENTER,
      showCropOverlay = false,
      showProgressBar = false,
      autoZoomEnabled = false,
      multiTouchEnabled = true,
      maxZoom = 8,
      initialCropWindowPaddingRatio = 0.1f,
      fixAspectRatio = true,
      aspectRatioX = 16,
      aspectRatioY = 9,
      minCropResultWidth = 200,
      maxCropResultWidth = 2000,
      minCropResultHeight = 150,
      maxCropResultHeight = 1500,
      customOutputUri = Uri.parse("content://test"),
      outputCompressFormat = Bitmap.CompressFormat.PNG,
      outputCompressQuality = 80,
      outputRequestWidth = 1920,
      outputRequestHeight = 1080,
      outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_EXACT,
      noOutputImage = true,
      initialCropWindowRectangle = Rect(10, 20, 100, 200),
      initialRotation = 90,
      allowRotation = false,
      allowFlipping = false,
      rotationDegrees = 45,
      flipHorizontally = true,
      flipVertically = true,
      activityTitle = "Crop Image",
      activityMenuIconColor = Color.RED,
      progressBarColor = Color.BLUE,
      backgroundColor = Color.BLACK,
    )

    // WHEN - Use Bundle to test Parcelable (proper way with @Parcelize)
    val bundle = Bundle()
    bundle.putParcelable("options", original)
    val restored = bundle.parcelable<CropImageOptions>("options")!!

    // THEN - Verify all critical fields
    assertEquals(original.imageSourceIncludeGallery, restored.imageSourceIncludeGallery)
    assertEquals(original.imageSourceIncludeCamera, restored.imageSourceIncludeCamera)
    assertEquals(original.cropShape, restored.cropShape)
    assertEquals(original.cornerShape, restored.cornerShape)
    assertEquals(original.guidelines, restored.guidelines)
    assertEquals(original.scaleType, restored.scaleType)
    assertEquals(original.showCropOverlay, restored.showCropOverlay)
    assertEquals(original.showProgressBar, restored.showProgressBar)
    assertEquals(original.autoZoomEnabled, restored.autoZoomEnabled)
    assertEquals(original.multiTouchEnabled, restored.multiTouchEnabled)
    assertEquals(original.maxZoom, restored.maxZoom)
    assertEquals(original.initialCropWindowPaddingRatio, restored.initialCropWindowPaddingRatio)
    assertEquals(original.fixAspectRatio, restored.fixAspectRatio)
    assertEquals(original.aspectRatioX, restored.aspectRatioX)
    assertEquals(original.aspectRatioY, restored.aspectRatioY)
    assertEquals(original.minCropResultWidth, restored.minCropResultWidth)
    assertEquals(original.maxCropResultWidth, restored.maxCropResultWidth)
    assertEquals(original.minCropResultHeight, restored.minCropResultHeight)
    assertEquals(original.maxCropResultHeight, restored.maxCropResultHeight)
    assertEquals(original.customOutputUri, restored.customOutputUri)
    assertEquals(original.outputCompressFormat, restored.outputCompressFormat)
    assertEquals(original.outputCompressQuality, restored.outputCompressQuality)
    assertEquals(original.outputRequestWidth, restored.outputRequestWidth)
    assertEquals(original.outputRequestHeight, restored.outputRequestHeight)
    assertEquals(original.outputRequestSizeOptions, restored.outputRequestSizeOptions)
    assertEquals(original.noOutputImage, restored.noOutputImage)
    assertEquals(original.initialCropWindowRectangle, restored.initialCropWindowRectangle)
    assertEquals(original.initialRotation, restored.initialRotation)
    assertEquals(original.allowRotation, restored.allowRotation)
    assertEquals(original.allowFlipping, restored.allowFlipping)
    assertEquals(original.rotationDegrees, restored.rotationDegrees)
    assertEquals(original.flipHorizontally, restored.flipHorizontally)
    assertEquals(original.flipVertically, restored.flipVertically)
    assertEquals(original.activityTitle, restored.activityTitle)
    assertEquals(original.activityMenuIconColor, restored.activityMenuIconColor)
    assertEquals(original.progressBarColor, restored.progressBarColor)
    assertEquals(original.backgroundColor, restored.backgroundColor)
  }

  @Test
  fun `WHEN options with null customOutputUri parceled THEN unparceled correctly`() {
    // GIVEN
    val original = CropImageOptions(customOutputUri = null)

    // WHEN - Use Bundle to test Parcelable
    val bundle = Bundle()
    bundle.putParcelable("options", original)
    val restored = bundle.parcelable<CropImageOptions>("options")!!

    // THEN
    assertNull(restored.customOutputUri)
  }

  @Test
  fun `WHEN options with null initialCropWindowRectangle parceled THEN unparceled correctly`() {
    // GIVEN
    val original = CropImageOptions(initialCropWindowRectangle = null)

    // WHEN - Use Bundle to test Parcelable
    val bundle = Bundle()
    bundle.putParcelable("options", original)
    val restored = bundle.parcelable<CropImageOptions>("options")!!

    // THEN
    assertNull(restored.initialCropWindowRectangle)
  }

  // ==================== Configuration Scenarios ====================

  @Test
  fun `WHEN creating square crop options THEN aspect ratio is 1 to 1`() {
    // WHEN
    val options = CropImageOptions(
      fixAspectRatio = true,
      aspectRatioX = 1,
      aspectRatioY = 1,
    )

    // THEN
    assertEquals(true, options.fixAspectRatio)
    assertEquals(1, options.aspectRatioX)
    assertEquals(1, options.aspectRatioY)
  }

  @Test
  fun `WHEN creating 16 by 9 crop options THEN aspect ratio is correct`() {
    // WHEN
    val options = CropImageOptions(
      fixAspectRatio = true,
      aspectRatioX = 16,
      aspectRatioY = 9,
    )

    // THEN
    assertEquals(true, options.fixAspectRatio)
    assertEquals(16, options.aspectRatioX)
    assertEquals(9, options.aspectRatioY)
  }

  @Test
  fun `WHEN creating oval crop options THEN crop shape is oval`() {
    // WHEN
    val options = CropImageOptions(
      cropShape = CropImageView.CropShape.OVAL,
    )

    // THEN
    assertEquals(CropImageView.CropShape.OVAL, options.cropShape)
  }

  @Test
  fun `WHEN creating options with custom output URI THEN URI is set`() {
    // GIVEN
    val customUri = Uri.parse("content://com.example/output.jpg")

    // WHEN
    val options = CropImageOptions(customOutputUri = customUri)

    // THEN
    assertEquals(customUri, options.customOutputUri)
  }

  @Test
  fun `WHEN creating options with PNG compression THEN format is PNG`() {
    // WHEN
    val options = CropImageOptions(
      outputCompressFormat = Bitmap.CompressFormat.PNG,
      outputCompressQuality = 100,
    )

    // THEN
    assertEquals(Bitmap.CompressFormat.PNG, options.outputCompressFormat)
    assertEquals(100, options.outputCompressQuality)
  }
}
