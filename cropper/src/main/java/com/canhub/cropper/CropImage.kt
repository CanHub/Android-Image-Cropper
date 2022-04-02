package com.canhub.cropper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.common.CommonValues
import com.canhub.cropper.common.CommonVersionCheck.isAtLeastQ29
import com.canhub.cropper.utils.getFilePathFromUri
import java.io.File

/**
 * Helper to simplify crop image work like starting pick-image acitvity and handling camera/gallery
 * intents.<br></br>
 * The goal of the helper is to simplify the starting and most-common usage of image cropping and
 * not all porpose all possible scenario one-to-rule-them-all code base. So feel free to use it as
 * is and as a wiki to make your own.<br></br>
 * Added value you get out-of-the-box is some edge case handling that you may miss otherwise, like
 * the stupid-ass Android camera result URI that may differ from version to version and from device
 * to device.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object CropImage {

    /**
     * The key used to pass crop image source URI to [CropImageActivity].
     */
    const val CROP_IMAGE_EXTRA_SOURCE = "CROP_IMAGE_EXTRA_SOURCE"

    /**
     * The key used to pass crop image options to [CropImageActivity].
     */
    const val CROP_IMAGE_EXTRA_OPTIONS = "CROP_IMAGE_EXTRA_OPTIONS"

    /**
     * The key used to pass crop image bundle data to [CropImageActivity].
     */
    const val CROP_IMAGE_EXTRA_BUNDLE = "CROP_IMAGE_EXTRA_BUNDLE"

    /**
     * The key used to pass crop image result data back from [CropImageActivity].
     */
    const val CROP_IMAGE_EXTRA_RESULT = "CROP_IMAGE_EXTRA_RESULT"

    /**
     * The request code used to start pick image activity to be used on result to identify the this
     * specific request.
     */
    const val PICK_IMAGE_CHOOSER_REQUEST_CODE = 200

    /**
     * The request code used to request permission to pick image from external storage.
     */
    const val PICK_IMAGE_PERMISSIONS_REQUEST_CODE = 201

    /**
     * The request code used to request permission to capture image from camera.
     */
    const val CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE = 2011

    /**
     * The request code used to start [CropImageActivity] to be used on result to identify the
     * this specific request.
     */
    const val CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203

    /**
     * The result code used to return error from [CropImageActivity].
     */
    const val CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE = 204

    /**
     * Create a new bitmap that has all pixels beyond the oval shape transparent. Old bitmap is
     * recycled.
     */
    fun toOvalBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawOval(rect, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        bitmap.recycle()
        return output
    }

    /**
     * Get URI to image received from capture by camera.
     *
     * This is not the File Path, for it please use [getCaptureImageOutputUriFilePath]
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     */
    fun getCaptureImageOutputUriContent(context: Context): Uri {
        val outputFileUri: Uri
        val getImage: File?
        // We have this because of a HUAWEI path bug when we use getUriForFile
        if (isAtLeastQ29()) {
            getImage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            outputFileUri = try {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + CommonValues.authority,
                    File(getImage!!.path, "pickImageResult.jpeg")
                )
            } catch (e: Exception) {
                Uri.fromFile(File(getImage!!.path, "pickImageResult.jpeg"))
            }
        } else {
            getImage = context.externalCacheDir
            outputFileUri = Uri.fromFile(File(getImage!!.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }

    /**
     * Get File Path to image received from capture by camera.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param uniqueName If true, make each image cropped have a different file name, this could cause
     * memory issues, use wisely. [Default: false]
     */
    fun getCaptureImageOutputUriFilePath(context: Context, uniqueName: Boolean = false): String =
        getFilePathFromUri(context, getCaptureImageOutputUriContent(context), uniqueName)

    /**
     * Get the URI of the selected image
     * Will return the correct URI for camera and gallery image.
     *
     * This is not the File Path, for it please use [getPickImageResultUriFilePath]
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param data    the returned data of the activity result
     */
    @JvmStatic
    fun getPickImageResultUriContent(context: Context, data: Intent?): Uri {
        var isCamera = true
        val uri = data?.data
        if (uri != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera || uri == null) getCaptureImageOutputUriContent(context)
        else uri
    }

    /**
     * Get the File Path of the selected image
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param data    the returned data of the activity result
     * @param uniqueName If true, make each image cropped have a different file name, this could cause
     * memory issues, use wisely. [Default: false]
     */
    @JvmStatic
    fun getPickImageResultUriFilePath(
        context: Context,
        data: Intent?,
        uniqueName: Boolean = false
    ): String =
        getFilePathFromUri(context, getPickImageResultUriContent(context, data), uniqueName)

    /**
     * Result data of Crop Image Activity.
     */
    open class ActivityResult : CropResult, Parcelable {

        constructor(
            originalUri: Uri?,
            uriContent: Uri?,
            error: Exception?,
            cropPoints: FloatArray?,
            cropRect: Rect?,
            rotation: Int,
            wholeImageRect: Rect?,
            sampleSize: Int
        ) : super(
            originalBitmap = null,
            originalUri = originalUri,
            bitmap = null,
            uriContent = uriContent,
            error = error,
            cropPoints = cropPoints!!,
            cropRect = cropRect,
            wholeImageRect = wholeImageRect,
            rotation = rotation,
            sampleSize = sampleSize
        )

        protected constructor(`in`: Parcel) : super(
            originalBitmap = null,
            originalUri = `in`.readParcelable<Parcelable>(Uri::class.java.classLoader) as Uri?,
            bitmap = null,
            uriContent = `in`.readParcelable<Parcelable>(Uri::class.java.classLoader) as Uri?,
            error = `in`.readSerializable() as Exception?,
            cropPoints = `in`.createFloatArray()!!,
            cropRect = `in`.readParcelable<Parcelable>(Rect::class.java.classLoader) as Rect?,
            wholeImageRect = `in`.readParcelable<Parcelable>(Rect::class.java.classLoader) as Rect?,
            rotation = `in`.readInt(),
            sampleSize = `in`.readInt()
        )

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeParcelable(originalUri, flags)
            dest.writeParcelable(uriContent, flags)
            dest.writeSerializable(error)
            dest.writeFloatArray(cropPoints)
            dest.writeParcelable(cropRect, flags)
            dest.writeParcelable(wholeImageRect, flags)
            dest.writeInt(rotation)
            dest.writeInt(sampleSize)
        }

        override fun describeContents(): Int = 0

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<ActivityResult?> =
                object : Parcelable.Creator<ActivityResult?> {
                    override fun createFromParcel(`in`: Parcel): ActivityResult =
                        ActivityResult(`in`)

                    override fun newArray(size: Int): Array<ActivityResult?> = arrayOfNulls(size)
                }
        }
    }

    object CancelledResult : CropImageView.CropResult(
        originalBitmap = null,
        originalUri = null,
        bitmap = null,
        uriContent = null,
        error = CropException.Cancellation(),
        cropPoints = floatArrayOf(),
        cropRect = null,
        wholeImageRect = null,
        rotation = 0,
        sampleSize = 0
    )
}
