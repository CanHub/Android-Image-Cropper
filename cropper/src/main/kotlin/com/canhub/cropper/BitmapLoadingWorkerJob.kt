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
  val uri: Uri,
) : CoroutineScope {
  private val width: Int
  private val height: Int
  private val cropImageViewReference = WeakReference(cropImageView)
  private var currentJob: Job = Job()

  override val coroutineContext: CoroutineContext get() = Dispatchers.Main + currentJob

  init {
    val metrics = cropImageView.resources.displayMetrics
    val densityAdj = if (metrics.density > 1) (1.0 / metrics.density) else 1.0
    width = (metrics.widthPixels * densityAdj).toInt()
    height = (metrics.heightPixels * densityAdj).toInt()
  }

  fun start() {
    currentJob = launch(Dispatchers.Default) {
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
              ),
            )
          }
        }
      } catch (e: Exception) {
        onPostExecute(Result(uri, e))
      }
    }
  }

  /**
   * Once complete, see if ImageView is still around and set bitmap.
   *
   * [result] the result of bitmap loading
   */
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
        // fast release of unused bitmap
        result.bitmap.recycle()
      }
    }
  }

  fun cancel() {
    currentJob.cancel()
  }

  /** The result of BitmapLoadingWorkerJob async loading. */
  internal companion object class Result {
    /**
     * The Android URI of the image to load.
     */
    val uriContent: Uri

    /** The loaded bitmap. */
    val bitmap: Bitmap?

    /** The sample size used to load the given bitmap. */
    val loadSampleSize: Int

    /** The degrees the image was rotated. */
    val degreesRotated: Int

    /** If the image was flipped horizontally. */
    var flipHorizontally: Boolean = false

    /** If the image was flipped vertically. */
    var flipVertically: Boolean = false

    /** The error that occurred during async bitmap loading. */
    val error: Exception?

    internal constructor(
      uri: Uri,
      bitmap: Bitmap?,
      loadSampleSize: Int,
      degreesRotated: Int,
      flipHorizontally: Boolean,
      flipVertically: Boolean,
    ) {
      uriContent = uri
      this.bitmap = bitmap
      this.loadSampleSize = loadSampleSize
      this.degreesRotated = degreesRotated
      this.flipHorizontally = flipHorizontally
      this.flipVertically = flipVertically
      error = null
    }

    internal constructor(uri: Uri, error: Exception?) {
      uriContent = uri
      bitmap = null
      loadSampleSize = 0
      degreesRotated = 0
      this.error = error
    }
  }
}
