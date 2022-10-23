package com.canhub.cropper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

internal class BitmapCroppingWorkerJob(
  private val context: Context,
  private val cropImageViewReference: WeakReference<CropImageView>,
  private val uri: Uri?,
  private val bitmap: Bitmap?,
  private val cropPoints: FloatArray,
  private val degreesRotated: Int,
  private val orgWidth: Int,
  private val orgHeight: Int,
  private val fixAspectRatio: Boolean,
  private val aspectRatioX: Int,
  private val aspectRatioY: Int,
  private val reqWidth: Int,
  private val reqHeight: Int,
  private val flipHorizontally: Boolean,
  private val flipVertically: Boolean,
  private val options: CropImageView.RequestSizeOptions,
  private val saveCompressFormat: Bitmap.CompressFormat,
  private val saveCompressQuality: Int,
  private val customOutputUri: Uri?,
) : CoroutineScope {
  private var job: Job = Job()

  override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

  fun start() {
    job = launch(Dispatchers.Default) {
      try {
        if (isActive) {
          val bitmapSampled: BitmapUtils.BitmapSampled
          when {
            uri != null -> {
              bitmapSampled = BitmapUtils.cropBitmap(
                context = context,
                loadedImageUri = uri,
                cropPoints = cropPoints,
                degreesRotated = degreesRotated,
                orgWidth = orgWidth,
                orgHeight = orgHeight,
                fixAspectRatio = fixAspectRatio,
                aspectRatioX = aspectRatioX,
                aspectRatioY = aspectRatioY,
                reqWidth = reqWidth,
                reqHeight = reqHeight,
                flipHorizontally = flipHorizontally,
                flipVertically = flipVertically,
              )
            }
            bitmap != null -> {
              bitmapSampled = BitmapUtils.cropBitmapObjectHandleOOM(
                bitmap = bitmap,
                cropPoints = cropPoints,
                degreesRotated = degreesRotated,
                fixAspectRatio = fixAspectRatio,
                aspectRatioX = aspectRatioX,
                aspectRatioY = aspectRatioY,
                flipHorizontally = flipHorizontally,
                flipVertically = flipVertically,
              )
            }
            else -> {
              onPostExecute(
                Result(bitmap = null, sampleSize = 1),
              )
              return@launch
            }
          }
          val resizedBitmap = BitmapUtils.resizeBitmap(
            bitmap = bitmapSampled.bitmap,
            reqWidth = reqWidth,
            reqHeight = reqHeight,
            options = options,
          )

          launch(Dispatchers.IO) {
            val newUri = BitmapUtils.writeBitmapToUri(
              context = context,
              bitmap = resizedBitmap,
              compressFormat = saveCompressFormat,
              compressQuality = saveCompressQuality,
              customOutputUri = customOutputUri,
            )
            resizedBitmap.recycle()
            onPostExecute(
              Result(
                uri = newUri,
                sampleSize = bitmapSampled.sampleSize,
              ),
            )
          }
        }
      } catch (throwable: Exception) {
        onPostExecute(
          Result(error = throwable, sampleSize = 1),
        )
      }
    }
  }

  private suspend fun onPostExecute(result: Result) {
    withContext(Dispatchers.Main) {
      var completeCalled = false
      if (isActive) {
        cropImageViewReference.get()?.let {
          completeCalled = true
          it.onImageCroppingAsyncComplete(result)
        }
      }
      if (!completeCalled && result.bitmap != null) {
        // fast release of unused bitmap
        result.bitmap.recycle()
      }
    }
  }

  fun cancel() {
    job.cancel()
  }

  internal data class Result(
    /** The cropped bitmap. */
    val bitmap: Bitmap? = null,
    /** The saved cropped bitmap uri. */
    val uri: Uri? = null,
    /** The error that occurred during async bitmap cropping. */
    val error: Exception? = null,
    /** Sample size used creating the crop bitmap to lower its size. */
    val sampleSize: Int,
  )
}
