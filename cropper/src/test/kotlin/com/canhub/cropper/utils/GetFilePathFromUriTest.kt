package com.canhub.cropper.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException

/**
 * Test suite for GetFilePathFromUri utility function.
 *
 * Covers:
 * - Path validation and security (path traversal attacks)
 * - Temp file management (unique/non-unique names)
 * - Stream handling and error recovery
 * - MIME type extension extraction
 * - Edge cases and malicious inputs
 */
@RunWith(RobolectricTestRunner::class)
class GetFilePathFromUriTest {

  private lateinit var context: Context
  private lateinit var contentResolver: ContentResolver
  private lateinit var realContext: Context

  @Before
  fun setup() {
    realContext = RuntimeEnvironment.getApplication()
    context = mockk(relaxed = true)
    contentResolver = mockk(relaxed = true)

    // Mock context to return our mock contentResolver and real cacheDir
    every { context.contentResolver } returns contentResolver
    every { context.cacheDir } returns realContext.cacheDir

    // Mock MimeTypeMap for extension extraction
    mockkStatic(MimeTypeMap::class)
    val mimeTypeMap = mockk<MimeTypeMap>(relaxed = true)
    every { MimeTypeMap.getSingleton() } returns mimeTypeMap
    // Return appropriate extension based on MIME type
    every { mimeTypeMap.getExtensionFromMimeType("image/jpeg") } returns "jpg"
    every { mimeTypeMap.getExtensionFromMimeType("image/png") } returns "png"
    every { mimeTypeMap.getExtensionFromMimeType("image/webp") } returns "webp"
    every { mimeTypeMap.getExtensionFromMimeType(match { it != "image/jpeg" && it != "image/png" && it != "image/webp" }) } returns null
    every { MimeTypeMap.getFileExtensionFromUrl(any()) } returns "jpg"
  }

  @After
  fun teardown() {
    unmockkStatic(MimeTypeMap::class)
    // Clean up temp files created during tests
    realContext.cacheDir.listFiles()?.forEach { file ->
      if (file.name.startsWith("temp_file_")) {
        file.delete()
      }
    }
  }

  // ==================== File Path Extraction Tests ====================

  @Test
  fun `WHEN URI path contains file scheme string THEN return that path`() {
    // GIVEN
    val pathWithFileScheme = "/file:///storage/emulated/0/Pictures/image.jpg"
    val uri = mockk<Uri>()
    every { uri.path } returns pathWithFileScheme
    every { uri.scheme } returns ContentResolver.SCHEME_CONTENT

    // WHEN
    val result = getFilePathFromUri(context, uri, uniqueName = true)

    // THEN
    assertEquals(pathWithFileScheme, result)
  }

  @Suppress("Recycle") // False positive: verify block doesn't actually call openInputStream
  @Test
  fun `WHEN file URI path has no file scheme THEN create temp file from content`() {
    // GIVEN
    val contentUri = Uri.parse("content://com.example.provider/images/123")
    val testData = "test image data".toByteArray()
    val inputStream = ByteArrayInputStream(testData)

    every { contentResolver.openInputStream(contentUri) } returns inputStream
    every { contentResolver.getType(contentUri) } returns "image/jpeg"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN
    assertTrue(result.contains(context.cacheDir.path))
    assertTrue(result.endsWith(".jpg"))
    verify { contentResolver.openInputStream(contentUri) }
  }

  // ==================== Security Tests - Path Traversal ====================

