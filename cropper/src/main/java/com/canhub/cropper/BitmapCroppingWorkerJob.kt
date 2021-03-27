package com.canhub.cropper

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class BitmapCroppingWorkerJob internal constructor(
    private val activity: FragmentActivity,
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
    private val saveUri: Uri?,
    private val saveCompressFormat: Bitmap.CompressFormat? = Bitmap.CompressFormat.JPEG,
    private val saveCompressQuality: Int
) {

    private var currentJob: Job? = null

    constructor(
        activity: FragmentActivity,
        cropImageView: CropImageView,
        bitmap: Bitmap?,
        cropPoints: FloatArray,
        degreesRotated: Int,
        fixAspectRatio: Boolean,
        aspectRatioX: Int,
        aspectRatioY: Int,
        reqWidth: Int,
        reqHeight: Int,
        flipHorizontally: Boolean,
        flipVertically: Boolean,
        options: CropImageView.RequestSizeOptions,
        saveUri: Uri?,
        @Nullable saveCompressFormat: Bitmap.CompressFormat?,
        saveCompressQuality: Int
    ) : this(
        activity = activity,
        cropImageViewReference = WeakReference(cropImageView),
        uri = null,
        bitmap = bitmap,
        cropPoints = cropPoints,
        degreesRotated = degreesRotated,
        orgWidth = 0,
        orgHeight = 0,
        fixAspectRatio = fixAspectRatio,
        aspectRatioX = aspectRatioX,
        aspectRatioY = aspectRatioY,
        reqWidth = reqWidth,
        reqHeight = reqHeight,
        flipHorizontally = flipHorizontally,
        flipVertically = flipVertically,
        options = options,
        saveUri = saveUri,
        saveCompressFormat = saveCompressFormat ?: Bitmap.CompressFormat.JPEG,
        saveCompressQuality = saveCompressQuality
    )

    constructor(
        activity: FragmentActivity,
        cropImageView: CropImageView,
        uri: Uri?,
        cropPoints: FloatArray,
        degreesRotated: Int,
        orgWidth: Int,
        orgHeight: Int,
        fixAspectRatio: Boolean,
        aspectRatioX: Int,
        aspectRatioY: Int,
        reqWidth: Int,
        reqHeight: Int,
        flipHorizontally: Boolean,
        flipVertically: Boolean,
        options: CropImageView.RequestSizeOptions,
        saveUri: Uri?,
        @Nullable saveCompressFormat: Bitmap.CompressFormat,
        saveCompressQuality: Int
    ) : this(
        activity = activity,
        cropImageViewReference = WeakReference(cropImageView),
        uri = uri,
        bitmap = null,
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
        options = options,
        saveUri = saveUri,
        saveCompressFormat = saveCompressFormat,
        saveCompressQuality = saveCompressQuality
    )

    fun start() {
        currentJob = activity.lifecycleScope.launch(Dispatchers.Default) {
            try {
                if (isActive) {
                    val bitmapSampled: BitmapUtils.BitmapSampled
                    when {
                        uri != null -> {
                            bitmapSampled = BitmapUtils.cropBitmap(
                                activity,
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

                    if (saveUri == null) {
                        onPostExecute(
                            Result(
                                resizedBitmap,
                                bitmapSampled.sampleSize
                            )
                        )
                    } else {
                        BitmapUtils.writeBitmapToUri(
                            activity,
                            resizedBitmap,
                            saveUri,
                            saveCompressFormat ?: Bitmap.CompressFormat.JPEG,
                            saveCompressQuality
                        )
                        resizedBitmap.recycle()
                        onPostExecute(
                            Result(
                                saveUri,
                                bitmapSampled.sampleSize
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                onPostExecute(Result(e, saveUri != null))
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
        currentJob?.cancel()
    }

    // region: Inner class: Result
    companion object
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
