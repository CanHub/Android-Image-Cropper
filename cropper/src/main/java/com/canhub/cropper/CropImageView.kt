package com.canhub.cropper

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Pair
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.exifinterface.media.ExifInterface
import com.canhub.cropper.CropOverlayView.CropWindowChangeListener
import com.canhub.cropper.utils.getFilePathFromUri
import java.lang.ref.WeakReference
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/** Custom view that provides cropping capabilities to an image.  */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class CropImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs),
    CropWindowChangeListener {

    /** Image view widget used to show the image for cropping.  */
    private val imageView: ImageView

    /** Overlay over the image view to show cropping UI.  */
    private val mCropOverlayView: CropOverlayView?

    /** The matrix used to transform the cropping image in the image view  */
    private val mImageMatrix = Matrix()

    /** Reusing matrix instance for reverse matrix calculations.  */
    private val mImageInverseMatrix = Matrix()

    /** Progress bar widget to show progress bar on async image loading and cropping.  */
    private val mProgressBar: ProgressBar

    /** Rectangle used in image matrix transformation calculation (reusing rect instance)  */
    private val mImagePoints = FloatArray(8)

    /** Rectangle used in image matrix transformation for scale calculation (reusing rect instance)  */
    private val mScaleImagePoints = FloatArray(8)

    /** Animation class to smooth animate zoom-in/out  */
    private var mAnimation: CropImageAnimation? = null
    private var originalBitmap: Bitmap? = null

    /** The image rotation value used during loading of the image so we can reset to it  */
    private var mInitialDegreesRotated = 0

    /** How much the image is rotated from original clockwise  */
    private var mDegreesRotated = 0

    /** if the image flipped horizontally  */
    private var mFlipHorizontally: Boolean

    /** if the image flipped vertically  */
    private var mFlipVertically: Boolean
    private var mLayoutWidth = 0
    private var mLayoutHeight = 0
    private var mImageResource = 0

    /** The initial scale type of the image in the crop image view  */
    private var mScaleType: ScaleType

    /**
     * if to save bitmap on save instance state.<br></br>
     * It is best to avoid it by using URI in setting image for cropping.<br></br>
     * If false the bitmap is not saved and if restore is required to view will be empty, storing the
     * bitmap requires saving it to file which can be expensive. default: false.
     */
    var isSaveBitmapToInstanceState = false

    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * default: true, may disable for animation or frame transition.
     */
    private var mShowCropOverlay = true

    /** If true, shows a helper text label over crop overlay UI
     *  default: false
     */
    private var mShowCropLabel = false

    /**
     * Helper text label over crop overlay UI
     * default: empty string
     */
    private var mCropTextLabel = ""

    /**
     * Text size for text label over crop overlay UI
     * default: 20sp
     */
    private var mCropLabelTextSize = 20f

    /**
     * Text color for text label over crop overlay UI
     * default: White
     */
    private var mCropLabelTextColor = Color.WHITE

    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    private var mShowProgressBar = true

    /**
     * if auto-zoom functionality is enabled.<br></br>
     * default: true.
     */
    private var mAutoZoomEnabled = true

    /** The max zoom allowed during cropping  */
    private var mMaxZoom: Int

    /** callback to be invoked when crop overlay is released.  */
    private var mOnCropOverlayReleasedListener: OnSetCropOverlayReleasedListener? = null

    /** callback to be invoked when crop overlay is moved.  */
    private var mOnSetCropOverlayMovedListener: OnSetCropOverlayMovedListener? = null

    /** callback to be invoked when crop window is changed.  */
    private var mOnSetCropWindowChangeListener: OnSetCropWindowChangeListener? = null

    /** callback to be invoked when image async loading is complete.  */
    private var mOnSetImageUriCompleteListener: OnSetImageUriCompleteListener? = null

    /** callback to be invoked when image async cropping is complete.  */
    private var mOnCropImageCompleteListener: OnCropImageCompleteListener? = null
    /** Get the URI of an image that was set by URI, null otherwise.  */
    /** The URI that the image was loaded from (if loaded from URI)  */
    var imageUri: Uri? = null
        private set

    /** The sample size the image was loaded by if was loaded by URI  */
    private var loadedSampleSize = 1

    /** The current zoom level to to scale the cropping image  */
    private var mZoom = 1f

    /** The X offset that the cropping image was translated after zooming  */
    private var mZoomOffsetX = 0f

    /** The Y offset that the cropping image was translated after zooming  */
    private var mZoomOffsetY = 0f

    /** Used to restore the cropping windows rectangle after state restore  */
    private var mRestoreCropWindowRect: RectF? = null

    /** Used to restore image rotation after state restore  */
    private var mRestoreDegreesRotated = 0

    /**
     * Used to detect size change to handle auto-zoom using [.handleCropWindowChanged] in [.layout].
     */
    private var mSizeChanged = false

    /** Task used to load bitmap async from UI thread  */
    private var bitmapLoadingWorkerJob: WeakReference<BitmapLoadingWorkerJob>? = null

    /** Task used to crop bitmap async from UI thread  */
    private var bitmapCroppingWorkerJob: WeakReference<BitmapCroppingWorkerJob>? = null

    /** Get / set the scale type of the image in the crop view.  */
    var scaleType: ScaleType
        get() = mScaleType
        set(scaleType) {
            if (scaleType != mScaleType) {
                mScaleType = scaleType
                mZoom = 1f
                mZoomOffsetY = 0f
                mZoomOffsetX = mZoomOffsetY
                mCropOverlayView?.resetCropOverlayView()
                requestLayout()
            }
        }

    /**
     * The shape of the cropping area - rectangle/circular.<br></br>
     * To set square/circle crop shape set aspect ratio to 1:1.
     *
     * When setting RECTANGLE_VERTICAL_ONLY or RECTANGLE_HORIZONTAL_ONLY you may also want to
     * use a free aspect ratio (to allow the crop window to change in the desired dimension
     * whilst staying the same in the other dimension) and have the crop window start covering the
     * entirety of the image (so that the crop window has no space to move in the other dimension).
     * These can be done with
     * [CropImageView.setFixedAspectRatio] } (with argument `false`) and
     * [CropImageView.wholeImageRect] } (with argument `cropImageView.getWholeImageRect()`).
     */
    var cropShape: CropShape?
        get() = mCropOverlayView!!.cropShape
        set(cropShape) {
            mCropOverlayView!!.setCropShape(cropShape!!)
        }

    /**
     * The shape of the crop corner in the crop overlay (Rectangular / Circular)
     */
    var cornerShape: CropCornerShape?
        get() = mCropOverlayView!!.cornerShape
        set(cornerShape) {
            mCropOverlayView!!.setCropCornerShape(cornerShape!!)
        }

    /** Set auto-zoom functionality to enabled/disabled.  */
    var isAutoZoomEnabled: Boolean
        get() = mAutoZoomEnabled
        set(autoZoomEnabled) {
            if (mAutoZoomEnabled != autoZoomEnabled) {
                mAutoZoomEnabled = autoZoomEnabled
                handleCropWindowChanged(inProgress = false, animate = false)
                mCropOverlayView!!.invalidate()
            }
        }

    /** Set multi-touch functionality to enabled/disabled.  */
    fun setMultiTouchEnabled(multiTouchEnabled: Boolean) {
        if (mCropOverlayView!!.setMultiTouchEnabled(multiTouchEnabled)) {
            handleCropWindowChanged(inProgress = false, animate = false)
            mCropOverlayView.invalidate()
        }
    }

    /** Set moving of the crop window by dragging the center to enabled/disabled.  */
    fun setCenterMoveEnabled(centerMoveEnabled: Boolean) {
        if (mCropOverlayView!!.setCenterMoveEnabled(centerMoveEnabled)) {
            handleCropWindowChanged(inProgress = false, animate = false)
            mCropOverlayView.invalidate()
        }
    }

    /** The max zoom allowed during cropping.  */
    var maxZoom: Int
        get() = mMaxZoom
        set(maxZoom) {
            if (mMaxZoom != maxZoom && maxZoom > 0) {
                mMaxZoom = maxZoom
                handleCropWindowChanged(inProgress = false, animate = false)
                mCropOverlayView!!.invalidate()
            }
        }

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMinCropResultSize(minCropResultWidth: Int, minCropResultHeight: Int) {
        mCropOverlayView!!.setMinCropResultSize(minCropResultWidth, minCropResultHeight)
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMaxCropResultSize(maxCropResultWidth: Int, maxCropResultHeight: Int) {
        mCropOverlayView!!.setMaxCropResultSize(maxCropResultWidth, maxCropResultHeight)
    }

    /**
     * Set / Get the amount of degrees (between 0 and 360) the cropping image is rotated clockwise.<br></br>
     */
    var rotatedDegrees: Int
        get() = mDegreesRotated
        set(degrees) {
            if (mDegreesRotated != degrees) {
                rotateImage(degrees - mDegreesRotated)
            }
        }

    /**
     * whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows it to
     * be changed.
     */
    val isFixAspectRatio: Boolean
        get() = mCropOverlayView!!.isFixAspectRatio

    /**
     * Sets whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows
     * it to be changed.
     */
    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        mCropOverlayView!!.setFixedAspectRatio(fixAspectRatio)
    }

    /** Sets whether the image should be flipped horizontally  */
    var isFlippedHorizontally: Boolean
        get() = mFlipHorizontally
        set(flipHorizontally) {
            if (mFlipHorizontally != flipHorizontally) {
                mFlipHorizontally = flipHorizontally
                applyImageMatrix(
                    width = width.toFloat(),
                    height = height.toFloat(),
                    center = true,
                    animate = false
                )
            }
        }
    /** the Android Uri to save the cropped image to  */
    var customOutputUri: Uri? = null

    /** Sets whether the image should be flipped vertically  */
    var isFlippedVertically: Boolean
        get() = mFlipVertically
        set(flipVertically) {
            if (mFlipVertically != flipVertically) {
                mFlipVertically = flipVertically
                applyImageMatrix(
                    width = width.toFloat(),
                    height = height.toFloat(),
                    center = true,
                    animate = false
                )
            }
        }
    /** Get the current guidelines option set.  */
    /**
     * Sets the guidelines for the CropOverlayView to be either on, off, or to show when resizing the
     * application.
     */
    var guidelines: Guidelines?
        get() = mCropOverlayView!!.guidelines
        set(guidelines) {
            mCropOverlayView!!.setGuidelines(guidelines!!)
        }

    /** both the X and Y values of the aspectRatio.  */
    val aspectRatio: Pair<Int, Int>
        get() = Pair(mCropOverlayView!!.aspectRatioX, mCropOverlayView.aspectRatioY)

    /**
     * Sets the both the X and Y values of the aspectRatio.<br></br>
     * Sets fixed aspect ratio to TRUE.
     *
     * @param aspectRatioX int that specifies the new X value of the aspect ratio
     * @param aspectRatioY int that specifies the new Y value of the aspect ratio
     */
    fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int) {
        mCropOverlayView!!.aspectRatioX = aspectRatioX
        mCropOverlayView.aspectRatioY = aspectRatioY
        setFixedAspectRatio(true)
    }

    /** Clears set aspect ratio values and sets fixed aspect ratio to FALSE.  */
    fun clearAspectRatio() {
        mCropOverlayView!!.aspectRatioX = 1
        mCropOverlayView.aspectRatioY = 1
        setFixedAspectRatio(false)
    }

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (default: 3dp)
     */
    fun setSnapRadius(snapRadius: Float) {
        if (snapRadius >= 0) mCropOverlayView!!.setSnapRadius(snapRadius)
    }

    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    var isShowProgressBar: Boolean
        get() = mShowProgressBar
        set(showProgressBar) {
            if (mShowProgressBar != showProgressBar) {
                mShowProgressBar = showProgressBar
                setProgressBarVisibility()
            }
        }

    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * default: true, may disable for animation or frame transition.
     */
    var isShowCropOverlay: Boolean
        get() = mShowCropOverlay
        set(showCropOverlay) {
            if (mShowCropOverlay != showCropOverlay) {
                mShowCropOverlay = showCropOverlay
                setCropOverlayVisibility()
            }
        }
    /**
     * If enabled, show a text label on top of crop overlay UI, which gets moved along with the cropper
     */
    var isShowCropLabel: Boolean
        get() = mShowCropLabel
        set(showCropLabel) {
            if (mShowCropLabel != showCropLabel) {
                mShowCropLabel = showCropLabel
                mCropOverlayView?.setCropperTextLabelVisibility(mShowCropLabel)
            }
        }
    var cropLabelText: String
        get() = mCropTextLabel
        set(cropLabelText) {
            mCropTextLabel = cropLabelText
            mCropOverlayView?.setCropLabelText(cropLabelText)
        }
    var cropLabelTextSize: Float
        get() = mCropLabelTextSize
        set(textSize) {
            mCropLabelTextSize = cropLabelTextSize
            mCropOverlayView?.setCropLabelTextSize(textSize)
        }
    var cropLabelTextColor: Int
        get() = mCropLabelTextColor
        set(cropLabelTextColor) {
            mCropLabelTextColor = cropLabelTextColor
            mCropOverlayView?.setCropLabelTextColor(cropLabelTextColor)
        }
    /** Returns the integer of the imageResource  */
    /**
     * Sets a Drawable as the content of the CropImageView.
     *
     * resId the drawable resource ID to set
     */
    var imageResource: Int
        get() = mImageResource
        set(resId) {
            if (resId != 0) {
                mCropOverlayView!!.initialCropWindowRect = null
                val bitmap = BitmapFactory.decodeResource(resources, resId)
                setBitmap(bitmap, resId, null, 1, 0)
            }
        }

    /**
     * Gets the source Bitmap's dimensions. This represents the largest possible crop rectangle.
     *
     * @return a Rect instance dimensions of the source Bitmap
     */
    val wholeImageRect: Rect?
        get() {
            val loadedSampleSize = loadedSampleSize
            val bitmap = originalBitmap ?: return null
            val orgWidth = bitmap.width * loadedSampleSize
            val orgHeight = bitmap.height * loadedSampleSize
            return Rect(0, 0, orgWidth, orgHeight)
        } // get the points of the crop rectangle adjusted to source bitmap
    // get the rectangle for the points (it may be larger than original if rotation is not straight)
    /**
     * Gets the crop window's position relative to the source Bitmap (not the image displayed in the
     * CropImageView) using the original image rotation.
     *
     * @return a Rect instance containing cropped area boundaries of the source Bitmap
     *
     * Set the crop window position and size to the given rectangle.<br></br>
     * Image to crop must be first set before invoking this, for async - after complete callback.
     *
     * rect window rectangle (position and size) relative to source bitmap
     */
    var cropRect: Rect?
        get() {
            val loadedSampleSize = loadedSampleSize
            val bitmap = originalBitmap ?: return null
            // get the points of the crop rectangle adjusted to source bitmap
            val points = cropPoints
            val orgWidth = bitmap.width * loadedSampleSize
            val orgHeight = bitmap.height * loadedSampleSize
            // get the rectangle for the points (it may be larger than original if rotation is not stright)
            return BitmapUtils.getRectFromPoints(
                points,
                orgWidth,
                orgHeight,
                mCropOverlayView!!.isFixAspectRatio,
                mCropOverlayView.aspectRatioX,
                mCropOverlayView.aspectRatioY
            )
        }
        set(rect) {
            mCropOverlayView!!.initialCropWindowRect = rect
        }

    /**
     * Gets the crop window's position relative to the parent's view at screen.
     *
     * @return a Rect instance containing cropped area boundaries of the source Bitmap
     */
    val cropWindowRect: RectF?
        get() = mCropOverlayView?.cropWindowRect // Get crop window position relative to the displayed image.

    /**
     * Gets the 4 points of crop window's position relative to the source Bitmap (not the image
     * displayed in the CropImageView) using the original image rotation.<br></br>
     * Note: the 4 points may not be a rectangle if the image was rotates to NOT straight angle (!=
     * 90/180/270).
     *
     * @return 4 points (x0,y0,x1,y1,x2,y2,x3,y3) of cropped area boundaries
     */
    val cropPoints: FloatArray
        get() {
            // Get crop window position relative to the displayed image.
            val cropWindowRect = mCropOverlayView!!.cropWindowRect
            val points = floatArrayOf(
                cropWindowRect.left,
                cropWindowRect.top,
                cropWindowRect.right,
                cropWindowRect.top,
                cropWindowRect.right,
                cropWindowRect.bottom,
                cropWindowRect.left,
                cropWindowRect.bottom
            )
            mImageMatrix.invert(mImageInverseMatrix)
            mImageInverseMatrix.mapPoints(points)
            val resultPoints = FloatArray(points.size)
            for (i in points.indices) {
                resultPoints[i] = points[i] * loadedSampleSize
            }
            return resultPoints
        }

    /** Reset crop window to initial rectangle.  */
    fun resetCropRect() {
        mZoom = 1f
        mZoomOffsetX = 0f
        mZoomOffsetY = 0f
        mDegreesRotated = mInitialDegreesRotated
        mFlipHorizontally = false
        mFlipVertically = false
        applyImageMatrix(
            width = width.toFloat(),
            height = height.toFloat(),
            center = false,
            animate = false
        )
        mCropOverlayView!!.resetCropWindowRect()
    }

    /**
     * Gets the cropped image based on the current crop window.
     *
     * @return a new Bitmap representing the cropped image
     */
    val croppedImage: Bitmap?
        get() = getCroppedImage(0, 0, RequestSizeOptions.NONE)

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     * Uses [RequestSizeOptions.RESIZE_INSIDE] option.
     *
     * @param reqWidth the width to resize the cropped image to
     * @param reqHeight the height to resize the cropped image to
     * @return a new Bitmap representing the cropped image
     */
    fun getCroppedImage(reqWidth: Int, reqHeight: Int): Bitmap? {
        return getCroppedImage(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE)
    }

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     *
     * @param reqWidth the width to resize the cropped image to (see options)
     * @param reqHeight the height to resize the cropped image to (see options)
     * @param options the resize method to use, see its documentation
     * @return a new Bitmap representing the cropped image
     */
    fun getCroppedImage(reqWidth: Int, reqHeight: Int, options: RequestSizeOptions): Bitmap? {
        var croppedBitmap: Bitmap? = null
        if (originalBitmap != null) {
            val newReqWidth = if (options != RequestSizeOptions.NONE) reqWidth else 0
            val newReqHeight = if (options != RequestSizeOptions.NONE) reqHeight else 0
            croppedBitmap = if (imageUri != null &&
                (loadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING)
            ) {
                val orgWidth = originalBitmap!!.width * loadedSampleSize
                val orgHeight = originalBitmap!!.height * loadedSampleSize
                val bitmapSampled = BitmapUtils
                    .cropBitmap(
                        context,
                        imageUri,
                        cropPoints,
                        mDegreesRotated,
                        orgWidth,
                        orgHeight,
                        mCropOverlayView!!.isFixAspectRatio,
                        mCropOverlayView.aspectRatioX,
                        mCropOverlayView.aspectRatioY,
                        newReqWidth,
                        newReqHeight,
                        mFlipHorizontally,
                        mFlipVertically
                    )
                bitmapSampled.bitmap
            } else {
                BitmapUtils
                    .cropBitmapObjectHandleOOM(
                        originalBitmap,
                        cropPoints,
                        mDegreesRotated,
                        mCropOverlayView!!.isFixAspectRatio,
                        mCropOverlayView.aspectRatioX,
                        mCropOverlayView.aspectRatioY,
                        mFlipHorizontally,
                        mFlipVertically
                    )
                    .bitmap
            }
            croppedBitmap =
                BitmapUtils.resizeBitmap(croppedBitmap, newReqWidth, newReqHeight, options)
        }
        return croppedBitmap
    }

    /**
     * Cropped image based on the current crop window to the given uri.
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param saveCompressFormat the compression format to use when writing the image
     * @param saveCompressQuality the quality (if applicable) to use when writing the image (0 - 100)
     * @param reqWidth the width to resize the cropped image to (see options)
     * @param reqHeight the height to resize the cropped image to (see options)
     * @param options the resize method to use, see its documentation
     */
    fun croppedImageAsync(
        saveCompressFormat: CompressFormat = CompressFormat.JPEG,
        saveCompressQuality: Int = 90,
        reqWidth: Int = 0,
        reqHeight: Int = 0,
        options: RequestSizeOptions = RequestSizeOptions.RESIZE_INSIDE,
        customOutputUri: Uri? = null,
    ) {
        requireNotNull(mOnCropImageCompleteListener) { "mOnCropImageCompleteListener is not set" }
        startCropWorkerTask(
            reqWidth = reqWidth,
            reqHeight = reqHeight,
            options = options,
            saveCompressFormat = saveCompressFormat,
            saveCompressQuality = saveCompressQuality,
            customOutputUri = customOutputUri,
        )
    }

    /** Set the callback t  */
    fun setOnSetCropOverlayReleasedListener(listener: OnSetCropOverlayReleasedListener?) {
        mOnCropOverlayReleasedListener = listener
    }

    /** Set the callback when the cropping is moved  */
    fun setOnSetCropOverlayMovedListener(listener: OnSetCropOverlayMovedListener?) {
        mOnSetCropOverlayMovedListener = listener
    }

    /** Set the callback when the crop window is changed  */
    fun setOnCropWindowChangedListener(listener: OnSetCropWindowChangeListener?) {
        mOnSetCropWindowChangeListener = listener
    }

    /**
     * Set the callback to be invoked when image async loading ([.setImageUriAsync]) is
     * complete (successful or failed).
     */
    fun setOnSetImageUriCompleteListener(listener: OnSetImageUriCompleteListener?) {
        mOnSetImageUriCompleteListener = listener
    }

    /**
     * Set the callback to be invoked when image async cropping image ([.getCroppedImageAsync]
     * or [.saveCroppedImageAsync]) is complete (successful or failed).
     */
    fun setOnCropImageCompleteListener(listener: OnCropImageCompleteListener?) {
        mOnCropImageCompleteListener = listener
    }

    /**
     * Sets a Bitmap as the content of the CropImageView.
     *
     * @param bitmap the Bitmap to set
     */
    fun setImageBitmap(bitmap: Bitmap?) {
        mCropOverlayView!!.initialCropWindowRect = null
        setBitmap(bitmap, 0, null, 1, 0)
    }

    /**
     * Sets a Bitmap and initializes the image rotation according to the EXIT data.<br></br>
     * <br></br>
     * The EXIF can be retrieved by doing the following: `
     * ExifInterface exif = new ExifInterface(path);`
     *
     * @param bitmap the original bitmap to set; if null, this
     * @param exif the EXIF information about this bitmap; may be null
     */
    fun setImageBitmap(bitmap: Bitmap?, exif: ExifInterface?) {
        val setBitmap: Bitmap?
        var degreesRotated = 0
        if (bitmap != null && exif != null) {
            val result = BitmapUtils.orientateBitmapByExif(bitmap, exif)
            setBitmap = result.bitmap
            degreesRotated = result.degrees
            mFlipHorizontally = result.flipHorizontally
            mFlipVertically = result.flipVertically
            mInitialDegreesRotated = result.degrees
        } else setBitmap = bitmap

        mCropOverlayView!!.initialCropWindowRect = null
        setBitmap(setBitmap, 0, null, 1, degreesRotated)
    }

    /**
     * Sets a bitmap loaded from the given Android URI as the content of the CropImageView.<br></br>
     * Can be used with URI from gallery or camera source.<br></br>
     * Will rotate the image by exif data.<br></br>
     *
     * @param uri the URI to load the image from
     */
    fun setImageUriAsync(uri: Uri?) {
        if (uri != null) {
            val currentTask =
                if (bitmapLoadingWorkerJob != null) bitmapLoadingWorkerJob!!.get() else null
            currentTask?.cancel()
            // either no existing task is working or we canceled it, need to load new URI
            clearImageInt()
            mCropOverlayView!!.initialCropWindowRect = null
            bitmapLoadingWorkerJob =
                WeakReference(BitmapLoadingWorkerJob(context, this, uri))
            bitmapLoadingWorkerJob!!.get()!!.start()
            setProgressBarVisibility()
        }
    }

    /** Clear the current image set for cropping.  */
    fun clearImage() {
        clearImageInt()
        mCropOverlayView?.initialCropWindowRect = null
    }

    /**
     * Rotates image by the specified number of degrees clockwise.<br></br>
     * Negative values represent counter-clockwise rotations.
     *
     * @param degrees Integer specifying the number of degrees to rotate.
     */
    fun rotateImage(degrees: Int) {
        if (originalBitmap != null) {
            // Force degrees to be a non-zero value between 0 and 360 (inclusive)
            val newDegrees =
                if (degrees < 0) degrees % 360 + 360
                else degrees % 360
            val flipAxes = (
                !mCropOverlayView!!.isFixAspectRatio &&
                    (newDegrees in 46..134 || newDegrees in 216..304)
                )

            BitmapUtils.RECT.set(mCropOverlayView.cropWindowRect)
            var halfWidth =
                (if (flipAxes) BitmapUtils.RECT.height() else BitmapUtils.RECT.width()) / 2f
            var halfHeight =
                (if (flipAxes) BitmapUtils.RECT.width() else BitmapUtils.RECT.height()) / 2f

            if (flipAxes) {
                val isFlippedHorizontally = mFlipHorizontally
                mFlipHorizontally = mFlipVertically
                mFlipVertically = isFlippedHorizontally
            }
            mImageMatrix.invert(mImageInverseMatrix)
            BitmapUtils.POINTS[0] = BitmapUtils.RECT.centerX()
            BitmapUtils.POINTS[1] = BitmapUtils.RECT.centerY()
            BitmapUtils.POINTS[2] = 0f
            BitmapUtils.POINTS[3] = 0f
            BitmapUtils.POINTS[4] = 1f
            BitmapUtils.POINTS[5] = 0f
            mImageInverseMatrix.mapPoints(BitmapUtils.POINTS)
            // This is valid because degrees is not negative.
            mDegreesRotated = (mDegreesRotated + newDegrees) % 360
            applyImageMatrix(
                width = width.toFloat(),
                height = height.toFloat(),
                center = true,
                animate = false
            )
            // adjust the zoom so the crop window size remains the same even after image scale change
            mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS)
            mZoom /= sqrt(
                (BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2]).toDouble().pow(2.0) +
                    (BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3]).toDouble().pow(2.0)
            ).toFloat()
            mZoom = max(mZoom, 1f)
            applyImageMatrix(
                width = width.toFloat(),
                height = height.toFloat(),
                center = true,
                animate = false
            )
            mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS)
            // adjust the width/height by the changes in scaling to the image
            val change = sqrt(
                (BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2]).toDouble().pow(2.0) +
                    (BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3]).toDouble().pow(2.0)
            )
            halfWidth *= change.toFloat()
            halfHeight *= change.toFloat()
            // calculate the new crop window rectangle to center in the same location and have proper
            // width/height
            BitmapUtils.RECT[BitmapUtils.POINTS2[0] - halfWidth, BitmapUtils.POINTS2[1] - halfHeight, BitmapUtils.POINTS2[0] + halfWidth] =
                BitmapUtils.POINTS2[1] + halfHeight
            mCropOverlayView.resetCropOverlayView()
            mCropOverlayView.cropWindowRect = BitmapUtils.RECT
            applyImageMatrix(
                width = width.toFloat(),
                height = height.toFloat(),
                center = true,
                animate = false
            )
            handleCropWindowChanged(inProgress = false, animate = false)
            // make sure the crop window rectangle is within the cropping image bounds after all the
            // changes
            mCropOverlayView.fixCurrentCropWindowRect()
        }
    }

    /** Flips the image horizontally.  */
    fun flipImageHorizontally() {
        mFlipHorizontally = !mFlipHorizontally
        applyImageMatrix(
            width = width.toFloat(),
            height = height.toFloat(),
            center = true,
            animate = false
        )
    }

    /** Flips the image vertically.  */
    fun flipImageVertically() {
        mFlipVertically = !mFlipVertically
        applyImageMatrix(
            width = width.toFloat(),
            height = height.toFloat(),
            center = true,
            animate = false
        )
    }

    /**
     * On complete of the async bitmap loading by [setImageUriAsync] set the result to the
     * widget if still relevant and call listener if set.
     *
     * @param result the result of bitmap loading
     */
    fun onSetImageUriAsyncComplete(result: BitmapLoadingWorkerJob.Result) {
        bitmapLoadingWorkerJob = null
        setProgressBarVisibility()
        if (result.error == null) {
            mInitialDegreesRotated = result.degreesRotated
            mFlipHorizontally = result.flipHorizontally
            mFlipVertically = result.flipVertically
            setBitmap(
                result.bitmap,
                0,
                result.uriContent,
                result.loadSampleSize,
                result.degreesRotated
            )
        }
        val listener = mOnSetImageUriCompleteListener
        listener?.onSetImageUriComplete(this, result.uriContent, result.error)
    }

    /**
     * On complete of the async bitmap cropping by [.getCroppedImageAsync] call listener if
     * set.
     *
     * @param result the result of bitmap cropping
     */
    fun onImageCroppingAsyncComplete(result: BitmapCroppingWorkerJob.Result) {
        bitmapCroppingWorkerJob = null
        setProgressBarVisibility()
        val listener = mOnCropImageCompleteListener
        if (listener != null) {
            val cropResult = CropResult(
                originalBitmap,
                imageUri,
                result.bitmap,
                result.uri,
                result.error,
                cropPoints,
                cropRect,
                wholeImageRect,
                rotatedDegrees,
                result.sampleSize
            )
            listener.onCropImageComplete(this, cropResult)
        }
    }

    /**
     * Set the given bitmap to be used in for cropping<br></br>
     * Optionally clear full if the bitmap is new, or partial clear if the bitmap has been
     * manipulated.
     */
    private fun setBitmap(
        bitmap: Bitmap?,
        imageResource: Int,
        imageUri: Uri?,
        loadSampleSize: Int,
        degreesRotated: Int
    ) {
        if (originalBitmap == null || originalBitmap != bitmap) {
            clearImageInt()
            originalBitmap = bitmap
            imageView.setImageBitmap(originalBitmap)
            this.imageUri = imageUri
            mImageResource = imageResource
            loadedSampleSize = loadSampleSize
            mDegreesRotated = degreesRotated
            applyImageMatrix(
                width = width.toFloat(),
                height = height.toFloat(),
                center = true,
                animate = false
            )
            if (mCropOverlayView != null) {
                mCropOverlayView.resetCropOverlayView()
                setCropOverlayVisibility()
            }
        }
    }

    /**
     * Clear the current image set for cropping.<br></br>
     * Full clear will also clear the data of the set image like Uri or Resource id while partial
     * clear will only clear the bitmap and recycle if required.
     */
    private fun clearImageInt() {
        // if we allocated the bitmap, release it as fast as possible
        if (originalBitmap != null && (mImageResource > 0 || imageUri != null)) {
            originalBitmap!!.recycle()
        }
        originalBitmap = null
        // clean the loaded image flags for new image
        mImageResource = 0
        imageUri = null
        loadedSampleSize = 1
        mDegreesRotated = 0
        mZoom = 1f
        mZoomOffsetX = 0f
        mZoomOffsetY = 0f
        mImageMatrix.reset()
        mRestoreCropWindowRect = null
        mRestoreDegreesRotated = 0
        imageView.setImageBitmap(null)
        setCropOverlayVisibility()
    }

    /**
     * Gets the cropped image based on the current crop window.
     * If (reqWidth,reqHeight) is given AND image is loaded from URI cropping will try to use sample
     * size to fit in the requested width and height down-sampling if possible - optimization to get
     * best size to quality.
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     *
     * @param reqWidth the width to resize the cropped image to (see options)
     * @param reqHeight the height to resize the cropped image to (see options)
     * @param options the resize method to use on the cropped bitmap
     * @param saveCompressFormat if saveUri is given, the given compression will be used for saving
     * the image
     * @param saveCompressQuality if saveUri is given, the given quality will be used for the
     * compression.
     */
    fun startCropWorkerTask(
        reqWidth: Int,
        reqHeight: Int,
        options: RequestSizeOptions,
        saveCompressFormat: CompressFormat,
        saveCompressQuality: Int,
        customOutputUri: Uri?,
    ) {
        val bitmap = originalBitmap
        if (bitmap != null) {
            val currentTask =
                if (bitmapCroppingWorkerJob != null) bitmapCroppingWorkerJob!!.get() else null
            currentTask?.cancel()

            val (orgWidth, orgHeight) =
                if (loadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING)
                    Pair((bitmap.width * loadedSampleSize), (bitmap.height * loadedSampleSize))
                else Pair(0, 0)

            bitmapCroppingWorkerJob = WeakReference(
                BitmapCroppingWorkerJob(
                    context = context,
                    cropImageViewReference = WeakReference(this),
                    uri = imageUri,
                    bitmap = bitmap,
                    cropPoints = cropPoints,
                    degreesRotated = mDegreesRotated,
                    orgWidth = orgWidth,
                    orgHeight = orgHeight,
                    fixAspectRatio = mCropOverlayView!!.isFixAspectRatio,
                    aspectRatioX = mCropOverlayView.aspectRatioX,
                    aspectRatioY = mCropOverlayView.aspectRatioY,
                    reqWidth = if (options != RequestSizeOptions.NONE) reqWidth else 0,
                    reqHeight = if (options != RequestSizeOptions.NONE) reqHeight else 0,
                    flipHorizontally = mFlipHorizontally,
                    flipVertically = mFlipVertically,
                    options = options,
                    saveCompressFormat = saveCompressFormat,
                    saveCompressQuality = saveCompressQuality,
                    customOutputUri = customOutputUri ?: this.customOutputUri,
                )
            )

            bitmapCroppingWorkerJob!!.get()!!.start()
            setProgressBarVisibility()
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        if (imageUri == null && originalBitmap == null && mImageResource < 1) {
            return super.onSaveInstanceState()
        }
        val bundle = Bundle()
        val loadedImageUri =
            if (isSaveBitmapToInstanceState && imageUri == null && mImageResource < 1) {
                BitmapUtils.writeTempStateStoreBitmap(
                    context = context,
                    bitmap = originalBitmap,
                    customOutputUri = customOutputUri
                )
            } else {
                imageUri
            }
        if (loadedImageUri != null && originalBitmap != null) {
            val key = UUID.randomUUID().toString()
            BitmapUtils.mStateBitmap = Pair(key, WeakReference(originalBitmap))
            bundle.putString("LOADED_IMAGE_STATE_BITMAP_KEY", key)
        }
        if (bitmapLoadingWorkerJob != null) {
            val task = bitmapLoadingWorkerJob!!.get()
            if (task != null) {
                bundle.putParcelable("LOADING_IMAGE_URI", task.uri)
            }
        }
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putParcelable("LOADED_IMAGE_URI", loadedImageUri)
        bundle.putInt("LOADED_IMAGE_RESOURCE", mImageResource)
        bundle.putInt("LOADED_SAMPLE_SIZE", loadedSampleSize)
        bundle.putInt("DEGREES_ROTATED", mDegreesRotated)
        bundle.putParcelable("INITIAL_CROP_RECT", mCropOverlayView!!.initialCropWindowRect)
        BitmapUtils.RECT.set(mCropOverlayView.cropWindowRect)
        mImageMatrix.invert(mImageInverseMatrix)
        mImageInverseMatrix.mapRect(BitmapUtils.RECT)
        bundle.putParcelable("CROP_WINDOW_RECT", BitmapUtils.RECT)
        bundle.putString("CROP_SHAPE", mCropOverlayView.cropShape!!.name)
        bundle.putBoolean("CROP_AUTO_ZOOM_ENABLED", mAutoZoomEnabled)
        bundle.putInt("CROP_MAX_ZOOM", mMaxZoom)
        bundle.putBoolean("CROP_FLIP_HORIZONTALLY", mFlipHorizontally)
        bundle.putBoolean("CROP_FLIP_VERTICALLY", mFlipVertically)
        bundle.putBoolean("SHOW_CROP_LABEL", mShowCropLabel)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            // prevent restoring state if already set by outside code
            if (bitmapLoadingWorkerJob == null && imageUri == null && originalBitmap == null && mImageResource == 0) {
                var uri = state.getParcelable<Uri>("LOADED_IMAGE_URI")
                if (uri != null) {
                    val key = state.getString("LOADED_IMAGE_STATE_BITMAP_KEY")
                    key?.run {
                        val stateBitmap = BitmapUtils.mStateBitmap?.let {
                            if (it.first == key) it.second.get() else null
                        }
                        BitmapUtils.mStateBitmap = null
                        if (stateBitmap != null && !stateBitmap.isRecycled) {
                            setBitmap(stateBitmap, 0, uri, state.getInt("LOADED_SAMPLE_SIZE"), 0)
                        }
                    }
                    imageUri ?: setImageUriAsync(uri)
                } else {
                    val resId = state.getInt("LOADED_IMAGE_RESOURCE")

                    if (resId > 0) imageResource = resId
                    else {
                        uri = state.getParcelable("LOADING_IMAGE_URI")
                        uri?.let { setImageUriAsync(it) }
                    }
                }
                mRestoreDegreesRotated = state.getInt("DEGREES_ROTATED")
                mDegreesRotated = mRestoreDegreesRotated
                val initialCropRect = state.getParcelable<Rect>("INITIAL_CROP_RECT")
                if (initialCropRect != null &&
                    (initialCropRect.width() > 0 || initialCropRect.height() > 0)
                ) {
                    mCropOverlayView!!.initialCropWindowRect = initialCropRect
                }
                val cropWindowRect = state.getParcelable<RectF>("CROP_WINDOW_RECT")
                if (cropWindowRect != null && (cropWindowRect.width() > 0 || cropWindowRect.height() > 0)) {
                    mRestoreCropWindowRect = cropWindowRect
                }
                mCropOverlayView!!.setCropShape(
                    CropShape.valueOf(
                        state.getString("CROP_SHAPE")!!
                    )
                )
                mAutoZoomEnabled = state.getBoolean("CROP_AUTO_ZOOM_ENABLED")
                mMaxZoom = state.getInt("CROP_MAX_ZOOM")
                mFlipHorizontally = state.getBoolean("CROP_FLIP_HORIZONTALLY")
                mFlipVertically = state.getBoolean("CROP_FLIP_VERTICALLY")
                mShowCropLabel = state.getBoolean("SHOW_CROP_LABEL")
                mCropOverlayView.setCropperTextLabelVisibility(mShowCropLabel)
            }
            super.onRestoreInstanceState(state.getParcelable("instanceState"))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val bitmap = originalBitmap
        if (bitmap != null) {
            // Bypasses a baffling bug when used within a ScrollView, where heightSize is set to 0.
            if (heightSize == 0) heightSize = bitmap.height
            val desiredWidth: Int
            val desiredHeight: Int
            var viewToBitmapWidthRatio = Double.POSITIVE_INFINITY
            var viewToBitmapHeightRatio = Double.POSITIVE_INFINITY
            // Checks if either width or height needs to be fixed
            if (widthSize < bitmap.width) {
                viewToBitmapWidthRatio = widthSize.toDouble() / bitmap.width.toDouble()
            }
            if (heightSize < bitmap.height) {
                viewToBitmapHeightRatio = heightSize.toDouble() / bitmap.height.toDouble()
            }
            // If either needs to be fixed, choose smallest ratio and calculate from there
            if (viewToBitmapWidthRatio != Double.POSITIVE_INFINITY ||
                viewToBitmapHeightRatio != Double.POSITIVE_INFINITY
            ) {
                if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
                    desiredWidth = widthSize
                    desiredHeight = (bitmap.height * viewToBitmapWidthRatio).toInt()
                } else {
                    desiredHeight = heightSize
                    desiredWidth = (bitmap.width * viewToBitmapHeightRatio).toInt()
                }
            } else {
                // Otherwise, the picture is within frame layout bounds. Desired width is simply picture
                // size
                desiredWidth = bitmap.width
                desiredHeight = bitmap.height
            }
            val width = getOnMeasureSpec(widthMode, widthSize, desiredWidth)
            val height = getOnMeasureSpec(heightMode, heightSize, desiredHeight)
            mLayoutWidth = width
            mLayoutHeight = height
            setMeasuredDimension(mLayoutWidth, mLayoutHeight)
        } else setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mLayoutWidth > 0 && mLayoutHeight > 0) {
            // Gets original parameters, and creates the new parameters
            val origParams = this.layoutParams
            origParams.width = mLayoutWidth
            origParams.height = mLayoutHeight
            layoutParams = origParams
            if (originalBitmap != null) {
                applyImageMatrix(
                    (r - l).toFloat(), (b - t).toFloat(),
                    center = true,
                    animate = false
                )
                // after state restore we want to restore the window crop, possible only after widget size
                // is known
                val restoreCropWindowRect = mRestoreCropWindowRect
                if (restoreCropWindowRect != null) {
                    if (mRestoreDegreesRotated != mInitialDegreesRotated) {
                        mDegreesRotated = mRestoreDegreesRotated
                        applyImageMatrix(
                            width = (r - l).toFloat(),
                            height = (b - t).toFloat(),
                            center = true,
                            animate = false
                        )
                        mRestoreDegreesRotated = 0
                    }
                    mImageMatrix.mapRect(mRestoreCropWindowRect)
                    mCropOverlayView?.cropWindowRect = restoreCropWindowRect
                    handleCropWindowChanged(inProgress = false, animate = false)
                    mCropOverlayView?.fixCurrentCropWindowRect()
                    mRestoreCropWindowRect = null
                } else if (mSizeChanged) {
                    mSizeChanged = false
                    handleCropWindowChanged(inProgress = false, animate = false)
                }
            } else updateImageBounds(true)
        } else updateImageBounds(true)
    }

    /**
     * Detect size change to handle auto-zoom using [.handleCropWindowChanged]
     * in [.layout].
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSizeChanged = oldw > 0 && oldh > 0
    }

    /**
     * Handle crop window change to:<br></br>
     * 1. Execute auto-zoom-in/out depending on the area covered of cropping window relative to the
     * available view area.<br></br>
     * 2. Slide the zoomed sub-area if the cropping window is outside of the visible view sub-area.
     * <br></br>
     *
     * @param inProgress is the crop window change is still in progress by the user
     * @param animate if to animate the change to the image matrix, or set it directly
     */
    private fun handleCropWindowChanged(inProgress: Boolean, animate: Boolean) {
        val width = width
        val height = height
        if (originalBitmap != null && width > 0 && height > 0) {
            val cropRect = mCropOverlayView!!.cropWindowRect
            if (inProgress) {
                if (cropRect.left < 0 || cropRect.top < 0 || cropRect.right > width || cropRect.bottom > height) {
                    applyImageMatrix(
                        width = width.toFloat(),
                        height = height.toFloat(),
                        center = false,
                        animate = false
                    )
                }
            } else if (mAutoZoomEnabled || mZoom > 1) {
                var newZoom = 0f
                // keep the cropping window covered area to 50%-65% of zoomed sub-area
                if (mZoom < mMaxZoom && cropRect.width() < width * 0.5f && cropRect.height() < height * 0.5f) {
                    newZoom = min(
                        mMaxZoom.toFloat(),
                        min(
                            width / (cropRect.width() / mZoom / 0.64f),
                            height / (cropRect.height() / mZoom / 0.64f)
                        )
                    )
                }
                if (mZoom > 1 && (cropRect.width() > width * 0.65f || cropRect.height() > height * 0.65f)) {
                    newZoom = max(
                        1f,
                        min(
                            width / (cropRect.width() / mZoom / 0.51f),
                            height / (cropRect.height() / mZoom / 0.51f)
                        )
                    )
                }
                if (!mAutoZoomEnabled) newZoom = 1f

                if (newZoom > 0 && newZoom != mZoom) {
                    if (animate) {
                        if (mAnimation == null) {
                            // lazy create animation single instance
                            mAnimation = CropImageAnimation(imageView, mCropOverlayView)
                        }
                        // set the state for animation to start from
                        mAnimation!!.setStartState(mImagePoints, mImageMatrix)
                    }
                    mZoom = newZoom
                    applyImageMatrix(width.toFloat(), height.toFloat(), true, animate)
                }
            }
            if (mOnSetCropWindowChangeListener != null && !inProgress) {
                mOnSetCropWindowChangeListener!!.onCropWindowChanged()
            }
        }
    }

    /**
     * Apply matrix to handle the image inside the image view.
     *
     * @param width the width of the image view
     * @param height the height of the image view
     */
    private fun applyImageMatrix(width: Float, height: Float, center: Boolean, animate: Boolean) {
        val bitmap = originalBitmap
        if (bitmap != null && width > 0 && height > 0) {
            mImageMatrix.invert(mImageInverseMatrix)
            val cropRect = mCropOverlayView!!.cropWindowRect
            mImageInverseMatrix.mapRect(cropRect)
            mImageMatrix.reset()
            // move the image to the center of the image view first so we can manipulate it from there
            mImageMatrix.postTranslate(
                (width - bitmap.width) / 2, (height - bitmap.height) / 2
            )
            mapImagePointsByImageMatrix()
            // rotate the image the required degrees from center of image
            if (mDegreesRotated > 0) {
                mImageMatrix.postRotate(
                    mDegreesRotated.toFloat(),
                    BitmapUtils.getRectCenterX(mImagePoints),
                    BitmapUtils.getRectCenterY(mImagePoints)
                )
                mapImagePointsByImageMatrix()
            }
            // scale the image to the image view, image rect transformed to know new width/height
            val scale = min(
                width / BitmapUtils.getRectWidth(mImagePoints),
                height / BitmapUtils.getRectHeight(mImagePoints)
            )
            if (mScaleType == ScaleType.FIT_CENTER || mScaleType == ScaleType.CENTER_INSIDE && scale < 1 ||
                scale > 1 && mAutoZoomEnabled
            ) {
                mImageMatrix.postScale(
                    scale,
                    scale,
                    BitmapUtils.getRectCenterX(mImagePoints),
                    BitmapUtils.getRectCenterY(mImagePoints)
                )
                mapImagePointsByImageMatrix()
            } else if (mScaleType == ScaleType.CENTER_CROP) {
                mZoom = max(
                    getWidth() / BitmapUtils.getRectWidth(mImagePoints),
                    getHeight() / BitmapUtils.getRectHeight(mImagePoints)
                )
            }
            // scale by the current zoom level
            val scaleX = if (mFlipHorizontally) -mZoom else mZoom
            val scaleY = if (mFlipVertically) -mZoom else mZoom
            mImageMatrix.postScale(
                scaleX,
                scaleY,
                BitmapUtils.getRectCenterX(mImagePoints),
                BitmapUtils.getRectCenterY(mImagePoints)
            )
            mapImagePointsByImageMatrix()
            mImageMatrix.mapRect(cropRect)

            if (mScaleType == ScaleType.CENTER_CROP && center && !animate) {
                mZoomOffsetX = 0f
                mZoomOffsetY = 0f
            } else if (center) {
                // set the zoomed area to be as to the center of cropping window as possible
                mZoomOffsetX =
                    if (width > BitmapUtils.getRectWidth(mImagePoints)) 0f
                    else max(
                        min(
                            width / 2 - cropRect.centerX(),
                            -BitmapUtils.getRectLeft(mImagePoints)
                        ),
                        getWidth() - BitmapUtils.getRectRight(mImagePoints)
                    ) / scaleX

                mZoomOffsetY =
                    if (height > BitmapUtils.getRectHeight(mImagePoints)) 0f
                    else max(
                        min(
                            height / 2 - cropRect.centerY(),
                            -BitmapUtils.getRectTop(mImagePoints)
                        ),
                        getHeight() - BitmapUtils.getRectBottom(mImagePoints)
                    ) / scaleY
            } else {
                // adjust the zoomed area so the crop window rectangle will be inside the area in case it
                // was moved outside
                mZoomOffsetX = (
                    min(
                        max(mZoomOffsetX * scaleX, -cropRect.left),
                        -cropRect.right + width
                    ) / scaleX
                    )

                mZoomOffsetY = (
                    min(
                        max(mZoomOffsetY * scaleY, -cropRect.top),
                        -cropRect.bottom + height
                    ) / scaleY
                    )
            }
            // apply to zoom offset translate and update the crop rectangle to offset correctly
            mImageMatrix.postTranslate(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY)
            cropRect.offset(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY)
            mCropOverlayView.cropWindowRect = cropRect
            mapImagePointsByImageMatrix()
            mCropOverlayView.invalidate()
            // set matrix to apply
            if (animate) {
                // set the state for animation to end in, start animation now
                mAnimation!!.setEndState(mImagePoints, mImageMatrix)
                imageView.startAnimation(mAnimation)
            } else imageView.imageMatrix = mImageMatrix
            // update the image rectangle in the crop overlay
            updateImageBounds(false)
        }
    }

    /**
     * Adjust the given image rectangle by image transformation matrix to know the final rectangle of
     * the image.<br></br>
     * To get the proper rectangle it must be first reset to original image rectangle.
     */
    private fun mapImagePointsByImageMatrix() {
        mImagePoints[0] = 0f
        mImagePoints[1] = 0f
        mImagePoints[2] = originalBitmap!!.width.toFloat()
        mImagePoints[3] = 0f
        mImagePoints[4] = originalBitmap!!.width.toFloat()
        mImagePoints[5] = originalBitmap!!.height.toFloat()
        mImagePoints[6] = 0f
        mImagePoints[7] = originalBitmap!!.height.toFloat()
        mImageMatrix.mapPoints(mImagePoints)
        mScaleImagePoints[0] = 0f
        mScaleImagePoints[1] = 0f
        mScaleImagePoints[2] = 100f
        mScaleImagePoints[3] = 0f
        mScaleImagePoints[4] = 100f
        mScaleImagePoints[5] = 100f
        mScaleImagePoints[6] = 0f
        mScaleImagePoints[7] = 100f
        mImageMatrix.mapPoints(mScaleImagePoints)
    }

    /**
     * Set visibility of crop overlay to hide it when there is no image or specificly set by client.
     */
    private fun setCropOverlayVisibility() {
        if (mCropOverlayView != null) {
            mCropOverlayView.visibility =
                if (mShowCropOverlay && originalBitmap != null) VISIBLE else INVISIBLE
        }
    }

    /**
     * Set visibility of progress bar when async loading/cropping is in process and show is enabled.
     */
    private fun setProgressBarVisibility() {
        val visible = (
            mShowProgressBar &&
                (
                    originalBitmap == null && bitmapLoadingWorkerJob != null ||
                        bitmapCroppingWorkerJob != null
                    )
            )
        mProgressBar.visibility =
            if (visible) VISIBLE else INVISIBLE
    }

    /** Update the scale factor between the actual image bitmap and the shown image.<br></br>  */
    private fun updateImageBounds(clear: Boolean) {
        if (originalBitmap != null && !clear) {
            // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for
            // width/height.
            val scaleFactorWidth =
                100f * loadedSampleSize / BitmapUtils.getRectWidth(mScaleImagePoints)
            val scaleFactorHeight =
                100f * loadedSampleSize / BitmapUtils.getRectHeight(mScaleImagePoints)
            mCropOverlayView!!.setCropWindowLimits(
                width.toFloat(), height.toFloat(), scaleFactorWidth, scaleFactorHeight
            )
        }
        // set the bitmap rectangle and update the crop window after scale factor is set
        mCropOverlayView!!.setBounds(if (clear) null else mImagePoints, width, height)
    }

    /**
     * The possible cropping area shape.<br></br>
     * To set square/circle crop shape set aspect ratio to 1:1.
     */
    enum class CropShape {

        RECTANGLE, OVAL, RECTANGLE_VERTICAL_ONLY, RECTANGLE_HORIZONTAL_ONLY
    }
    /**
     * Possible crop corner shape
     */
    enum class CropCornerShape {
        RECTANGLE, OVAL
    }

    /**
     * Options for scaling the bounds of cropping image to the bounds of Crop Image View.<br></br>
     * Note: Some options are affected by auto-zoom, if enabled.
     */
    enum class ScaleType {

        /**
         * Scale the image uniformly (maintain the image's aspect ratio) to fit in crop image view.<br></br>
         * The largest dimension will be equals to crop image view and the second dimension will be
         * smaller.
         */
        FIT_CENTER,

        /**
         * Center the image in the view, but perform no scaling.<br></br>
         * Note: If auto-zoom is enabled and the source image is smaller than crop image view then it
         * will be scaled uniformly to fit the crop image view.
         */
        CENTER,

        /**
         * Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions (width
         * and height) of the image will be equal to or **larger** than the corresponding dimension
         * of the view (minus padding).<br></br>
         * The image is then centered in the view.
         */
        CENTER_CROP,

        /**
         * Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions (width
         * and height) of the image will be equal to or **less** than the corresponding dimension of
         * the view (minus padding).<br></br>
         * The image is then centered in the view.<br></br>
         * Note: If auto-zoom is enabled and the source image is smaller than crop image view then it
         * will be scaled uniformly to fit the crop image view.
         */
        CENTER_INSIDE
    }

    /** The possible guidelines showing types.  */
    enum class Guidelines {

        /** Never show  */
        OFF,

        /** Show when crop move action is live  */
        ON_TOUCH,

        /** Always show  */
        ON
    }

    /** Possible options for handling requested width/height for cropping.  */
    enum class RequestSizeOptions {

        /** No resize/sampling is done unless required for memory management (OOM).  */
        NONE,

        /**
         * Only sample the image during loading (if image set using URI) so the smallest of the image
         * dimensions will be between the requested size and x2 requested size.<br></br>
         * NOTE: resulting image will not be exactly requested width/height see: [Loading
 * Large Bitmaps Efficiently](http://developer.android.com/training/displaying-bitmaps/load-bitmap.html).
         */
        SAMPLING,

        /**
         * Resize the image uniformly (maintain the image's aspect ratio) so that both dimensions (width
         * and height) of the image will be equal to or **less** than the corresponding requested
         * dimension.<br></br>
         * If the image is smaller than the requested size it will NOT change.
         */
        RESIZE_INSIDE,

        /**
         * Resize the image uniformly (maintain the image's aspect ratio) to fit in the given
         * width/height.<br></br>
         * The largest dimension will be equals to the requested and the second dimension will be
         * smaller.<br></br>
         * If the image is smaller than the requested size it will enlarge it.
         */
        RESIZE_FIT,

        /**
         * Resize the image to fit exactly in the given width/height.<br></br>
         * This resize method does NOT preserve aspect ratio.<br></br>
         * If the image is smaller than the requested size it will enlarge it.
         */
        RESIZE_EXACT
    }

    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    fun interface OnSetCropOverlayReleasedListener {

        /**
         * Called when the crop overlay changed listener is called and inProgress is false.
         *
         * @param rect The rect coordinates of the cropped overlay
         */
        fun onCropOverlayReleased(rect: Rect?)
    }

    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    fun interface OnSetCropOverlayMovedListener {

        /**
         * Called when the crop overlay is moved
         *
         * @param rect The rect coordinates of the cropped overlay
         */
        fun onCropOverlayMoved(rect: Rect?)
    }

    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    fun interface OnSetCropWindowChangeListener {

        /** Called when the crop window is changed  */
        fun onCropWindowChanged()
    }

    /** Interface definition for a callback to be invoked when image async loading is complete.  */
    fun interface OnSetImageUriCompleteListener {

        /**
         * Called when a crop image view has completed loading image for cropping.<br></br>
         * If loading failed error parameter will contain the error.
         *
         * @param view The crop image view that loading of image was complete.
         * @param uri the URI of the image that was loading
         * @param error if error occurred during loading will contain the error, otherwise null.
         */
        fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?)
    }

    /** Interface definition for a callback to be invoked when image async crop is complete.  */
    fun interface OnCropImageCompleteListener {

        /**
         * Called when a crop image view has completed cropping image.<br></br>
         * Result object contains the cropped bitmap, saved cropped image uri, crop points data or the
         * error occured during cropping.
         *
         * @param view The crop image view that cropping of image was complete.
         * @param result the crop image result data (with cropped image or error)
         */
        fun onCropImageComplete(view: CropImageView, result: CropResult)
    }

    /** Result data of crop image.  */
    open class CropResult internal constructor(
        /**
         * The image bitmap of the original image loaded for cropping.<br></br>
         * Null if uri used to load image or activity result is used.
         */
        val originalBitmap: Bitmap?,
        /**
         * The Android uri of the original image loaded for cropping.<br></br>
         * Null if bitmap was used to load image.
         */
        val originalUri: Uri?,
        /**
         * The cropped image bitmap result.<br></br>
         * Null if save cropped image was executed, no output requested or failure.
         */
        val bitmap: Bitmap?,
        /**
         * The Android uri of the saved cropped image result.<br></br>
         * Null if get cropped image was executed, no output requested or failure.
         *
         * This is NOT the file path, please use [getUriFilePath]
         */
        val uriContent: Uri?,
        /** The error that failed the loading/cropping (null if successful)  */
        val error: Exception?,
        /** The 4 points of the cropping window in the source image  */
        val cropPoints: FloatArray,
        /** The rectangle of the cropping window in the source image  */
        val cropRect: Rect?,
        /** The rectangle of the source image dimensions  */
        val wholeImageRect: Rect?,
        /** The final rotation of the cropped image relative to source  */
        val rotation: Int,
        /** sample size used creating the crop bitmap to lower its size  */
        val sampleSize: Int
    ) {

        /**
         * The image bitmap of the original image loaded for cropping.<br></br>
         * Null if uri used to load image or activity result is used.
         */
        /**
         * The Android uri of the original image loaded for cropping.<br></br>
         * Null if bitmap was used to load image.
         */
        /**
         * The cropped image bitmap result.<br></br>
         * Null if save cropped image was executed, no output requested or failure.
         * Null if uri used to load image or activity result is used.
         */
        /**
         * The Android uri of the saved cropped image result Null if get cropped image was executed, no
         * output requested or failure.
         */
        /** The error that failed the loading/cropping (null if successful)  */
        /** The 4 points of the cropping window in the source image  */
        /** The rectangle of the cropping window in the source image  */
        /** The rectangle of the source image dimensions  */
        /** The final rotation of the cropped image relative to source  */
        /** sample size used creating the crop bitmap to lower its size  */
        /** Is the result is success or error.  */
        val isSuccessful: Boolean
            get() = error == null

        /**
         * The cropped image bitmap result.<br></br>
         * Null if save cropped image was executed, no output requested or failure.
         *
         * @param context used to retrieve the bitmap in case you need from activity result
         */
        fun getBitmap(context: Context): Bitmap? {
            return bitmap ?: try {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uriContent)
            } catch (e: Exception) {
                null
            }
        }

        /**
         * The file path of the image to load
         * Null if get cropped image was executed, no output requested or failure.
         *
         * @param context used to access Android APIs, like content resolve, it is your
         * activity/fragment/widget.
         * @param uniqueName If true, make each image cropped have a different file name, this could
         * cause memory issues, use wisely. [Default: false]
         */
        fun getUriFilePath(context: Context, uniqueName: Boolean = false): String? =
            uriContent?.let { getFilePathFromUri(context, it, uniqueName) }
    }

    companion object {

        /**
         * Determines the specs for the onMeasure function. Calculates the width or height depending on
         * the mode.
         *
         * @param measureSpecMode The mode of the measured width or height.
         * @param measureSpecSize The size of the measured width or height.
         * @param desiredSize The desired size of the measured width or height.
         * @return The final size of the width or height.
         */
        private fun getOnMeasureSpec(
            measureSpecMode: Int,
            measureSpecSize: Int,
            desiredSize: Int
        ): Int {
            // Measure Width
            return when (measureSpecMode) {
                MeasureSpec.EXACTLY -> measureSpecSize // Must be this size
                MeasureSpec.AT_MOST -> min(
                    desiredSize,
                    measureSpecSize
                ) // Can't be bigger than...; match_parent value
                else -> desiredSize // Be whatever you want; wrap_content
            }
        }
    }

    init {
        var options: CropImageOptions? = null
        val intent = if (context is Activity) context.intent else null
        if (intent != null) {
            val bundle = intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
            if (bundle != null) {
                options = bundle.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS)
            }
        }
        if (options == null) {
            options = CropImageOptions()
            if (attrs != null) {
                val ta = context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, 0)
                try {
                    options.fixAspectRatio = ta.getBoolean(
                        R.styleable.CropImageView_cropFixAspectRatio,
                        options.fixAspectRatio
                    )
                    options.aspectRatioX = ta.getInteger(
                        R.styleable.CropImageView_cropAspectRatioX,
                        options.aspectRatioX
                    )
                    options.aspectRatioY = ta.getInteger(
                        R.styleable.CropImageView_cropAspectRatioY,
                        options.aspectRatioY
                    )
                    options.scaleType = ScaleType.values()[
                        ta.getInt(
                            R.styleable.CropImageView_cropScaleType,
                            options.scaleType.ordinal
                        )
                    ]
                    options.autoZoomEnabled = ta.getBoolean(
                        R.styleable.CropImageView_cropAutoZoomEnabled,
                        options.autoZoomEnabled
                    )
                    options.multiTouchEnabled = ta.getBoolean(
                        R.styleable.CropImageView_cropMultiTouchEnabled, options.multiTouchEnabled
                    )
                    options.centerMoveEnabled = ta.getBoolean(
                        R.styleable.CropImageView_cropCenterMoveEnabled, options.centerMoveEnabled
                    )
                    options.maxZoom =
                        ta.getInteger(R.styleable.CropImageView_cropMaxZoom, options.maxZoom)
                    options.cropShape = CropShape.values()[
                        ta.getInt(
                            R.styleable.CropImageView_cropShape,
                            options.cropShape.ordinal
                        )
                    ]
                    options.cornerShape = CropCornerShape.values()[
                        ta.getInt(
                            R.styleable.CropImageView_cornerShape,
                            options.cornerShape.ordinal
                        )
                    ]
                    options.cropCornerRadius = ta.getDimension(
                        R.styleable.CropImageView_cropCornerRadius,
                        options.cropCornerRadius
                    )
                    options.guidelines = Guidelines.values()[
                        ta.getInt(
                            R.styleable.CropImageView_cropGuidelines, options.guidelines.ordinal
                        )
                    ]
                    options.snapRadius = ta.getDimension(
                        R.styleable.CropImageView_cropSnapRadius,
                        options.snapRadius
                    )
                    options.touchRadius = ta.getDimension(
                        R.styleable.CropImageView_cropTouchRadius,
                        options.touchRadius
                    )
                    options.initialCropWindowPaddingRatio = ta.getFloat(
                        R.styleable.CropImageView_cropInitialCropWindowPaddingRatio,
                        options.initialCropWindowPaddingRatio
                    )
                    options.circleCornerFillColorHexValue = ta.getInteger(
                        R.styleable.CropImageView_cropCornerCircleFillColor, options.circleCornerFillColorHexValue
                    )
                    options.borderLineThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderLineThickness,
                        options.borderLineThickness
                    )
                    options.borderLineColor = ta.getInteger(
                        R.styleable.CropImageView_cropBorderLineColor,
                        options.borderLineColor
                    )
                    options.borderCornerThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerThickness,
                        options.borderCornerThickness
                    )
                    options.borderCornerOffset = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerOffset, options.borderCornerOffset
                    )
                    options.borderCornerLength = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerLength, options.borderCornerLength
                    )
                    options.borderCornerColor = ta.getInteger(
                        R.styleable.CropImageView_cropBorderCornerColor, options.borderCornerColor
                    )
                    options.guidelinesThickness = ta.getDimension(
                        R.styleable.CropImageView_cropGuidelinesThickness,
                        options.guidelinesThickness
                    )
                    options.guidelinesColor = ta.getInteger(
                        R.styleable.CropImageView_cropGuidelinesColor,
                        options.guidelinesColor
                    )
                    options.backgroundColor = ta.getInteger(
                        R.styleable.CropImageView_cropBackgroundColor,
                        options.backgroundColor
                    )
                    options.showCropOverlay = ta.getBoolean(
                        R.styleable.CropImageView_cropShowCropOverlay,
                        mShowCropOverlay
                    )
                    options.showProgressBar = ta.getBoolean(
                        R.styleable.CropImageView_cropShowProgressBar,
                        mShowProgressBar
                    )
                    options.borderCornerThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerThickness,
                        options.borderCornerThickness
                    )
                    options.minCropWindowWidth = ta.getDimension(
                        R.styleable.CropImageView_cropMinCropWindowWidth,
                        options.minCropWindowWidth.toFloat()
                    ).toInt()
                    options.minCropWindowHeight = ta.getDimension(
                        R.styleable.CropImageView_cropMinCropWindowHeight,
                        options.minCropWindowHeight.toFloat()
                    ).toInt()
                    options.minCropResultWidth = ta.getFloat(
                        R.styleable.CropImageView_cropMinCropResultWidthPX,
                        options.minCropResultWidth.toFloat()
                    ).toInt()
                    options.minCropResultHeight = ta.getFloat(
                        R.styleable.CropImageView_cropMinCropResultHeightPX,
                        options.minCropResultHeight.toFloat()
                    ).toInt()
                    options.maxCropResultWidth = ta.getFloat(
                        R.styleable.CropImageView_cropMaxCropResultWidthPX,
                        options.maxCropResultWidth.toFloat()
                    ).toInt()
                    options.maxCropResultHeight = ta.getFloat(
                        R.styleable.CropImageView_cropMaxCropResultHeightPX,
                        options.maxCropResultHeight.toFloat()
                    ).toInt()
                    options.flipHorizontally = ta.getBoolean(
                        R.styleable.CropImageView_cropFlipHorizontally, options.flipHorizontally
                    )
                    options.flipVertically = ta.getBoolean(
                        R.styleable.CropImageView_cropFlipHorizontally,
                        options.flipVertically
                    )
                    options.cropperLabelTextSize = ta.getDimension(
                        R.styleable.CropImageView_cropperLabelTextSize,
                        options.cropperLabelTextSize
                    )
                    options.cropperLabelTextColor = ta.getInteger(
                        R.styleable.CropImageView_cropperLabelTextColor,
                        options.cropperLabelTextColor
                    )
                    options.cropperLabelText = ta.getString(
                        R.styleable.CropImageView_cropperLabelText
                    )
                    options.showCropLabel = ta.getBoolean(
                        R.styleable.CropImageView_cropShowLabel,
                        options.showCropLabel
                    )
                    isSaveBitmapToInstanceState = ta.getBoolean(
                        R.styleable.CropImageView_cropSaveBitmapToInstanceState,
                        isSaveBitmapToInstanceState
                    )
                    // if aspect ratio is set then set fixed to true
                    if (ta.hasValue(R.styleable.CropImageView_cropAspectRatioX) &&
                        ta.hasValue(R.styleable.CropImageView_cropAspectRatioX) &&
                        !ta.hasValue(R.styleable.CropImageView_cropFixAspectRatio)
                    ) {
                        options.fixAspectRatio = true
                    }
                } finally {
                    ta.recycle()
                }
            }
        }
        options.validate()
        mScaleType = options.scaleType
        mAutoZoomEnabled = options.autoZoomEnabled
        mMaxZoom = options.maxZoom
        mCropLabelTextSize = options.cropperLabelTextSize
        mShowCropLabel = options.showCropLabel
        mShowCropOverlay = options.showCropOverlay
        mShowProgressBar = options.showProgressBar
        mFlipHorizontally = options.flipHorizontally
        mFlipVertically = options.flipVertically
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.crop_image_view, this, true)
        imageView = v.findViewById(R.id.ImageView_image)
        imageView.scaleType = ImageView.ScaleType.MATRIX
        mCropOverlayView = v.findViewById(R.id.CropOverlayView)
        mCropOverlayView.setCropWindowChangeListener(this)
        mCropOverlayView.setInitialAttributeValues(options)
        mProgressBar = v.findViewById(R.id.CropProgressBar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.indeterminateTintList = ColorStateList.valueOf(options.progressBarColor)
        }
        setProgressBarVisibility()
    }

    override fun onCropWindowChanged(inProgress: Boolean) {
        handleCropWindowChanged(inProgress, true)
        val listener = mOnCropOverlayReleasedListener
        if (listener != null && !inProgress) listener.onCropOverlayReleased(cropRect)
        val movedListener = mOnSetCropOverlayMovedListener
        if (movedListener != null && inProgress) movedListener.onCropOverlayMoved(cropRect)
    }
}