  @Test
  fun `WHEN URI contains path traversal attempt THEN handle safely`() {
    // GIVEN - Malicious URI trying to escape to system directories
    val maliciousUri = Uri.parse("content://provider/../../../etc/passwd")
    val testData = "malicious".toByteArray()

    every { contentResolver.openInputStream(maliciousUri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(maliciousUri) } returns "text/plain"

    // WHEN
    val result = getFilePathFromUri(context, maliciousUri, uniqueName = true)

    // THEN - File should be created in cache directory, not system directory
    assertTrue(result.startsWith(context.cacheDir.path))
    val file = File(result)
    assertTrue(file.parentFile?.absolutePath == context.cacheDir.absolutePath)
  }

  @Test
  fun `WHEN URI contains encoded path traversal THEN handle safely`() {
    // GIVEN - Encoded path traversal attempt
    val maliciousUri = Uri.parse("content://provider/%2E%2E%2F%2E%2E%2Fpasswd")
    val testData = "encoded attack".toByteArray()

    every { contentResolver.openInputStream(maliciousUri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(maliciousUri) } returns "text/plain"

    // WHEN
    val result = getFilePathFromUri(context, maliciousUri, uniqueName = true)

    // THEN - Should create safe temp file in cache
    assertTrue(result.startsWith(context.cacheDir.path))
  }

  @Test
  fun `WHEN URI has null path THEN create temp file from content`() {
    // GIVEN
    val uri = mockk<Uri>()
    every { uri.path } returns null
    every { uri.scheme } returns ContentResolver.SCHEME_CONTENT

    val testData = "test data".toByteArray()
    every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(uri) } returns "image/png"

    // WHEN
    val result = getFilePathFromUri(context, uri, uniqueName = true)

    // THEN
    assertTrue(result.contains(context.cacheDir.path))
  }

  // ==================== Temp File Management Tests ====================

  @Test
  fun `WHEN uniqueName is true THEN generate unique timestamp-based filename`() {
    // GIVEN
    val contentUri = Uri.parse("content://com.example.provider/image1")
    val testData = "data1".toByteArray()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(contentUri) } returns "image/jpeg"

    // WHEN - Call twice with same URI
    val result1 = getFilePathFromUri(context, contentUri, uniqueName = true)
    Thread.sleep(1100) // Ensure different timestamp (format is yyyyMMdd_HHmmss, needs >1sec)
    val result2 = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN - Should generate different filenames
    assertNotEquals(result1, result2)
    assertTrue(result1.contains("temp_file_"))
    assertTrue(result2.contains("temp_file_"))
  }

  @Test
  fun `WHEN uniqueName is false THEN generate consistent filename`() {
    // GIVEN
    val contentUri = Uri.parse("content://com.example.provider/image2")
    val testData = "data2".toByteArray()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(contentUri) } returns "image/png"

    // WHEN - Call twice with uniqueName=false
    val result1 = getFilePathFromUri(context, contentUri, uniqueName = false)
    val result2 = getFilePathFromUri(context, contentUri, uniqueName = false)

