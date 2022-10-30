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

internal class BitmapLoadingWorkerJob internal constructor(
  private val context: Context,
  cropImageView: CropImageView,
  internal val uri: Uri,
) : CoroutineScope {
  private val width: Int
  private val height: Int
  private val cropImageViewReference = WeakReference(cropImageView)
  private var job: Job = Job()

  override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

  init {
    val metrics = cropImageView.resources.displayMetrics
    val densityAdjustment = if (metrics.density > 1) (1.0 / metrics.density) else 1.0
    width = (metrics.widthPixels * densityAdjustment).toInt()
    height = (metrics.heightPixels * densityAdjustment).toInt()
  }

  fun start() {
    job = launch(Dispatchers.Default) {
      try {
        if (isActive) {
          val decodeResult = BitmapUtils.decodeSampledBitmap(
            context = context,
            uri = uri,
            reqWidth = width,
            reqHeight = height,
          )

          if (isActive) {
            val orientateResult = BitmapUtils.orientateBitmapByExif(
              bitmap = decodeResult.bitmap,
              context = context,
              uri = uri,
            )

            onPostExecute(
              Result(
                uri = uri,
                bitmap = orientateResult.bitmap,
                loadSampleSize = decodeResult.sampleSize,
                degreesRotated = orientateResult.degrees,
                flipHorizontally = orientateResult.flipHorizontally,
                flipVertically = orientateResult.flipVertically,
                error = null,
              ),
            )
          }
        }
      } catch (e: Exception) {
        onPostExecute(
          Result(
            uri = uri,
            bitmap = null,
            loadSampleSize = 0,
            degreesRotated = 0,
            flipHorizontally = false,
            flipVertically = false,
            error = e,
          ),
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
          it.onSetImageUriAsyncComplete(result)
        }
      }

      if (!completeCalled && result.bitmap != null) {
        // Fast release of unused bitmap.
        result.bitmap.recycle()
      }
    }
  }

  fun cancel() = job.cancel()

  internal data class Result(
    val uri: Uri,
    val bitmap: Bitmap?,
    val loadSampleSize: Int,
    val degreesRotated: Int,
    val flipHorizontally: Boolean,
    val flipVertically: Boolean,
    val error: Exception?,
  )
}
