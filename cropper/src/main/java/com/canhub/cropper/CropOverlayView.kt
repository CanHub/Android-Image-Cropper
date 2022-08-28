package com.canhub.cropper

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.annotation.RequiresApi
import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.common.CommonVersionCheck
import java.util.Arrays
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/** A custom View representing the crop window and the shaded background outside the crop window.  */
class CropOverlayView
@JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {

        /**
         * Creates the paint object for drawing text label over crop overlay */
        private fun getTextPaint(options: CropImageOptions): Paint =
            Paint().apply {
                strokeWidth = 1f
                textSize = options.cropperLabelTextSize
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                this.color = options.cropperLabelTextColor
            }
        /** Creates the Paint object for drawing.  */
        private fun getNewPaint(color: Int): Paint =
            Paint().apply {
                this.color = color
            }

        /** Creates the Paint object for given thickness and color, if thickness < 0 return null.  */
        private fun getNewPaintOrNull(thickness: Float, color: Int): Paint? =
            if (thickness > 0) {
                val borderPaint = Paint()
                borderPaint.color = color
                borderPaint.strokeWidth = thickness
                borderPaint.style = Paint.Style.STROKE
                borderPaint.isAntiAlias = true
                borderPaint
            } else null
        private fun getNewPaintWithFill(color: Int): Paint? {
            val borderPaint = Paint()
            borderPaint.color = color
            borderPaint.style = Paint.Style.FILL
            borderPaint.isAntiAlias = true
            return borderPaint
        }
    }
    private var mCropCornerRadius: Float = 0f
    private var mCircleCornerFillColor: Int? = null
    private var mOptions: CropImageOptions? = null

    /** Gesture detector used for multi touch box scaling  */
    private var mScaleDetector: ScaleGestureDetector? = null

    /** Boolean to see if multi touch is enabled for the crop rectangle  */
    private var mMultiTouchEnabled = false

    /** Boolean to see if movement via dragging center is enabled for the crop rectangle  */
    private var mCenterMoveEnabled = true

    /** Handler from crop window stuff, moving and knowing possition.  */
    private val mCropWindowHandler = CropWindowHandler()

    /** Listener to publicj crop window changes  */
    private var mCropWindowChangeListener: CropWindowChangeListener? = null

    /** Rectangle used for drawing  */
    private val mDrawRect = RectF()

    /** The Paint used to draw the white rectangle around the crop area.  */
    private var mBorderPaint: Paint? = null

    /** The Paint used to draw the corners of the Border  */
    private var mBorderCornerPaint: Paint? = null

    /** The Paint used to draw the guidelines within the crop area when pressed.  */
    private var mGuidelinePaint: Paint? = null

    /** The Paint used to darken the surrounding areas outside the crop area.  */
    private var mBackgroundPaint: Paint? = null

    private var textLabelPaint: Paint? = null

    /** Used for oval crop window shape or non-straight rotation drawing.  */
    private val mPath = Path()

    /** The bounding box around the Bitmap that we are cropping.  */
    private val mBoundsPoints = FloatArray(8)

    /** The bounding box around the Bitmap that we are cropping.  */
    private val mCalcBounds = RectF()

    /** The bounding image view width used to know the crop overlay is at view edges.  */
    private var mViewWidth = 0

    /** The bounding image view height used to know the crop overlay is at view edges.  */
    private var mViewHeight = 0

    /** The offset to draw the border corener from the border  */
    private var mBorderCornerOffset = 0f

    /** the length of the border corner to draw  */
    private var mBorderCornerLength = 0f

    /** The initial crop window padding from image borders  */
    private var mInitialCropWindowPaddingRatio = 0f

    /** The radius of the touch zone (in pixels) around a given Handle.  */
    private var mTouchRadius = 0f

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge.
     */
    private var mSnapRadius = 0f

    /** The Handle that is currently pressed; null if no Handle is pressed.  */
    private var mMoveHandler: CropWindowMoveHandler? = null
    /**
     * whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows it to
     * be changed.
     */
    /**
     * Flag indicating if the crop area should always be a certain aspect ratio (indicated by
     * mTargetAspectRatio).
     */
    var isFixAspectRatio = false
        private set

    /** save the current aspect ratio of the image  */
    private var mAspectRatioX = 0

    /** save the current aspect ratio of the image  */
    private var mAspectRatioY = 0

    /**
     * The aspect ratio that the crop area should maintain; this variable is only used when
     * mMaintainAspectRatio is true.
     */
    private var mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY
    /** Get the current guidelines option set.  */
    /** Instance variables for customizable attributes  */
    var guidelines: Guidelines? = null
        private set
    /** The shape of the cropping area - rectangle/circular.  */
    /** The shape of the cropping area - rectangle/circular.  */
    var cropShape: CropShape? = null
        private set
    /**
     * The shape of the crop corner
     */
    var cornerShape: CropImageView.CropCornerShape? = null
        private set

    /** To show the text label over crop overlay **/
    private var isCropLabelEnabled: Boolean = false
    /** Text to show over text label over crop overlay */
    private var cropLabelText: String = ""
    /** Text color to apply over text label over crop overlay */
    private var cropLabelTextSize: Float = 20f
    /** Text color to apply over text label over crop overlay */
    private var cropLabelTextColor = Color.WHITE
    /** the initial crop window rectangle to set  */
    private val mInitialCropWindowRect = Rect()

    /** Whether the Crop View has been initialized for the first time  */
    private var initializedCropWindow = false

    /** Used to set back LayerType after changing to software.  */
    private var mOriginalLayerType: Int? = null

    /** The maximum vertical gesture exclusion allowed by Android (200dp) in px. **/
    private val maxVerticalGestureExclusion = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, Resources.getSystem().displayMetrics)

    /** Set the crop window change listener.  */
    fun setCropWindowChangeListener(listener: CropWindowChangeListener?) {
        mCropWindowChangeListener = listener
    }
    /** Get the left/top/right/bottom coordinates of the crop window.  */
    /** Set the left/top/right/bottom coordinates of the crop window.  */
    var cropWindowRect: RectF
        get() = mCropWindowHandler.getRect()
        set(rect) {
            mCropWindowHandler.setRect(rect)
        }

    /** Fix the current crop window rectangle if it is outside of cropping image or view bounds.  */
    fun fixCurrentCropWindowRect() {
        val rect = cropWindowRect
        fixCropWindowRectByRules(rect)
        mCropWindowHandler.setRect(rect)
    }

    /**
     * Informs the CropOverlayView of the image's position relative to the ImageView. This is
     * necessary to call in order to draw the crop window.
     *
     * @param boundsPoints the image's bounding points
     * @param viewWidth The bounding image view width.
     * @param viewHeight The bounding image view height.
     */
    fun setBounds(boundsPoints: FloatArray?, viewWidth: Int, viewHeight: Int) {
        if (boundsPoints == null || !Arrays.equals(mBoundsPoints, boundsPoints)) {
            if (boundsPoints == null) Arrays.fill(mBoundsPoints, 0f)
            else System.arraycopy(boundsPoints, 0, mBoundsPoints, 0, boundsPoints.size)

            mViewWidth = viewWidth
            mViewHeight = viewHeight
            val cropRect = mCropWindowHandler.getRect()
            if (cropRect.width() == 0f || cropRect.height() == 0f) initCropWindow()
        }
    }

    /** Resets the crop overlay view.  */
    fun resetCropOverlayView() {
        if (initializedCropWindow) {
            cropWindowRect = BitmapUtils.EMPTY_RECT_F
            initCropWindow()
            invalidate()
        }
    }

    /** The shape of the cropping area - rectangle/circular.  */
    fun setCropShape(cropShape: CropShape) {
        if (this.cropShape != cropShape) {
            this.cropShape = cropShape
            if (!CommonVersionCheck.isAtLeastJ18()) {
                if (this.cropShape == CropShape.OVAL) {
                    mOriginalLayerType = layerType

                    if (mOriginalLayerType != LAYER_TYPE_SOFTWARE) setLayerType(
                        LAYER_TYPE_SOFTWARE,
                        null
                    )
                    else mOriginalLayerType = null
                } else if (mOriginalLayerType != null) {
                    // return hardware acceleration back
                    setLayerType(mOriginalLayerType!!, null)
                    mOriginalLayerType = null
                }
            }
            invalidate()
        }
    }

    /**
     * The shape of cropper corners (rectangle / oval)
     */
    fun setCropCornerShape(cropCornerShape: CropImageView.CropCornerShape) {
        if (this.cornerShape != cropCornerShape) {
            this.cornerShape = cropCornerShape
            invalidate()
        }
    }
    /**
     * Sets the cropper label if it is enabled
     */
    fun setCropperTextLabelVisibility(isEnabled: Boolean) {
        this.isCropLabelEnabled = isEnabled
        invalidate()
    }

    /**
     * Sets the copy text for cropper text
     */
    fun setCropLabelText(textLabel: String?) {
        textLabel?.let {
            this.cropLabelText = it
        }
    }

    /**
     * Sets the text size for cropper text
     */
    fun setCropLabelTextSize(textSize: Float) {
        this.cropLabelTextSize = textSize
        invalidate()
    }
    /**
     * Sets the text color for cropper text
     */
    fun setCropLabelTextColor(textColor: Int) {
        this.cropLabelTextColor = textColor
        invalidate()
    }
    /**
     * Sets the guidelines for the CropOverlayView to be either on, off, or to show when resizing the
     * application.
     */
    fun setGuidelines(guidelines: Guidelines) {
        if (this.guidelines != guidelines) {
            this.guidelines = guidelines
            if (initializedCropWindow) {
                invalidate()
            }
        }
    }

    /**
     * Sets whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows
     * it to be changed.
     */
    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        if (isFixAspectRatio != fixAspectRatio) {
            isFixAspectRatio = fixAspectRatio
            if (initializedCropWindow) {
                initCropWindow()
                invalidate()
            }
        }
    }
    /** the X value of the aspect ratio;  */
    /** Sets the X value of the aspect ratio; is defaulted to 1.  */
    var aspectRatioX: Int
        get() = mAspectRatioX
        set(aspectRatioX) {
            require(aspectRatioX > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
            if (mAspectRatioX != aspectRatioX) {
                mAspectRatioX = aspectRatioX
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY
                if (initializedCropWindow) {
                    initCropWindow()
                    invalidate()
                }
            }
        }
    /** the Y value of the aspect ratio;  */
    /**
     * Sets the Y value of the aspect ratio; is defaulted to 1.
     *
     * aspectRatioY int that specifies the new Y value of the aspect ratio
     */
    var aspectRatioY: Int
        get() = mAspectRatioY
        set(aspectRatioY) {
            require(aspectRatioY > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
            if (mAspectRatioY != aspectRatioY) {
                mAspectRatioY = aspectRatioY
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY
                if (initializedCropWindow) {
                    initCropWindow()
                    invalidate()
                }
            }
        }

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (default: 3)
     */
    fun setSnapRadius(snapRadius: Float) {
        mSnapRadius = snapRadius
    }
    /**
     * Radius of the circular crop corner
     * Default radius is 10
     */
    fun setCropCornerRadius(cornerRadius: Float) {
        mCropCornerRadius = cornerRadius
    }

    /** Set multi touch functionality to enabled/disabled.  */
    fun setMultiTouchEnabled(multiTouchEnabled: Boolean): Boolean {
        if (mMultiTouchEnabled != multiTouchEnabled) {
            mMultiTouchEnabled = multiTouchEnabled
            if (mMultiTouchEnabled && mScaleDetector == null) {
                mScaleDetector = ScaleGestureDetector(context, ScaleListener())
            }
            return true
        }
        return false
    }

    /** Set movement of the crop window by dragging the center to enabled/disabled.  */
    fun setCenterMoveEnabled(centerMoveEnabled: Boolean): Boolean {
        if (mCenterMoveEnabled != centerMoveEnabled) {
            mCenterMoveEnabled = centerMoveEnabled
            return true
        }
        return false
    }

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMinCropResultSize(minCropResultWidth: Int, minCropResultHeight: Int) {
        mCropWindowHandler.setMinCropResultSize(minCropResultWidth, minCropResultHeight)
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMaxCropResultSize(maxCropResultWidth: Int, maxCropResultHeight: Int) {
        mCropWindowHandler.setMaxCropResultSize(maxCropResultWidth, maxCropResultHeight)
    }

    /**
     * set the max width/height and scale factor of the shown image to original image to scale the
     * limits appropriately.
     */
    fun setCropWindowLimits(
        maxWidth: Float,
        maxHeight: Float,
        scaleFactorWidth: Float,
        scaleFactorHeight: Float
    ) {
        mCropWindowHandler
            .setCropWindowLimits(maxWidth, maxHeight, scaleFactorWidth, scaleFactorHeight)
    }
    /** Get crop window initial rectangle.  */
    /** Set crop window initial rectangle to be used instead of default.  */
    var initialCropWindowRect: Rect?
        get() = mInitialCropWindowRect
        set(rect) {
            mInitialCropWindowRect.set(rect ?: BitmapUtils.EMPTY_RECT)
            if (initializedCropWindow) {
                initCropWindow()
                invalidate()
                callOnCropWindowChanged(false)
            }
        }

    /** Reset crop window to initial rectangle.  */
    fun resetCropWindowRect() {
        if (initializedCropWindow) {
            initCropWindow()
            invalidate()
            callOnCropWindowChanged(false)
        }
    }

    /**
     * Sets all initial values, but does not call initCropWindow to reset the views.<br></br>
     * Used once at the very start to initialize the attributes.
     */
    fun setInitialAttributeValues(options: CropImageOptions) {
        mOptions = options
        mCropWindowHandler.setInitialAttributeValues(options)
        setCropLabelTextColor(options.cropperLabelTextColor)
        setCropLabelTextSize(options.cropperLabelTextSize)
        setCropLabelText(options.cropperLabelText)
        setCropperTextLabelVisibility(options.showCropLabel)
        setCropCornerRadius(options.cropCornerRadius)
        setCropCornerShape(options.cornerShape)
        setCropShape(options.cropShape)
        setSnapRadius(options.snapRadius)
        setGuidelines(options.guidelines)
        setFixedAspectRatio(options.fixAspectRatio)
        aspectRatioX = options.aspectRatioX
        aspectRatioY = options.aspectRatioY
        setMultiTouchEnabled(options.multiTouchEnabled)
        setCenterMoveEnabled(options.centerMoveEnabled)
        mTouchRadius = options.touchRadius
        mInitialCropWindowPaddingRatio = options.initialCropWindowPaddingRatio
        mBorderPaint = getNewPaintOrNull(options.borderLineThickness, options.borderLineColor)
        mBorderCornerOffset = options.borderCornerOffset
        mBorderCornerLength = options.borderCornerLength
        mCircleCornerFillColor = options.circleCornerFillColorHexValue
        mBorderCornerPaint =
            getNewPaintOrNull(options.borderCornerThickness, options.borderCornerColor)
        mGuidelinePaint = getNewPaintOrNull(options.guidelinesThickness, options.guidelinesColor)
        mBackgroundPaint = getNewPaint(options.backgroundColor)
        textLabelPaint = getTextPaint(options)
    }

    /**
     * Set the initial crop window size and position. This is dependent on the size and position of
     * the image being cropped.
     */
    private fun initCropWindow() {
        val leftLimit = max(BitmapUtils.getRectLeft(mBoundsPoints), 0f)
        val topLimit = max(BitmapUtils.getRectTop(mBoundsPoints), 0f)
        val rightLimit = min(BitmapUtils.getRectRight(mBoundsPoints), width.toFloat())
        val bottomLimit = min(BitmapUtils.getRectBottom(mBoundsPoints), height.toFloat())
        if (rightLimit <= leftLimit || bottomLimit <= topLimit) return
        val rect = RectF()
        // Tells the attribute functions the crop window has already been initialized
        initializedCropWindow = true
        val horizontalPadding = mInitialCropWindowPaddingRatio * (rightLimit - leftLimit)
        val verticalPadding = mInitialCropWindowPaddingRatio * (bottomLimit - topLimit)
        if (mInitialCropWindowRect.width() > 0 && mInitialCropWindowRect.height() > 0) {
            // Get crop window position relative to the displayed image.
            rect.left =
                leftLimit + mInitialCropWindowRect.left / mCropWindowHandler.getScaleFactorWidth()
            rect.top =
                topLimit + mInitialCropWindowRect.top / mCropWindowHandler.getScaleFactorHeight()
            rect.right =
                rect.left + mInitialCropWindowRect.width() / mCropWindowHandler.getScaleFactorWidth()
            rect.bottom =
                rect.top + mInitialCropWindowRect.height() / mCropWindowHandler.getScaleFactorHeight()
            // Correct for floating point errors. Crop rect boundaries should not exceed the source Bitmap
            // bounds.
            rect.left = max(leftLimit, rect.left)
            rect.top = max(topLimit, rect.top)
            rect.right = min(rightLimit, rect.right)
            rect.bottom = min(bottomLimit, rect.bottom)
        } else if (isFixAspectRatio && rightLimit > leftLimit && bottomLimit > topLimit) {
            // If the image aspect ratio is wider than the crop aspect ratio,
            // then the image height is the determining initial length. Else, vice-versa.
            val bitmapAspectRatio = (rightLimit - leftLimit) / (bottomLimit - topLimit)
            if (bitmapAspectRatio > mTargetAspectRatio) {
                rect.top = topLimit + verticalPadding
                rect.bottom = bottomLimit - verticalPadding
                val centerX = width / 2f
                // dirty fix for wrong crop overlay aspect ratio when using fixed aspect ratio
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY
                // Limits the aspect ratio to no less than 40 wide or 40 tall
                val cropWidth = max(
                    mCropWindowHandler.getMinCropWidth(),
                    rect.height() * mTargetAspectRatio
                )
                val halfCropWidth = cropWidth / 2f
                rect.left = centerX - halfCropWidth
                rect.right = centerX + halfCropWidth
            } else {
                rect.left = leftLimit + horizontalPadding
                rect.right = rightLimit - horizontalPadding
                val centerY = height / 2f
                // Limits the aspect ratio to no less than 40 wide or 40 tall
                val cropHeight = max(
                    mCropWindowHandler.getMinCropHeight(),
                    rect.width() / mTargetAspectRatio
                )
                val halfCropHeight = cropHeight / 2f
                rect.top = centerY - halfCropHeight
                rect.bottom = centerY + halfCropHeight
            }
        } else {
            // Initialize crop window to have 10% padding w/ respect to image.
            rect.left = leftLimit + horizontalPadding
            rect.top = topLimit + verticalPadding
            rect.right = rightLimit - horizontalPadding
            rect.bottom = bottomLimit - verticalPadding
        }
        fixCropWindowRectByRules(rect)
        mCropWindowHandler.setRect(rect)
    }

    /** Fix the given rect to fit into bitmap rect and follow min, max and aspect ratio rules.  */
    private fun fixCropWindowRectByRules(rect: RectF) {
        if (rect.width() < mCropWindowHandler.getMinCropWidth()) {
            val adj = (mCropWindowHandler.getMinCropWidth() - rect.width()) / 2
            rect.left -= adj
            rect.right += adj
        }
        if (rect.height() < mCropWindowHandler.getMinCropHeight()) {
            val adj = (mCropWindowHandler.getMinCropHeight() - rect.height()) / 2
            rect.top -= adj
            rect.bottom += adj
        }
        if (rect.width() > mCropWindowHandler.getMaxCropWidth()) {
            val adj = (rect.width() - mCropWindowHandler.getMaxCropWidth()) / 2
            rect.left += adj
            rect.right -= adj
        }
        if (rect.height() > mCropWindowHandler.getMaxCropHeight()) {
            val adj = (rect.height() - mCropWindowHandler.getMaxCropHeight()) / 2
            rect.top += adj
            rect.bottom -= adj
        }
        calculateBounds(rect)

        if (mCalcBounds.width() > 0 && mCalcBounds.height() > 0) {
            val leftLimit = max(mCalcBounds.left, 0f)
            val topLimit = max(mCalcBounds.top, 0f)
            val rightLimit = min(mCalcBounds.right, width.toFloat())
            val bottomLimit = min(mCalcBounds.bottom, height.toFloat())

            if (rect.left < leftLimit) rect.left = leftLimit
            if (rect.top < topLimit) rect.top = topLimit
            if (rect.right > rightLimit) rect.right = rightLimit
            if (rect.bottom > bottomLimit) rect.bottom = bottomLimit
        }
        if (isFixAspectRatio && abs(rect.width() - rect.height() * mTargetAspectRatio) > 0.1) {
            if (rect.width() > rect.height() * mTargetAspectRatio) {
                val adj = abs(rect.height() * mTargetAspectRatio - rect.width()) / 2
                rect.left += adj
                rect.right -= adj
            } else {
                val adj = abs(rect.width() / mTargetAspectRatio - rect.height()) / 2
                rect.top += adj
                rect.bottom -= adj
            }
        }
    }

    /**
     * Draw crop overview by drawing background over image not in the cripping area, then borders and
     * guidelines.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw translucent background for the cropped area.
        drawBackground(canvas)
        if (mCropWindowHandler.showGuidelines()) {
            // Determines whether guidelines should be drawn or not
            if (guidelines == Guidelines.ON) drawGuidelines(canvas)
            else if (guidelines == Guidelines.ON_TOUCH && mMoveHandler != null) drawGuidelines(
                canvas
            )
        }
        // To retain the changes in Paint object when the App goes background this is required
        mBorderCornerPaint = getNewPaintOrNull(mOptions?.borderCornerThickness ?: 0.0f, mOptions?.borderCornerColor ?: Color.WHITE)
        drawCropLabelText(canvas)
        drawBorders(canvas)
        drawCorners(canvas)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setSystemGestureExclusionRects()
        }
    }

    /**
     *  Newer Android phones let you go back by swiping from the left or right edge of the screen inwards.
     *  When the crop window is near the edge it's easy to accidentally swipe back when trying to resize it.
     *  This can be prevented by setting systemGestureExclusionRects. However Android lets you only exclude max 200dp in total vertically.
     *  Therefore a top, middle and bottom strip are used so at least the corners and the vertical middle of the crop window are covered.
     * **/
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setSystemGestureExclusionRects() {
        val cropWindowRect = mCropWindowHandler.getRect()
        val rectTop = systemGestureExclusionRects.getOrElse(0) { Rect() }
        val rectMiddle = systemGestureExclusionRects.getOrElse(1) { Rect() }
        val rectBottom = systemGestureExclusionRects.getOrElse(2) { Rect() }

        rectTop.left = (cropWindowRect.left - mTouchRadius).toInt()
        rectTop.right = (cropWindowRect.right + mTouchRadius).toInt()
        rectTop.top = (cropWindowRect.top - mTouchRadius).toInt()
        rectTop.bottom = (rectTop.top + (maxVerticalGestureExclusion * 0.3f)).toInt()

        rectMiddle.left = rectTop.left
        rectMiddle.right = rectTop.right
        rectMiddle.top = ((cropWindowRect.top + cropWindowRect.bottom) / 2.0f - (maxVerticalGestureExclusion * 0.2f)).toInt()
        rectMiddle.bottom = (rectMiddle.top + (maxVerticalGestureExclusion * 0.4f)).toInt()

        rectBottom.left = rectTop.left
        rectBottom.right = rectTop.right
        rectBottom.bottom = (cropWindowRect.bottom + mTouchRadius).toInt()
        rectBottom.top = (rectBottom.bottom - (maxVerticalGestureExclusion * 0.3f)).toInt()

        systemGestureExclusionRects = listOf(rectTop, rectMiddle, rectBottom)
    }

    /** Draws a text label (which can acts an helper text) on top of crop overlay **/
    private fun drawCropLabelText(canvas: Canvas) {
        if (isCropLabelEnabled) {
            val rect = mCropWindowHandler.getRect()
            var xCoordinate = (rect.left + rect.right) / 2
            var yCoordinate = rect.top - 50
            textLabelPaint?.apply {
                textSize = cropLabelTextSize
                color = cropLabelTextColor
            }
            canvas.drawText(cropLabelText, xCoordinate, yCoordinate, textLabelPaint!!)
            canvas.save()
        }
    }

    /** Draw shadow background over the image not including the crop area.  */
    private fun drawBackground(canvas: Canvas) {
        val rect = mCropWindowHandler.getRect()
        val left = max(BitmapUtils.getRectLeft(mBoundsPoints), 0f)
        val top = max(BitmapUtils.getRectTop(mBoundsPoints), 0f)
        val right = min(BitmapUtils.getRectRight(mBoundsPoints), width.toFloat())
        val bottom = min(BitmapUtils.getRectBottom(mBoundsPoints), height.toFloat())
        when (cropShape) {
            CropShape.RECTANGLE,
            CropShape.RECTANGLE_VERTICAL_ONLY,
            CropShape.RECTANGLE_HORIZONTAL_ONLY ->
                if (!isNonStraightAngleRotated || !CommonVersionCheck.isAtLeastJ18()) {
                    canvas.drawRect(left, top, right, rect.top, mBackgroundPaint!!)
                    canvas.drawRect(left, rect.bottom, right, bottom, mBackgroundPaint!!)
                    canvas.drawRect(left, rect.top, rect.left, rect.bottom, mBackgroundPaint!!)
                    canvas.drawRect(rect.right, rect.top, right, rect.bottom, mBackgroundPaint!!)
                } else {
                    mPath.reset()
                    mPath.moveTo(mBoundsPoints[0], mBoundsPoints[1])
                    mPath.lineTo(mBoundsPoints[2], mBoundsPoints[3])
                    mPath.lineTo(mBoundsPoints[4], mBoundsPoints[5])
                    mPath.lineTo(mBoundsPoints[6], mBoundsPoints[7])
                    mPath.close()
                    canvas.save()

                    if (CommonVersionCheck.isAtLeastO26()) canvas.clipOutPath(mPath)
                    else canvas.clipPath(mPath, Region.Op.INTERSECT)

                    canvas.clipRect(rect, Region.Op.XOR)
                    canvas.drawRect(left, top, right, bottom, mBackgroundPaint!!)
                    canvas.restore()
                }
            CropShape.OVAL -> {
                mPath.reset()
                if (CommonVersionCheck.isAtLeastJ18())
                    mDrawRect[rect.left, rect.top, rect.right] = rect.bottom
                else mDrawRect[rect.left + 2, rect.top + 2, rect.right - 2] = rect.bottom - 2

                mPath.addOval(mDrawRect, Path.Direction.CW)
                canvas.save()

                if (CommonVersionCheck.isAtLeastO26()) canvas.clipOutPath(mPath)
                else canvas.clipPath(mPath, Region.Op.XOR)

                canvas.drawRect(left, top, right, bottom, mBackgroundPaint!!)
                canvas.restore()
            }
            else -> throw IllegalStateException("Unrecognized crop shape")
        }
    }

    /**
     * Draw 2 vertical and 2 horizontal guidelines inside the cropping area to split it into 9 equal
     * parts.
     */
    private fun drawGuidelines(canvas: Canvas) {
        if (mGuidelinePaint != null) {
            val sw: Float = if (mBorderPaint != null) mBorderPaint!!.strokeWidth else 0f
            val rect = mCropWindowHandler.getRect()
            rect.inset(sw, sw)
            val oneThirdCropWidth = rect.width() / 3
            val oneThirdCropHeight = rect.height() / 3
            val x1: Float
            val x2: Float
            val y1: Float
            val y2: Float
            when (cropShape) {
                CropShape.OVAL -> {
                    val w = rect.width() / 2 - sw
                    val h = rect.height() / 2 - sw
                    // Draw vertical guidelines.
                    x1 = rect.left + oneThirdCropWidth
                    x2 = rect.right - oneThirdCropWidth
                    val yv = (h * sin(acos(((w - oneThirdCropWidth) / w).toDouble()))).toFloat()

                    canvas
                        .drawLine(
                            x1,
                            rect.top + h - yv,
                            x1,
                            rect.bottom - h + yv,
                            mGuidelinePaint!!
                        )
                    canvas
                        .drawLine(
                            x2,
                            rect.top + h - yv,
                            x2,
                            rect.bottom - h + yv,
                            mGuidelinePaint!!
                        )
                    // Draw horizontal guidelines.
                    y1 = rect.top + oneThirdCropHeight
                    y2 = rect.bottom - oneThirdCropHeight
                    val xv = (w * cos(asin(((h - oneThirdCropHeight) / h).toDouble()))).toFloat()
                    canvas
                        .drawLine(
                            rect.left + w - xv,
                            y1,
                            rect.right - w + xv,
                            y1,
                            mGuidelinePaint!!
                        )
                    canvas
                        .drawLine(
                            rect.left + w - xv,
                            y2,
                            rect.right - w + xv,
                            y2,
                            mGuidelinePaint!!
                        )
                }
                CropShape.RECTANGLE,
                CropShape.RECTANGLE_VERTICAL_ONLY,
                CropShape.RECTANGLE_HORIZONTAL_ONLY -> {
                    // Draw vertical guidelines.
                    x1 = rect.left + oneThirdCropWidth
                    x2 = rect.right - oneThirdCropWidth
                    canvas.drawLine(x1, rect.top, x1, rect.bottom, mGuidelinePaint!!)
                    canvas.drawLine(x2, rect.top, x2, rect.bottom, mGuidelinePaint!!)
                    // Draw horizontal guidelines.
                    y1 = rect.top + oneThirdCropHeight
                    y2 = rect.bottom - oneThirdCropHeight
                    canvas.drawLine(rect.left, y1, rect.right, y1, mGuidelinePaint!!)
                    canvas.drawLine(rect.left, y2, rect.right, y2, mGuidelinePaint!!)
                }
                else -> throw IllegalStateException("Unrecognized crop shape")
            }
        }
    }

    /** Draw borders of the crop area.  */
    private fun drawBorders(canvas: Canvas) {
        if (mBorderPaint != null) {
            val w = mBorderPaint!!.strokeWidth
            val rect = mCropWindowHandler.getRect()
            rect.inset(w / 2, w / 2)

            when (cropShape) {
                // Draw rectangle crop window border.
                CropShape.RECTANGLE_VERTICAL_ONLY,
                CropShape.RECTANGLE_HORIZONTAL_ONLY,
                CropShape.RECTANGLE -> canvas.drawRect(rect, mBorderPaint!!)
                // Draw circular crop window border
                CropShape.OVAL -> canvas.drawOval(rect, mBorderPaint!!)
                else -> throw IllegalStateException("Unrecognized crop shape")
            }
        }
    }

    /** Draw the corner of crop overlay.  */
    private fun drawCorners(canvas: Canvas) {
        if (mBorderCornerPaint != null) {
            val lineWidth: Float = if (mBorderPaint != null) mBorderPaint!!.strokeWidth else 0f
            val cornerWidth = mBorderCornerPaint!!.strokeWidth
            val cornerOffset = (cornerWidth - lineWidth) / 2
            val cornerExtension = cornerWidth / 2 + cornerOffset
            val w: Float = when (cropShape) {
                // for rectangle crop shape we allow the corners to be offset from the borders
                CropShape.RECTANGLE_VERTICAL_ONLY,
                CropShape.RECTANGLE_HORIZONTAL_ONLY,
                CropShape.RECTANGLE -> cornerWidth / 2 + mBorderCornerOffset
                CropShape.OVAL -> cornerWidth / 2
                else -> throw IllegalStateException("Unrecognized crop shape")
            }
            val rect = mCropWindowHandler.getRect()
            rect.inset(w, w)
            drawCornerBasedOnShape(canvas, rect, cornerOffset, cornerExtension)
            if (cornerShape == CropImageView.CropCornerShape.OVAL) {
                // To draw fill color updated paint object is needed and the corners to be redrawn
                mBorderCornerPaint = mCircleCornerFillColor?.let { getNewPaintWithFill(it) }
                drawCornerBasedOnShape(canvas, rect, cornerOffset, cornerExtension)
            }
        }
    }
    /**
     * Draw corners of crop overlay based on crop shape.
     * In case of RECTANGLE crop shape call [drawCornerShape], to handle the corner draw based on
     * crop corner shape.
     */
    private fun drawCornerBasedOnShape(
        canvas: Canvas,
        rect: RectF,
        cornerOffset: Float,
        cornerExtension: Float
    ) {
        when (cropShape) {
            CropShape.RECTANGLE -> {
                drawCornerShape(canvas, rect, cornerOffset, cornerExtension, mCropCornerRadius)
            }
            CropShape.OVAL -> {
                drawLineShape(canvas, rect, cornerOffset, cornerExtension)
            }
            CropShape.RECTANGLE_VERTICAL_ONLY -> {
                // For the vertical variant, the user can only resize the crop window up and down, so the
                // "corners" are in the center of the top and bottom edges of the crop window.
                canvas.drawLine(
                    rect.centerX() - mBorderCornerLength,
                    rect.top - cornerOffset,
                    rect.centerX() + mBorderCornerLength,
                    rect.top - cornerOffset,
                    mBorderCornerPaint!!
                )
                canvas.drawLine(
                    rect.centerX() - mBorderCornerLength,
                    rect.bottom + cornerOffset,
                    rect.centerX() + mBorderCornerLength,
                    rect.bottom + cornerOffset,
                    mBorderCornerPaint!!
                )
            }
            CropShape.RECTANGLE_HORIZONTAL_ONLY -> {
                // For the horizontal variant, the user can only resize the crop window left and right, so
                // the "corners" are in the center of the left and right edges of the crop window.
                canvas.drawLine(
                    rect.left - cornerOffset,
                    rect.centerY() - mBorderCornerLength,
                    rect.left - cornerOffset,
                    rect.centerY() + mBorderCornerLength,
                    mBorderCornerPaint!!
                )
                canvas.drawLine(
                    rect.right + cornerOffset,
                    rect.centerY() - mBorderCornerLength,
                    rect.right + cornerOffset,
                    rect.centerY() + mBorderCornerLength,
                    mBorderCornerPaint!!
                )
            }
            else -> throw IllegalStateException("Unrecognized crop shape")
        }
    }

    /**
     * Draws rectangle shape corners
     */
    private fun drawLineShape(
        canvas: Canvas,
        rect: RectF,
        cornerOffset: Float,
        cornerExtension: Float
    ) {
        // Top left
        canvas.drawLine(
            rect.left - cornerOffset,
            rect.top - cornerExtension,
            rect.left - cornerOffset,
            rect.top + mBorderCornerLength,
            mBorderCornerPaint!!
        )
        canvas.drawLine(
            rect.left - cornerExtension,
            rect.top - cornerOffset,
            rect.left + mBorderCornerLength,
            rect.top - cornerOffset,
            mBorderCornerPaint!!
        )
        // Top right
        canvas.drawLine(
            rect.right + cornerOffset,
            rect.top - cornerExtension,
            rect.right + cornerOffset,
            rect.top + mBorderCornerLength,
            mBorderCornerPaint!!
        )
        canvas.drawLine(
            rect.right + cornerExtension,
            rect.top - cornerOffset,
            rect.right - mBorderCornerLength,
            rect.top - cornerOffset,
            mBorderCornerPaint!!
        )
        // Bottom left
        canvas.drawLine(
            rect.left - cornerOffset,
            rect.bottom + cornerExtension,
            rect.left - cornerOffset,
            rect.bottom - mBorderCornerLength,
            mBorderCornerPaint!!
        )
        canvas.drawLine(
            rect.left - cornerExtension,
            rect.bottom + cornerOffset,
            rect.left + mBorderCornerLength,
            rect.bottom + cornerOffset,
            mBorderCornerPaint!!
        )
        // Bottom right
        canvas.drawLine(
            rect.right + cornerOffset,
            rect.bottom + cornerExtension,
            rect.right + cornerOffset,
            rect.bottom - mBorderCornerLength,
            mBorderCornerPaint!!
        )
        canvas.drawLine(
            rect.right + cornerExtension,
            rect.bottom + cornerOffset,
            rect.right - mBorderCornerLength,
            rect.bottom + cornerOffset,
            mBorderCornerPaint!!
        )
    }

    /**
     * Based on corner shape calls either [drawCircleShape] or [drawLineShape]
     */
    private fun drawCornerShape(
        canvas: Canvas,
        rect: RectF,
        cornerOffset: Float,
        cornerExtension: Float,
        radius: Float
    ) {
        when (cornerShape) {
            CropImageView.CropCornerShape.OVAL -> {
                drawCircleShape(canvas, rect, cornerOffset, cornerExtension, radius)
            }
            CropImageView.CropCornerShape.RECTANGLE -> drawLineShape(canvas, rect, cornerOffset, cornerExtension)
            null -> Unit
        }
    }

    /**
     * Draws circle shape corners
     */
    private fun drawCircleShape(
        canvas: Canvas,
        rect: RectF,
        cornerOffset: Float,
        cornerExtension: Float,
        radius: Float
    ) {
        // Top left
        canvas.drawCircle(
            rect.left - cornerExtension,
            (rect.top - cornerExtension),
            radius,
            mBorderCornerPaint!!
        )
        // Top right
        canvas.drawCircle(
            rect.right + cornerExtension,
            rect.top - cornerExtension,
            radius,
            mBorderCornerPaint!!
        )
        // Bottom left
        canvas.drawCircle(
            rect.left - cornerExtension,
            rect.bottom + cornerExtension,
            radius,
            mBorderCornerPaint!!
        )
        // Bottom right
        canvas.drawCircle(
            rect.right + cornerExtension,
            rect.bottom + cornerExtension,
            radius,
            mBorderCornerPaint!!
        )
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // If this View is not enabled, don't allow for touch interactions.
        return if (isEnabled) {
            if (mMultiTouchEnabled) mScaleDetector?.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onActionDown(event.x, event.y)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    onActionUp()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    onActionMove(event.x, event.y)
                    parent.requestDisallowInterceptTouchEvent(true)
                    true
                }
                else -> false
            }
        } else false
    }

    /**
     * On press down start crop window movment depending on the location of the press.<br></br>
     * if press is far from crop window then no move handler is returned (null).
     */
    private fun onActionDown(x: Float, y: Float) {
        mMoveHandler =
            mCropWindowHandler.getMoveHandler(x, y, mTouchRadius, cropShape!!, mCenterMoveEnabled)

        if (mMoveHandler != null) invalidate()
    }

    /** Clear move handler starting in [.onActionDown] if exists.  */
    private fun onActionUp() {
        if (mMoveHandler != null) {
            mMoveHandler = null
            callOnCropWindowChanged(false)
            invalidate()
        }
    }

    /**
     * Handle move of crop window using the move handler created in [.onActionDown].<br></br>
     * The move handler will do the proper move/resize of the crop window.
     */
    private fun onActionMove(x: Float, y: Float) {
        if (mMoveHandler != null) {
            var snapRadius = mSnapRadius
            val rect = mCropWindowHandler.getRect()
            if (calculateBounds(rect)) {
                snapRadius = 0f
            }
            mMoveHandler!!.move(
                rect,
                x,
                y,
                mCalcBounds,
                mViewWidth,
                mViewHeight,
                snapRadius,
                isFixAspectRatio,
                mTargetAspectRatio
            )
            mCropWindowHandler.setRect(rect)
            callOnCropWindowChanged(true)
            invalidate()
        }
    }

    /**
     * Calculate the bounding rectangle for current crop window, handle non-straight rotation angles.
     * <br></br>
     * If the rotation angle is straight then the bounds rectangle is the bitmap rectangle, otherwsie
     * we find the max rectangle that is within the image bounds starting from the crop window
     * rectangle.
     *
     * @param rect the crop window rectangle to start finsing bounded rectangle from
     * @return true - non straight rotation in place, false - otherwise.
     */
    private fun calculateBounds(rect: RectF): Boolean {
        var left = BitmapUtils.getRectLeft(mBoundsPoints)
        var top = BitmapUtils.getRectTop(mBoundsPoints)
        var right = BitmapUtils.getRectRight(mBoundsPoints)
        var bottom = BitmapUtils.getRectBottom(mBoundsPoints)

        return if (!isNonStraightAngleRotated) {
            mCalcBounds[left, top, right] = bottom
            false
        } else {
            var x0 = mBoundsPoints[0]
            var y0 = mBoundsPoints[1]
            var x2 = mBoundsPoints[4]
            var y2 = mBoundsPoints[5]
            var x3 = mBoundsPoints[6]
            var y3 = mBoundsPoints[7]
            if (mBoundsPoints[7] < mBoundsPoints[1]) {
                if (mBoundsPoints[1] < mBoundsPoints[3]) {
                    x0 = mBoundsPoints[6]
                    y0 = mBoundsPoints[7]
                    x2 = mBoundsPoints[2]
                    y2 = mBoundsPoints[3]
                    x3 = mBoundsPoints[4]
                    y3 = mBoundsPoints[5]
                } else {
                    x0 = mBoundsPoints[4]
                    y0 = mBoundsPoints[5]
                    x2 = mBoundsPoints[0]
                    y2 = mBoundsPoints[1]
                    x3 = mBoundsPoints[2]
                    y3 = mBoundsPoints[3]
                }
            } else if (mBoundsPoints[1] > mBoundsPoints[3]) {
                x0 = mBoundsPoints[2]
                y0 = mBoundsPoints[3]
                x2 = mBoundsPoints[6]
                y2 = mBoundsPoints[7]
                x3 = mBoundsPoints[0]
                y3 = mBoundsPoints[1]
            }
            val a0 = (y3 - y0) / (x3 - x0)
            val a1 = -1f / a0
            val b0 = y0 - a0 * x0
            val b1 = y0 - a1 * x0
            val b2 = y2 - a0 * x2
            val b3 = y2 - a1 * x2
            val c0 = (rect.centerY() - rect.top) / (rect.centerX() - rect.left)
            val c1 = -c0
            val d0 = rect.top - c0 * rect.left
            val d1 = rect.top - c1 * rect.right
            left = max(
                left,
                if ((d0 - b0) / (a0 - c0) < rect.right) (d0 - b0) / (a0 - c0) else left
            )
            left = max(
                left,
                if ((d0 - b1) / (a1 - c0) < rect.right) (d0 - b1) / (a1 - c0) else left
            )
            left = max(
                left,
                if ((d1 - b3) / (a1 - c1) < rect.right) (d1 - b3) / (a1 - c1) else left
            )
            right = min(
                right,
                if ((d1 - b1) / (a1 - c1) > rect.left) (d1 - b1) / (a1 - c1) else right
            )
            right = min(
                right,
                if ((d1 - b2) / (a0 - c1) > rect.left) (d1 - b2) / (a0 - c1) else right
            )
            right = min(
                right,
                if ((d0 - b2) / (a0 - c0) > rect.left) (d0 - b2) / (a0 - c0) else right
            )
            top = max(top, max(a0 * left + b0, a1 * right + b1))
            bottom = min(bottom, min(a1 * left + b3, a0 * right + b2))
            mCalcBounds.left = left
            mCalcBounds.top = top
            mCalcBounds.right = right
            mCalcBounds.bottom = bottom
            true
        }
    }

    /** Is the cropping image has been rotated by NOT 0,90,180 or 270 degrees.  */
    private val isNonStraightAngleRotated: Boolean
        get() = mBoundsPoints[0] != mBoundsPoints[6] && mBoundsPoints[1] != mBoundsPoints[7]

    /** Invoke on crop change listener safe, don't let the app crash on exception.  */
    private fun callOnCropWindowChanged(inProgress: Boolean) {
        try {
            mCropWindowChangeListener?.onCropWindowChanged(inProgress)
        } catch (e: Exception) {
            Log.e("AIC", "Exception in crop window changed", e)
        }
    }

    /** Interface definition for a callback to be invoked when crop window rectangle is changing.  */
    fun interface CropWindowChangeListener {

        /**
         * Called after a change in crop window rectangle.
         *
         * @param inProgress is the crop window change operation is still in progress by user touch
         */
        fun onCropWindowChanged(inProgress: Boolean)
    }

    /** Handle scaling the rectangle based on two finger input  */
    private inner class ScaleListener : SimpleOnScaleGestureListener() {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val rect = mCropWindowHandler.getRect()
            val x = detector.focusX
            val y = detector.focusY
            val dY = detector.currentSpanY / 2
            val dX = detector.currentSpanX / 2
            val newTop = y - dY
            val newLeft = x - dX
            val newRight = x + dX
            val newBottom = y + dY

            if (newLeft < newRight &&
                newTop <= newBottom &&
                newLeft >= 0 &&
                newRight <= mCropWindowHandler.getMaxCropWidth() &&
                newTop >= 0 &&
                newBottom <= mCropWindowHandler.getMaxCropHeight()
            ) {
                rect[newLeft, newTop, newRight] = newBottom
                mCropWindowHandler.setRect(rect)
                invalidate()
            }
            return true
        }
    }
}
