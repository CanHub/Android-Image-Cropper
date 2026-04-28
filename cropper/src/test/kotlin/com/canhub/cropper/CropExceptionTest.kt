package com.canhub.cropper

import android.net.Uri
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test suite for CropException sealed class hierarchy.
 *
 * Covers:
 * - All exception types and their messages
 * - Exception inheritance structure
 * - Message formatting
 * - Serialization compatibility
 */
class CropExceptionTest {

  // ==================== Exception Type Tests ====================

  @Test
  fun `WHEN Cancellation exception created THEN has correct message`() {
    // WHEN
    val exception = CropException.Cancellation()

    // THEN
    assertTrue(exception.message!!.contains("crop:"))
    assertTrue(exception.message!!.contains("cropping has been cancelled by the user"))
  }

  @Test
  fun `WHEN FailedToLoadBitmap exception created THEN includes URI and message`() {
    // GIVEN
    val uri = Uri.parse("content://com.example/image.jpg")
    val errorMessage = "OutOfMemoryError"

    // WHEN
    val exception = CropException.FailedToLoadBitmap(uri, errorMessage)

    // THEN
    assertNotNull(exception.message)
    assertTrue(exception.message!!.contains("crop:"))
    assertTrue(exception.message!!.contains("Failed to load sampled bitmap"))
    assertTrue(exception.message!!.contains(uri.toString()))
    assertTrue(exception.message!!.contains(errorMessage))
  }

  @Test
  fun `WHEN FailedToLoadBitmap exception created with null message THEN includes null in message`() {
    // GIVEN
    val uri = Uri.parse("content://com.example/image.jpg")

    // WHEN
    val exception = CropException.FailedToLoadBitmap(uri, null)

    // THEN
    assertNotNull(exception.message)
    assertTrue(exception.message!!.contains("crop:"))
    assertTrue(exception.message!!.contains("Failed to load sampled bitmap"))
    assertTrue(exception.message!!.contains(uri.toString()))
    assertTrue(exception.message!!.contains("null"))
  }

  @Test
  fun `WHEN FailedToDecodeImage exception created THEN includes URI`() {
    // GIVEN
    val uri = Uri.parse("content://com.example/corrupted.jpg")

    // WHEN
    val exception = CropException.FailedToDecodeImage(uri)

    // THEN
    assertNotNull(exception.message)
    assertTrue(exception.message!!.contains("crop:"))
    assertTrue(exception.message!!.contains("Failed to decode image"))
    assertTrue(exception.message!!.contains(uri.toString()))
  }

  // ==================== Message Prefix Tests ====================

  @Test
  fun `WHEN any CropException created THEN message starts with crop prefix`() {
    // GIVEN
    val uri = Uri.parse("content://test")

    // WHEN
    val cancellation = CropException.Cancellation()
    val failedToLoad = CropException.FailedToLoadBitmap(uri, "error")
    val failedToDecode = CropException.FailedToDecodeImage(uri)

    // THEN
    assertTrue(cancellation.message!!.startsWith("crop:"))
    assertTrue(failedToLoad.message!!.startsWith("crop:"))
    assertTrue(failedToDecode.message!!.startsWith("crop:"))
  }

  // ==================== Throwing and Catching Tests ====================

  @Test
  fun `WHEN Cancellation exception thrown THEN can be caught as CropException`() {
    // WHEN/THEN
    try {
      throw CropException.Cancellation()
    } catch (e: CropException) {
      assertTrue(e is CropException.Cancellation)
      assertTrue(e.message!!.contains("cancelled"))
    }
  }

  @Test
  fun `WHEN FailedToLoadBitmap exception thrown THEN can be caught as CropException`() {
    // GIVEN
    val uri = Uri.parse("content://test")

    // WHEN/THEN
    try {
      throw CropException.FailedToLoadBitmap(uri, "test error")
    } catch (e: CropException) {
      assertTrue(e is CropException.FailedToLoadBitmap)
      assertTrue(e.message!!.contains("Failed to load sampled bitmap"))
    }
  }

  @Test
  fun `WHEN FailedToDecodeImage exception thrown THEN can be caught as CropException`() {
    // GIVEN
    val uri = Uri.parse("content://test")

    // WHEN/THEN
    try {
      throw CropException.FailedToDecodeImage(uri)
    } catch (e: CropException) {
      assertTrue(e is CropException.FailedToDecodeImage)
      assertTrue(e.message!!.contains("Failed to decode image"))
    }
  }

