package com.canhub.cropper

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageOptions.Companion.DEGREES_360
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageView.RequestSizeOptions
import com.canhub.cropper.common.CommonValues
import com.canhub.cropper.common.CommonVersionCheck
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
     * Get the URI of the selected image from [getPickImageChooserIntent].
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
     * Get the File Path of the selected image from [getPickImageChooserIntent].
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
     * Create [ActivityBuilder] instance to open image picker for cropping and then start [ ] to crop the selected image.<br></br>
     * Result will be received in onActivityResult(int, int, Intent) and can be
     * retrieved using [.getActivityResult].
     *
     * @return builder for Crop Image Activity
     */
    @JvmStatic
    @Deprecated("use the CropImageContract ActivityResultContract instead")
    fun activity(): ActivityBuilder {
        return ActivityBuilder(null)
    }

    /**
     * Create [ActivityBuilder] instance to start [CropImageActivity] to crop the given
     * image.<br></br>
     * Result will be received in onActivityResult(int, int, Intent) and can be
     * retrieved using [getActivityResult].
     *
     * @param uri the image Android uri source to crop or null to start a picker
     * @return builder for Crop Image Activity
     */
    @JvmStatic
    @Deprecated("use the CropImageContract ActivityResultContract instead")
    fun activity(uri: Uri?): ActivityBuilder {
        return ActivityBuilder(uri)
    }

    /**
     * Get [CropImageActivity] result data object for crop image activity started using [ ][.activity].
     *
     * @param data result data intent as received in onActivityResult(int, int, Intent).
     * @return Crop Image Activity Result object or null if none exists
     */
    // TODO don't return null
    @JvmStatic
    @Deprecated("use the CropImageContract ActivityResultContract instead")
    fun getActivityResult(data: Intent?): ActivityResult? =
        data?.getParcelableExtra<Parcelable>(CROP_IMAGE_EXTRA_RESULT) as? ActivityResult?

    /**
     * Builder used for creating Image Crop Activity by user request.
     *
     * @param mSource The image to crop source Android uri.
     */
    @Deprecated("use the CropImageContract ActivityResultContract instead")
    class ActivityBuilder(private val mSource: Uri?) {

        /**
         * Options for image crop UX
         */
        private val cropImageOptions: CropImageOptions = CropImageOptions()

        /**
         * Get [CropImageActivity] intent to start the activity.
         */
        fun getIntent(context: Context): Intent {
            return getIntent(context, CropImageActivity::class.java)
        }

        /**
         * Get [CropImageActivity] intent to start the activity.
         */
        fun getIntent(context: Context, cls: Class<*>?): Intent {
            cropImageOptions.validate()
            val intent = Intent()
            intent.setClass(context, cls!!)
            val bundle = Bundle()
            bundle.putParcelable(CROP_IMAGE_EXTRA_SOURCE, mSource)
            bundle.putParcelable(CROP_IMAGE_EXTRA_OPTIONS, cropImageOptions)
            intent.putExtra(CROP_IMAGE_EXTRA_BUNDLE, bundle)
            return intent
        }

        /**
         * Start [CropImageActivity].
         *
         * @param activity activity to receive result
         */
        fun start(activity: Activity) {
            cropImageOptions.validate()
            activity.startActivityForResult(getIntent(activity), CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        }

        /**
         * Start [CropImageActivity].
         *
         * @param activity activity to receive result
         */
        fun start(activity: Activity, cls: Class<*>?) {
            cropImageOptions.validate()
            activity.startActivityForResult(
                getIntent(activity, cls),
                CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        /**
         * Start [CropImageActivity].
         *
         * @param fragment fragment to receive result
         */
        fun start(context: Context, fragment: Fragment) {
            fragment.startActivityForResult(getIntent(context), CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        }

        /**
         * Start [CropImageActivity].
         *
         * @param fragment fragment to receive result
         */
        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        fun start(context: Context, fragment: android.app.Fragment) {
            fragment.startActivityForResult(getIntent(context), CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        }

        /**
         * Start [CropImageActivity].
         *
         * @param fragment fragment to receive result
         */
        fun start(
            context: Context,
            fragment: Fragment,
            cls: Class<*>?,
        ) {
            fragment.startActivityForResult(
                getIntent(context, cls),
                CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        /**
         * Start [CropImageActivity].
         *
         * @param fragment fragment to receive result
         */
        @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
        fun start(
            context: Context,
            fragment: android.app.Fragment,
            cls: Class<*>?,
        ) {
            fragment.startActivityForResult(
                getIntent(context, cls),
                CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        }

        /**
         * The shape of the cropping window.<br></br>
         * To set square/circle crop shape set aspect ratio to 1:1.<br></br>
         * *Default: RECTANGLE*
         *
         * When setting RECTANGLE_VERTICAL_ONLY or RECTANGLE_HORIZONTAL_ONLY you may also want to
         * use a free aspect ratio (to allow the crop window to change in the desired dimension
         * whilst staying the same in the other dimension) and have the initial crop window cover
         * the entire image (so that the crop window has no space to move in the other dimension).
         * These can be done with
         * [ActivityBuilder.setFixAspectRatio] } (with argument `false`) and
         * [ActivityBuilder.setInitialCropWindowPaddingRatio] (with argument `0f).
         */
        fun setCropShape(cropShape: CropShape): ActivityBuilder {
            cropImageOptions.cropShape = cropShape
            return this
        }

        /**
         * An edge of the crop window will snap to the corresponding edge of a specified bounding box
         * when the crop window edge is less than or equal to this distance (in pixels) away from the
         * bounding box edge (in pixels).<br></br>
         * *Default: 3dp*
         */
        fun setSnapRadius(snapRadius: Float): ActivityBuilder {
            cropImageOptions.snapRadius = snapRadius
            return this
        }

        /**
         * The radius of the touchable area around the handle (in pixels).<br></br>
         * We are basing this value off of the recommended 48dp Rhythm.<br></br>
         * See: http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm<br></br>
         * *Default: 48dp*
         */
        fun setTouchRadius(touchRadius: Float): ActivityBuilder {
            cropImageOptions.touchRadius = touchRadius
            return this
        }

        /**
         * whether the guidelines should be on, off, or only showing when resizing.<br></br>
         * *Default: ON_TOUCH*
         */
        fun setGuidelines(guidelines: Guidelines): ActivityBuilder {
            cropImageOptions.guidelines = guidelines
            return this
        }

        /**
         * The initial scale type of the image in the crop image view<br></br>
         * *Default: FIT_CENTER*
         */
        fun setScaleType(scaleType: CropImageView.ScaleType): ActivityBuilder {
            cropImageOptions.scaleType = scaleType
            return this
        }

        /**
         * if to show crop overlay UI what contains the crop window UI surrounded by background over the
         * cropping image.<br></br>
         * *default: true, may disable for animation or frame transition.*
         */
        fun setShowCropOverlay(showCropOverlay: Boolean): ActivityBuilder {
            cropImageOptions.showCropOverlay = showCropOverlay
            return this
        }

        /**
         * if auto-zoom functionality is enabled.<br></br>
         * default: true.
         */
        fun setAutoZoomEnabled(autoZoomEnabled: Boolean): ActivityBuilder {
            cropImageOptions.autoZoomEnabled = autoZoomEnabled
            return this
        }

        /**
         * if multi touch functionality is enabled.<br></br>
         * default: true.
         */
        // TODO canato remove all ActivityBuilder
        fun setMultiTouchEnabled(multiTouchEnabled: Boolean): ActivityBuilder {
            cropImageOptions.multiTouchEnabled = multiTouchEnabled
            return this
        }

        /**
         * if the crop window can be moved by dragging the center.<br></br>
         * default: true
         */
        fun setCenterMoveEnabled(centerMoveEnabled: Boolean): ActivityBuilder {
            cropImageOptions.centerMoveEnabled = centerMoveEnabled
            return this
        }

        /**
         * The max zoom allowed during cropping.<br></br>
         * *Default: 4*
         */
        fun setMaxZoom(maxZoom: Int): ActivityBuilder {
            cropImageOptions.maxZoom = maxZoom
            return this
        }

        /**
         * The initial crop window padding from image borders in percentage of the cropping image
         * dimensions.<br></br>
         * *Default: 0.1*
         */
        fun setInitialCropWindowPaddingRatio(initialCropWindowPaddingRatio: Float): ActivityBuilder {
            cropImageOptions.initialCropWindowPaddingRatio = initialCropWindowPaddingRatio
            return this
        }

        /**
         * whether the width to height aspect ratio should be maintained or free to change.<br></br>
         * *Default: false*
         */
        fun setFixAspectRatio(fixAspectRatio: Boolean): ActivityBuilder {
            cropImageOptions.fixAspectRatio = fixAspectRatio
            return this
        }

        /**
         * the X,Y value of the aspect ratio.<br></br>
         * Also sets fixes aspect ratio to TRUE.<br></br>
         * *Default: 1/1*
         *
         * @param aspectRatioX the width
         * @param aspectRatioY the height
         */
        fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int): ActivityBuilder {
            cropImageOptions.aspectRatioX = aspectRatioX
            cropImageOptions.aspectRatioY = aspectRatioY
            cropImageOptions.fixAspectRatio = true
            return this
        }

        /**
         * the thickness of the guidelines lines (in pixels).<br></br>
         * *Default: 3dp*
         */
        fun setBorderLineThickness(borderLineThickness: Float): ActivityBuilder {
            cropImageOptions.borderLineThickness = borderLineThickness
            return this
        }

        /**
         * the color of the guidelines lines.<br></br>
         * *Default: Color.argb(170, 255, 255, 255)*
         */
        fun setBorderLineColor(borderLineColor: Int): ActivityBuilder {
            cropImageOptions.borderLineColor = borderLineColor
            return this
        }

        /**
         * thickness of the corner line (in pixels).<br></br>
         * *Default: 2dp*
         */
        fun setBorderCornerThickness(borderCornerThickness: Float): ActivityBuilder {
            cropImageOptions.borderCornerThickness = borderCornerThickness
            return this
        }

        /**
         * the offset of corner line from crop window border (in pixels).<br></br>
         * *Default: 5dp*
         */
        fun setBorderCornerOffset(borderCornerOffset: Float): ActivityBuilder {
            cropImageOptions.borderCornerOffset = borderCornerOffset
            return this
        }

        /**
         * the length of the corner line away from the corner (in pixels).<br></br>
         * *Default: 14dp*
         */
        fun setBorderCornerLength(borderCornerLength: Float): ActivityBuilder {
            cropImageOptions.borderCornerLength = borderCornerLength
            return this
        }

        /**
         * the color of the corner line.<br></br>
         * *Default: WHITE*
         */
        fun setBorderCornerColor(borderCornerColor: Int): ActivityBuilder {
            cropImageOptions.borderCornerColor = borderCornerColor
            return this
        }

        /**
         * the thickness of the guidelines lines (in pixels).<br></br>
         * *Default: 1dp*
         */
        fun setGuidelinesThickness(guidelinesThickness: Float): ActivityBuilder {
            cropImageOptions.guidelinesThickness = guidelinesThickness
            return this
        }

        /**
         * the color of the guidelines lines.<br></br>
         * *Default: Color.argb(170, 255, 255, 255)*
         */
        fun setGuidelinesColor(guidelinesColor: Int): ActivityBuilder {
            cropImageOptions.guidelinesColor = guidelinesColor
            return this
        }

        /**
         * the color of the overlay background around the crop window cover the image parts not in the
         * crop window.<br></br>
         * *Default: Color.argb(119, 0, 0, 0)*
         */
        fun setBackgroundColor(backgroundColor: Int): ActivityBuilder {
            cropImageOptions.backgroundColor = backgroundColor
            return this
        }

        /**
         * the min size the crop window is allowed to be (in pixels).<br></br>
         * *Default: 42dp, 42dp*
         */
        fun setMinCropWindowSize(
            minCropWindowWidth: Int,
            minCropWindowHeight: Int
        ): ActivityBuilder {
            cropImageOptions.minCropWindowWidth = minCropWindowWidth
            cropImageOptions.minCropWindowHeight = minCropWindowHeight
            return this
        }

        /**
         * the min size the resulting cropping image is allowed to be, affects the cropping window
         * limits (in pixels).<br></br>
         * *Default: 40px, 40px*
         */
        fun setMinCropResultSize(
            minCropResultWidth: Int,
            minCropResultHeight: Int
        ): ActivityBuilder {
            cropImageOptions.minCropResultWidth = minCropResultWidth
            cropImageOptions.minCropResultHeight = minCropResultHeight
            return this
        }

        /**
         * the max size the resulting cropping image is allowed to be, affects the cropping window
         * limits (in pixels).<br></br>
         * *Default: 99999, 99999*
         */
        fun setMaxCropResultSize(
            maxCropResultWidth: Int,
            maxCropResultHeight: Int
        ): ActivityBuilder {
            cropImageOptions.maxCropResultWidth = maxCropResultWidth
            cropImageOptions.maxCropResultHeight = maxCropResultHeight
            return this
        }

        /**
         * the title of the [CropImageActivity].<br></br>
         * *Default: ""*
         */
        fun setActivityTitle(activityTitle: CharSequence?): ActivityBuilder {
            cropImageOptions.activityTitle = activityTitle!!
            return this
        }

        /**
         * the color to use for action bar items icons.<br></br>
         * *Default: NONE*
         */
        fun setActivityMenuIconColor(activityMenuIconColor: Int): ActivityBuilder {
            cropImageOptions.activityMenuIconColor = activityMenuIconColor
            return this
        }

        /**
         * the Android Uri to save the cropped image to.<br></br>
         * *Default: NONE, will create a temp file*
         */
        fun setOutputUri(outputUri: Uri?): ActivityBuilder {
            cropImageOptions.customOutputUri = outputUri
            return this
        }

        /**
         * the compression format to use when writting the image.<br></br>
         * *Default: JPEG*
         */
        fun setOutputCompressFormat(outputCompressFormat: CompressFormat?): ActivityBuilder {
            cropImageOptions.outputCompressFormat = outputCompressFormat!!
            return this
        }

        /**
         * the quility (if applicable) to use when writting the image (0 - 100).<br></br>
         * *Default: 90*
         */
        fun setOutputCompressQuality(outputCompressQuality: Int): ActivityBuilder {
            cropImageOptions.outputCompressQuality = outputCompressQuality
            return this
        }

        /**
         * the size to resize the cropped image to.<br></br>
         * Uses [CropImageView.RequestSizeOptions.RESIZE_INSIDE] option.<br></br>
         * *Default: 0, 0 - not set, will not resize*
         */
        fun setRequestedSize(reqWidth: Int, reqHeight: Int): ActivityBuilder {
            return setRequestedSize(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE)
        }

        /**
         * the size to resize the cropped image to.<br></br>
         * *Default: 0, 0 - not set, will not resize*
         */
        fun setRequestedSize(
            reqWidth: Int,
            reqHeight: Int,
            options: RequestSizeOptions?,
        ): ActivityBuilder {
            cropImageOptions.outputRequestWidth = reqWidth
            cropImageOptions.outputRequestHeight = reqHeight
            cropImageOptions.outputRequestSizeOptions = options!!
            return this
        }

        /**
         * if the result of crop image activity should not save the cropped image bitmap.<br></br>
         * Used if you want to crop the image manually and need only the crop rectangle and rotation
         * data.<br></br>
         * *Default: false*
         */
        fun setNoOutputImage(noOutputImage: Boolean): ActivityBuilder {
            cropImageOptions.noOutputImage = noOutputImage
            return this
        }

        /**
         * the initial rectangle to set on the cropping image after loading.<br></br>
         * *Default: NONE - will initialize using initial crop window padding ratio*
         */
        fun setInitialCropWindowRectangle(initialCropWindowRectangle: Rect?): ActivityBuilder {
            cropImageOptions.initialCropWindowRectangle = initialCropWindowRectangle
            return this
        }

        /**
         * the initial rotation to set on the cropping image after loading (0-360 degrees clockwise).
         * <br></br>
         * *Default: NONE - will read image exif data*
         */
        fun setInitialRotation(initialRotation: Int): ActivityBuilder {
            cropImageOptions.initialRotation = (initialRotation + DEGREES_360) % DEGREES_360
            return this
        }

        /**
         * if to allow rotation during cropping.<br></br>
         * *Default: true*
         */
        fun setAllowRotation(allowRotation: Boolean): ActivityBuilder {
            cropImageOptions.allowRotation = allowRotation
            return this
        }

        /**
         * if to allow flipping during cropping.<br></br>
         * *Default: true*
         */
        fun setAllowFlipping(allowFlipping: Boolean): ActivityBuilder {
            cropImageOptions.allowFlipping = allowFlipping
            return this
        }

        /**
         * if to allow counter-clockwise rotation during cropping.<br></br>
         * Note: if rotation is disabled this option has no effect.<br></br>
         * *Default: false*
         */
        fun setAllowCounterRotation(allowCounterRotation: Boolean): ActivityBuilder {
            cropImageOptions.allowCounterRotation = allowCounterRotation
            return this
        }

        /**
         * The amount of degreees to rotate clockwise or counter-clockwise (0-360).<br></br>
         * *Default: 90*
         */
        fun setRotationDegrees(rotationDegrees: Int): ActivityBuilder {
            cropImageOptions.rotationDegrees = (rotationDegrees + DEGREES_360) % DEGREES_360
            return this
        }

        /**
         * whether the image should be flipped horizontally.<br></br>
         * *Default: false*
         */
        fun setFlipHorizontally(flipHorizontally: Boolean): ActivityBuilder {
            cropImageOptions.flipHorizontally = flipHorizontally
            return this
        }

        /**
         * whether the image should be flipped vertically.<br></br>
         * *Default: false*
         */
        fun setFlipVertically(flipVertically: Boolean): ActivityBuilder {
            cropImageOptions.flipVertically = flipVertically
            return this
        }

        /**
         * optional, set crop menu crop button title.<br></br>
         * *Default: null, will use resource string: crop_image_menu_crop*
         */
        fun setCropMenuCropButtonTitle(title: CharSequence?): ActivityBuilder {
            cropImageOptions.cropMenuCropButtonTitle = title
            return this
        }

        /**
         * Image resource id to use for crop icon instead of text.<br></br>
         * *Default: 0*
         */
        fun setCropMenuCropButtonIcon(@DrawableRes drawableResource: Int): ActivityBuilder {
            cropImageOptions.cropMenuCropButtonIcon = drawableResource
            return this
        }
    }

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
        error = Exception("cropping has been cancelled by the user"),
        cropPoints = floatArrayOf(),
        cropRect = null,
        wholeImageRect = null,
        rotation = 0,
        sampleSize = 0
    )
}
