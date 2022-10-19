package com.canhub.cropper.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.getDefault

/**
 * This class will create a temporary file in the cache if need.
 *
 * When the uri already have `file://` schema we don't need to create a new file.
 * The temporary file will always override a previous one, saving memory.
 * Using the cache memory(context.cacheDir) we guarantee to not leak memory
 *
 * @param context used to access Android APIs, like content resolve, it is your activity/fragment.
 * @param uri the URI to load the image from.
 * @param uniqueName If true, make each image cropped have a different file name, this could cause
 * memory issues, use wisely.
 *
 * @return string value of the File path.
 */
internal fun getFilePathFromUri(context: Context, uri: Uri, uniqueName: Boolean): String =
  if (uri.path?.contains("file://") == true) {
    uri.path!!
  } else {
    getFileFromContentUri(context, uri, uniqueName).path
  }

private fun getFileFromContentUri(context: Context, contentUri: Uri, uniqueName: Boolean): File {
  // Preparing Temp file name
  val fileExtension = getFileExtension(context, contentUri) ?: ""
  val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", getDefault()).format(Date())
  val fileName = ("temp_file_" + if (uniqueName) timeStamp else "") + ".$fileExtension"
  // Creating Temp file
  val tempFile = File(context.cacheDir, fileName)
  tempFile.createNewFile()
  // Initialize streams
  var oStream: FileOutputStream? = null
  var inputStream: InputStream? = null

  try {
    oStream = FileOutputStream(tempFile)
    inputStream = context.contentResolver.openInputStream(contentUri)

    inputStream?.let { copy(inputStream, oStream) }
    oStream.flush()
  } catch (e: Exception) {
    e.printStackTrace()
  } finally {
    // Close streams
    inputStream?.close()
    oStream?.close()
  }

  return tempFile
}

private fun getFileExtension(context: Context, uri: Uri): String? =
  if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
    MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
  } else {
    uri.path?.let { MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString()) }
  }

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
  val buf = ByteArray(8192)
  var length: Int
  while (source.read(buf).also { length = it } > 0) {
    target.write(buf, 0, length)
  }
}