    // THEN - Should generate same filename (temp_file_.png)
    val fileName1 = File(result1).name
    val fileName2 = File(result2).name
    assertEquals(fileName1, fileName2)
    assertEquals("temp_file_.png", fileName1)
  }

  @Test
  fun `WHEN temp file created THEN it exists in cache directory`() {
    // GIVEN
    val contentUri = Uri.parse("content://com.example.provider/test")
    val testData = "cache test".toByteArray()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(contentUri) } returns "image/jpeg"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN
    val file = File(result)
    assertTrue(file.exists())
    assertEquals(context.cacheDir, file.parentFile)
    assertTrue(file.readBytes().contentEquals(testData))
  }

  // ==================== File Extension Tests ====================

  @Test
  fun `WHEN content URI has JPEG MIME type THEN use jpg extension`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/image")
    val mimeTypeMap = MimeTypeMap.getSingleton()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream("data".toByteArray())
    every { contentResolver.getType(contentUri) } returns "image/jpeg"
    every { mimeTypeMap.getExtensionFromMimeType("image/jpeg") } returns "jpg"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = false)

    // THEN
    assertTrue(result.endsWith(".jpg"))
  }

  @Test
  fun `WHEN content URI has PNG MIME type THEN use png extension`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/image")
    val mimeTypeMap = MimeTypeMap.getSingleton()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream("data".toByteArray())
    every { contentResolver.getType(contentUri) } returns "image/png"
    every { mimeTypeMap.getExtensionFromMimeType("image/png") } returns "png"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = false)

    // THEN
    assertTrue(result.endsWith(".png"))
  }

  @Test
  fun `WHEN MIME type is unknown THEN use empty extension`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/unknown")
    val mimeTypeMap = MimeTypeMap.getSingleton()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream("data".toByteArray())
    every { contentResolver.getType(contentUri) } returns "application/octet-stream"
    every { mimeTypeMap.getExtensionFromMimeType("application/octet-stream") } returns null

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = false)

    // THEN
    assertTrue(result.endsWith("."))
  }

  @Test
  fun `WHEN URI path literally contains file colon slashes THEN return that path`() {
    // GIVEN - URI where the path itself contains the string "file://"
    val weirdUri = mockk<Uri>()
    every { weirdUri.path } returns "/file://storage/image.webp"
    every { weirdUri.scheme } returns ContentResolver.SCHEME_CONTENT

    // WHEN
    val result = getFilePathFromUri(context, weirdUri, uniqueName = true)

    // THEN - Function checks if path contains "file://", not if scheme is "file://"
    assertEquals("/file://storage/image.webp", result)
  }

  // ==================== Stream Handling Tests ====================

  @Test
  fun `WHEN stream copy succeeds THEN file contains correct data`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/data")
    val expectedData = "test image content with special chars: 测试数据".toByteArray()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream(expectedData)
    every { contentResolver.getType(contentUri) } returns "image/jpeg"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN
    val actualData = File(result).readBytes()
    assertTrue(actualData.contentEquals(expectedData))
  }

  @Test
  fun `WHEN input stream is null THEN create empty file`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/empty")

    every { contentResolver.openInputStream(contentUri) } returns null
    every { contentResolver.getType(contentUri) } returns "image/jpeg"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN - File created but empty
    val file = File(result)
    assertTrue(file.exists())
    assertEquals(0, file.length())
  }

  @Test
  fun `WHEN IOException occurs during copy THEN handle gracefully`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/error")
    val failingStream = mockk<ByteArrayInputStream>()

    every { contentResolver.openInputStream(contentUri) } returns failingStream
    every { contentResolver.getType(contentUri) } returns "image/jpeg"
    every { failingStream.read(any<ByteArray>()) } throws IOException("Read error")
    every { failingStream.close() } returns Unit

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN - File should exist (exception is caught and printed)
    val file = File(result)
    assertTrue(file.exists())
  }

  @Test
  fun `WHEN large file is copied THEN handle in chunks`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/large")
    // Create data larger than buffer size (8192 bytes)
    val largeData = ByteArray(20000) { it.toByte() }

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream(largeData)
    every { contentResolver.getType(contentUri) } returns "image/jpeg"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN
    val file = File(result)
    assertTrue(file.exists())
    assertEquals(largeData.size.toLong(), file.length())
    assertTrue(file.readBytes().contentEquals(largeData))
  }

  // ==================== Edge Cases ====================

  @Test
  fun `WHEN URI scheme is null THEN create temp file from content`() {
    // GIVEN
    val uri = mockk<Uri>()
    every { uri.path } returns "/some/path"
    every { uri.scheme } returns null

    val testData = "no scheme".toByteArray()
    every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(uri) } returns "image/png"

    // WHEN
    val result = getFilePathFromUri(context, uri, uniqueName = false)

    // THEN
    assertTrue(result.contains(context.cacheDir.path))
  }

  @Test
  fun `WHEN URI path is empty string THEN create temp file`() {
    // GIVEN
    val uri = Uri.parse("content://provider/")

    every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream("data".toByteArray())
    every { contentResolver.getType(uri) } returns "image/jpeg"

    // WHEN
    val result = getFilePathFromUri(context, uri, uniqueName = true)

    // THEN
    assertTrue(result.contains(context.cacheDir.path))
    assertTrue(File(result).exists())
  }

  @Test
  fun `WHEN URI contains special characters THEN handle correctly`() {
    // GIVEN
    val contentUri = Uri.parse("content://provider/image%20with%20spaces")
    val testData = "special chars".toByteArray()

    every { contentResolver.openInputStream(contentUri) } returns ByteArrayInputStream(testData)
    every { contentResolver.getType(contentUri) } returns "image/png"

    // WHEN
    val result = getFilePathFromUri(context, contentUri, uniqueName = true)

    // THEN
    val file = File(result)
    assertTrue(file.exists())
    assertTrue(file.readBytes().contentEquals(testData))
  }

  @Test
  fun `WHEN multiple files created THEN each has unique path`() {
    // GIVEN
    val uris = listOf(
      Uri.parse("content://provider/img1"),
      Uri.parse("content://provider/img2"),
      Uri.parse("content://provider/img3"),
    )

    uris.forEach { uri ->
      every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream("data".toByteArray())
      every { contentResolver.getType(uri) } returns "image/jpeg"
    }

    // WHEN
    val results = uris.mapIndexed { index, uri ->
      if (index > 0) Thread.sleep(1100) // Ensure different timestamps
      getFilePathFromUri(context, uri, uniqueName = true)
    }

    // THEN
    assertEquals(3, results.toSet().size) // All unique
    results.forEach { path ->
      assertTrue(File(path).exists())
    }
  }
}