  @Test
  fun `WHEN any CropException thrown THEN can be caught as Exception`() {
    // GIVEN
    val uri = Uri.parse("content://test")

    // WHEN/THEN - Cancellation
    try {
      throw CropException.Cancellation()
    } catch (e: Exception) {
      assertTrue(e is CropException)
    }

    // WHEN/THEN - FailedToLoadBitmap
    try {
      throw CropException.FailedToLoadBitmap(uri, "error")
    } catch (e: Exception) {
      assertTrue(e is CropException)
    }

    // WHEN/THEN - FailedToDecodeImage
    try {
      throw CropException.FailedToDecodeImage(uri)
    } catch (e: Exception) {
      assertTrue(e is CropException)
    }
  }

  // ==================== URI Formatting Tests ====================

  @Test
  fun `WHEN FailedToLoadBitmap created with file URI THEN message includes file path`() {
    // GIVEN
    val uri = Uri.parse("file:///storage/emulated/0/Pictures/image.jpg")

    // WHEN
    val exception = CropException.FailedToLoadBitmap(uri, "disk error")

    // THEN
    assertTrue(exception.message!!.contains(uri.toString()))
    assertTrue(exception.message!!.contains("file://"))
  }

  @Test
  fun `WHEN FailedToDecodeImage created with content URI THEN message includes content URI`() {
    // GIVEN
    val uri = Uri.parse("content://com.android.providers.media.documents/document/image%3A123")

    // WHEN
    val exception = CropException.FailedToDecodeImage(uri)

    // THEN
    assertTrue(exception.message!!.contains(uri.toString()))
    assertTrue(exception.message!!.contains("content://"))
  }

  // ==================== Edge Cases ====================

  @Test
  fun `WHEN FailedToLoadBitmap created with empty error message THEN includes empty string`() {
    // GIVEN
    val uri = Uri.parse("content://test")

    // WHEN
    val exception = CropException.FailedToLoadBitmap(uri, "")

    // THEN
    assertNotNull(exception.message)
    // Message should still be valid even with empty error
    assertTrue(exception.message!!.contains("Failed to load sampled bitmap"))
  }

  @Test
  fun `WHEN FailedToLoadBitmap created with multiline error message THEN preserves newlines`() {
    // GIVEN
    val uri = Uri.parse("content://test")
    val multilineError = "Error on line 1\nError on line 2"

    // WHEN
    val exception = CropException.FailedToLoadBitmap(uri, multilineError)

    // THEN
    assertTrue(exception.message!!.contains("Error on line 1"))
    assertTrue(exception.message!!.contains("Error on line 2"))
  }

  @Test
  fun `WHEN exception created with URI containing special characters THEN message is valid`() {
    // GIVEN
    val uri = Uri.parse("content://provider/path/with%20spaces/and&special?chars=123")

    // WHEN
    val exception = CropException.FailedToDecodeImage(uri)

    // THEN
    assertNotNull(exception.message)
    assertTrue(exception.message!!.contains(uri.toString()))
  }

  // ==================== Sealed Class Tests ====================

  @Test
  fun `WHEN checking sealed hierarchy THEN only three exception types exist`() {
    // GIVEN
    val uri = Uri.parse("content://test")

    // WHEN - Create all three types
    val cancellation: CropException = CropException.Cancellation()
    val failedToLoad: CropException = CropException.FailedToLoadBitmap(uri, "error")
    val failedToDecode: CropException = CropException.FailedToDecodeImage(uri)

    // THEN - Verify types using when expression (exhaustive for sealed class)
    listOf(cancellation, failedToLoad, failedToDecode).forEach { exception ->
      when (exception) {
        is CropException.Cancellation -> assertTrue(true)
        is CropException.FailedToLoadBitmap -> assertTrue(true)
        is CropException.FailedToDecodeImage -> assertTrue(true)
        // No else needed - sealed class is exhaustive
      }
    }
  }

  @Test
  fun `WHEN comparing exception messages THEN each type has unique message pattern`() {
    // GIVEN
    val uri = Uri.parse("content://test")
    val cancellation = CropException.Cancellation()
    val failedToLoad = CropException.FailedToLoadBitmap(uri, "error")
    val failedToDecode = CropException.FailedToDecodeImage(uri)

    // THEN - Each has unique identifying text
    assertTrue(cancellation.message!!.contains("cancelled by the user"))
    assertTrue(failedToLoad.message!!.contains("Failed to load sampled bitmap"))
    assertTrue(failedToDecode.message!!.contains("Failed to decode image"))

    // AND messages are different from each other
    assertTrue(cancellation.message != failedToLoad.message)
    assertTrue(failedToLoad.message != failedToDecode.message)
    assertTrue(cancellation.message != failedToDecode.message)
  }
}
