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

class BitmapCroppingWorkerJob(
    private val context: Context,
    private val cropImageViewReference: WeakReference<CropImageView>,
    val uri: Uri?,
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
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun start() {
        job = launch(Dispatchers.Default) {
            try {
                if (isActive) {
                    val bitmapSampled: BitmapUtils.BitmapSampled
                    when {
                        uri != null -> {
                            bitmapSampled = BitmapUtils.cropBitmap(
                                context,
                                uri,
                                cropPoints,
                                degreesRotated,
                                orgWidth,
                                orgHeight,
                                fixAspectRatio,
                                aspectRatioX,
                                aspectRatioY,
                                reqWidth,
                                reqHeight,
                                flipHorizontally,
                                flipVertically
                            )
                        }
                        bitmap != null -> {
                            bitmapSampled = BitmapUtils.cropBitmapObjectHandleOOM(
                                bitmap,
                                cropPoints,
                                degreesRotated,
                                fixAspectRatio,
                                aspectRatioX,
                                aspectRatioY,
                                flipHorizontally,
                                flipVertically
                            )
                        }
                        else -> {
                            onPostExecute(Result(bitmap = null, 1))
                            return@launch
                        }
                    }
                    val resizedBitmap =
                        BitmapUtils.resizeBitmap(bitmapSampled.bitmap, reqWidth, reqHeight, options)

                    launch(Dispatchers.IO) {
                        val newUri = BitmapUtils.writeBitmapToUri(
                            context = context,
                            bitmap = resizedBitmap,
                            compressFormat = saveCompressFormat,
                            compressQuality = saveCompressQuality,
                            customOutputUri = customOutputUri
                        )
                        resizedBitmap.recycle()
                        onPostExecute(
                            Result(
                                newUri,
                                bitmapSampled.sampleSize
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                onPostExecute(Result(e, false))
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

    class Result {

        /** The cropped bitmap  */
        val bitmap: Bitmap?

        /** The saved cropped bitmap uri  */
        val uri: Uri?

        /** The error that occurred during async bitmap cropping.  */
        val error: java.lang.Exception?

        /** is the cropping request was to get a bitmap or to save it to uri  */
        val isSave: Boolean

        /** sample size used creating the crop bitmap to lower its size  */
        val sampleSize: Int

        constructor(bitmap: Bitmap?, sampleSize: Int) {
            this.bitmap = bitmap
            uri = null
            error = null
            isSave = false
            this.sampleSize = sampleSize
        }

        constructor(uri: Uri?, sampleSize: Int) {
            bitmap = null
            this.uri = uri
            error = null
            isSave = true
            this.sampleSize = sampleSize
        }

        constructor(error: java.lang.Exception?, isSave: Boolean) {
            bitmap = null
            uri = null
            this.error = error
            this.isSave = isSave
            sampleSize = 1
        }
    }
}
