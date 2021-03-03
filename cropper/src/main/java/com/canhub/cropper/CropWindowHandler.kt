// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.canhub.cropper

import android.graphics.RectF
import com.canhub.cropper.CropImageView.CropShape.OVAL
import com.canhub.cropper.CropImageView.CropShape.RECTANGLE
import com.canhub.cropper.CropImageView.CropShape.RECTANGLE_HORIZONTAL_ONLY
import com.canhub.cropper.CropImageView.CropShape.RECTANGLE_VERTICAL_ONLY
import kotlin.math.abs

/** Handler from crop window stuff, moving and knowing position. */
class CropWindowHandler {

    // region: Fields and Constants
    /** The 4 edges of the crop window defining its coordinates and size  */
    private val mEdges = RectF()

    /**
     * Rectangle used to return the edges rectangle without ability to change it and without creating
     * new all the time.
     */
    private val mGetEdges = RectF()

    /** Minimum width in pixels that the crop window can get.  */
    private var mMinCropWindowWidth = 0f

    /** Minimum height in pixels that the crop window can get.  */
    private var mMinCropWindowHeight = 0f

    /** Maximum width in pixels that the crop window can CURRENTLY get.  */
    private var mMaxCropWindowWidth = 0f

    /** Maximum height in pixels that the crop window can CURRENTLY get.  */
    private var mMaxCropWindowHeight = 0f

    /**
     * Minimum width in pixels that the result of cropping an image can get, affects crop window width
     * adjusted by width scale factor.
     */
    private var mMinCropResultWidth = 0f

    /**
     * Minimum height in pixels that the result of cropping an image can get, affects crop window
     * height adjusted by height scale factor.
     */
    private var mMinCropResultHeight = 0f

    /**
     * Maximum width in pixels that the result of cropping an image can get, affects crop window width
     * adjusted by width scale factor.
     */
    private var mMaxCropResultWidth = 0f

    /**
     * Maximum height in pixels that the result of cropping an image can get, affects crop window
     * height adjusted by height scale factor.
     */
    private var mMaxCropResultHeight = 0f

    /** The width scale factor of shown image and actual image  */
    private var mScaleFactorWidth = 1f

    /** The height scale factor of shown image and actual image  */
    private var mScaleFactorHeight = 1f
    // endregion
    /** Get the left/top/right/bottom coordinates of the crop window.  */
    fun getRect(): RectF {
        mGetEdges.set(mEdges)
        return mGetEdges
    }

    /** Minimum width in pixels that the crop window can get.  */
    fun getMinCropWidth() =
        mMinCropWindowWidth.coerceAtLeast(mMinCropResultWidth / mScaleFactorWidth)

    /** Minimum height in pixels that the crop window can get.  */
    fun getMinCropHeight() =
        mMinCropWindowHeight.coerceAtLeast(mMinCropResultHeight / mScaleFactorHeight)

    /** Maximum width in pixels that the crop window can get.  */
    fun getMaxCropWidth() =
        mMaxCropWindowWidth.coerceAtMost(mMaxCropResultWidth / mScaleFactorWidth)

    /** Maximum height in pixels that the crop window can get.  */
    fun getMaxCropHeight() =
        mMaxCropWindowHeight.coerceAtMost(mMaxCropResultHeight / mScaleFactorHeight)

    /** get the scale factor (on width) of the shown image to original image.  */
    fun getScaleFactorWidth() = mScaleFactorWidth

    /** get the scale factor (on height) of the shown image to original image.  */
    fun getScaleFactorHeight() = mScaleFactorHeight

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMinCropResultSize(minCropResultWidth: Int, minCropResultHeight: Int) {
        mMinCropResultWidth = minCropResultWidth.toFloat()
        mMinCropResultHeight = minCropResultHeight.toFloat()
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMaxCropResultSize(maxCropResultWidth: Int, maxCropResultHeight: Int) {
        mMaxCropResultWidth = maxCropResultWidth.toFloat()
        mMaxCropResultHeight = maxCropResultHeight.toFloat()
    }

    /**
     * set the max width/height and scale factor of the shown image to original image to scale the
     * limits appropriately.
     */
    fun setCropWindowLimits(
        maxWidth: Float,
        maxHeight: Float,
        scaleFactorWidth: Float,
        scaleFactorHeight: Float,
    ) {
        mMaxCropWindowWidth = maxWidth
        mMaxCropWindowHeight = maxHeight
        mScaleFactorWidth = scaleFactorWidth
        mScaleFactorHeight = scaleFactorHeight
    }

    /** Set the variables to be used during crop window handling.  */
    fun setInitialAttributeValues(options: CropImageOptions) {
        mMinCropWindowWidth = options.minCropWindowWidth.toFloat()
        mMinCropWindowHeight = options.minCropWindowHeight.toFloat()
        mMinCropResultWidth = options.minCropResultWidth.toFloat()
        mMinCropResultHeight = options.minCropResultHeight.toFloat()
        mMaxCropResultWidth = options.maxCropResultWidth.toFloat()
        mMaxCropResultHeight = options.maxCropResultHeight.toFloat()
    }

    /** Set the left/top/right/bottom coordinates of the crop window.  */
    fun setRect(rect: RectF) { mEdges.set(rect) }

    /**
     * Indicates whether the crop window is small enough that the guidelines should be shown. Public
     * because this function is also used to determine if the center handle should be focused.
     *
     * @return boolean Whether the guidelines should be shown or not
     */
    fun showGuidelines() = !(mEdges.width() < 100 || mEdges.height() < 100)

    /**
     * Determines which, if any, of the handles are pressed given the touch coordinates, the bounding
     * box, and the touch radius.
     *
     * @param x the x-coordinate of the touch point
     * @param y the y-coordinate of the touch point
     * @param targetRadius the target radius in pixels
     * @return the Handle that was pressed; null if no Handle was pressed
     */
    fun getMoveHandler(
        x: Float,
        y: Float,
        targetRadius: Float,
        cropShape: CropImageView.CropShape,
    ): CropWindowMoveHandler? {
        val type: CropWindowMoveHandler.Type? = when (cropShape) {
            RECTANGLE -> getRectanglePressedMoveType(x, y, targetRadius)
            OVAL -> getOvalPressedMoveType(x, y)
            RECTANGLE_VERTICAL_ONLY -> getRectangleVerticalOnlyPressedMoveType(x, y, targetRadius)
            RECTANGLE_HORIZONTAL_ONLY -> getRectangleHorizontalOnlyPressedMoveType(x, y, targetRadius)
        }

        return if (type != null) CropWindowMoveHandler(type, this, x, y) else null
    }

    // region: Private methods

    /**
     * Determines which, if any, of the handles are pressed given the touch coordinates, the bounding
     * box, and the touch radius.
     *
     * @param x the x-coordinate of the touch point
     * @param y the y-coordinate of the touch point
     * @param targetRadius the target radius in pixels
     * @return the Handle that was pressed; null if no Handle was pressed
     */
    private fun getRectanglePressedMoveType(
        x: Float,
        y: Float,
        targetRadius: Float
    ): CropWindowMoveHandler.Type? {

        if (!isInBounds(x, y, mEdges)) return null

        // Note: corner-handles take precedence, then side-handles, then center.
        return when {
            isCloseToHandle(x, y, mEdges.left, mEdges.top, targetRadius) -> {
                CropWindowMoveHandler.Type.TOP_LEFT
            }
            isCloseToHandle(x, y, mEdges.right, mEdges.top, targetRadius) -> {
                CropWindowMoveHandler.Type.TOP_RIGHT
            }
            isCloseToHandle(x, y, mEdges.left, mEdges.bottom, targetRadius) -> {
                CropWindowMoveHandler.Type.BOTTOM_LEFT
            }
            isCloseToHandle(x, y, mEdges.right, mEdges.bottom, targetRadius) -> {
                CropWindowMoveHandler.Type.BOTTOM_RIGHT
            }
            focusCenter() -> {
                CropWindowMoveHandler.Type.CENTER
            }
            isCloseToHandleInY(y, mEdges.top, targetRadius) -> {
                CropWindowMoveHandler.Type.TOP
            }
            isCloseToHandleInY(y, mEdges.bottom, targetRadius) -> {
                CropWindowMoveHandler.Type.BOTTOM
            }
            isCloseToHandleInX(x, mEdges.left, targetRadius) -> {
                CropWindowMoveHandler.Type.LEFT
            }
            isCloseToHandleInX(x, mEdges.right, targetRadius) -> {
                CropWindowMoveHandler.Type.RIGHT
            }
            else -> CropWindowMoveHandler.Type.CENTER
        }
    }

    /**
     * Determines which, if any, of the handles are pressed given the touch coordinates, the bounding
     * box/oval, and the touch radius.
     *
     * @param x the x-coordinate of the touch point
     * @param y the y-coordinate of the touch point
     * @return the Handle that was pressed; null if no Handle was pressed
     */
    private fun getOvalPressedMoveType(x: Float, y: Float): CropWindowMoveHandler.Type {
        /*
            Use a 6x6 grid system divided into 9 "handles", with the center the biggest region. While
            this is not perfect, it's a good quick-to-ship approach.

            TL T T T T TR
             L C C C C R
             L C C C C R
             L C C C C R
             L C C C C R
            BL B B B B BR
        */

        val cellLength = mEdges.width() / 6
        val leftCenter = mEdges.left + cellLength
        val rightCenter = mEdges.left + 5 * cellLength
        val cellHeight = mEdges.height() / 6
        val topCenter = mEdges.top + cellHeight
        val bottomCenter = mEdges.top + 5 * cellHeight
        return when {
            x < leftCenter -> {
                when {
                    y < topCenter -> CropWindowMoveHandler.Type.TOP_LEFT
                    y < bottomCenter -> CropWindowMoveHandler.Type.LEFT
                    else -> CropWindowMoveHandler.Type.BOTTOM_LEFT
                }
            }
            x < rightCenter -> {
                when {
                    y < topCenter -> CropWindowMoveHandler.Type.TOP
                    y < bottomCenter -> CropWindowMoveHandler.Type.CENTER
                    else -> CropWindowMoveHandler.Type.BOTTOM
                }
            }
            else -> {
                when {
                    y < topCenter -> CropWindowMoveHandler.Type.TOP_RIGHT
                    y < bottomCenter -> CropWindowMoveHandler.Type.RIGHT
                    else -> CropWindowMoveHandler.Type.BOTTOM_RIGHT
                }
            }
        }
    }

    /**
     * Determines which, if any, of the handles are pressed given the touch coordinates, the bounding
     * box, and the touch radius.
     *
     * @param x the x-coordinate of the touch point
     * @param y the y-coordinate of the touch point
     * @param targetRadius the target radius in pixels
     * @return the Handle that was pressed; null if no Handle was pressed
     */
    private fun getRectangleVerticalOnlyPressedMoveType(
        x: Float,
        y: Float,
        targetRadius: Float
    ): CropWindowMoveHandler.Type? {

        if (!isInBounds(x, y, mEdges)) return null

        // Note: top and bottom handles take precedence, then center.
        // Note also that we ignore the focusCenter() function - if the user wants to drag the
        // window they can drag from the left and right sides.
        return when {
            isCloseToHandle(x, y, mEdges.centerX(), mEdges.top, targetRadius) -> {
                CropWindowMoveHandler.Type.TOP
            }
            isCloseToHandle(x, y, mEdges.centerX(), mEdges.bottom, targetRadius) -> {
                CropWindowMoveHandler.Type.BOTTOM
            }
            else -> CropWindowMoveHandler.Type.CENTER
        }
    }

    /**
     * Determines which, if any, of the handles are pressed given the touch coordinates, the bounding
     * box, and the touch radius.
     *
     * @param x the x-coordinate of the touch point
     * @param y the y-coordinate of the touch point
     * @param targetRadius the target radius in pixels
     * @return the Handle that was pressed; null if no Handle was pressed
     */
    private fun getRectangleHorizontalOnlyPressedMoveType(
        x: Float,
        y: Float,
        targetRadius: Float
    ): CropWindowMoveHandler.Type? {

        if (!isInBounds(x, y, mEdges)) return null

        // Note: left and right handles take precedence, then center.
        // Note also that we ignore the focusCenter() function - if the user wants to drag the
        // window they can drag from the top and bottom sides.
        return when {
            isCloseToHandle(x, y, mEdges.left, mEdges.centerY(), targetRadius) -> {
                CropWindowMoveHandler.Type.LEFT
            }
            isCloseToHandle(x, y, mEdges.right, mEdges.centerY(), targetRadius) -> {
                CropWindowMoveHandler.Type.RIGHT
            }
            else -> CropWindowMoveHandler.Type.CENTER
        }
    }

    /**
     * Determines if the specified coordinate is within the given radius of the handle.
     * Note: we actually use the absolute value norm rather than the Euclidean norm.
     *
     * @param x the x-coordinate of the touch point
     * @param y the y-coordinate of the touch point
     * @param handleX the x-coordinate of the corner handle
     * @param handleY the y-coordinate of the corner handle
     * @param targetRadius the target radius in pixels
     * @return true if the touch point is close enough; false otherwise
     */
    private fun isCloseToHandle(
        x: Float,
        y: Float,
        handleX: Float,
        handleY: Float,
        targetRadius: Float,
    ) = abs(x - handleX) <= targetRadius && abs(y - handleY) <= targetRadius

    /**
     * Determines if the specified coordinate is within the given radius of the handle in the
     * vertical dimension only.
     *
     * @param y the y-coordinate of the touch point
     * @param handleY the y-coordinate of the horizontal bar handle
     * @param targetRadius the target radius in pixels
     * @return true if the touch point is close enough; false otherwise
     */
    private fun isCloseToHandleInY(
        y: Float,
        handleY: Float,
        targetRadius: Float,
    ) = abs(y - handleY) <= targetRadius

    /**
     * Determines if the specified coordinate is within the given radius of the handle in the
     * horizontal dimension only.
     *
     * @param x the x-coordinate of the touch point
     * @param handleX the x-coordinate of the vertical bar handle
     * @param targetRadius the target radius in pixels
     * @return true if the touch point is close enough; false otherwise
     */
    private fun isCloseToHandleInX(
        x: Float,
        handleX: Float,
        targetRadius: Float,
    ) = abs(x - handleX) <= targetRadius

    /**
     * Determines if the specified coordinate falls anywhere inside the given bounds.
     *
     * @param x the x-coordinate of the touch point
     * @param y the y-coordinate of the touch point
     * @param bounds the bounds
     * @return true if the touch point is inside the bounding rectangle; false otherwise
     */
    private fun isInBounds(
        x: Float,
        y: Float,
        bounds: RectF,
    ) = x > bounds.left && x < bounds.right && y > bounds.top && y < bounds.bottom

    /**
     * Determines if the cropper should focus on the center handle or the side handles. If it is a
     * small image, focus on the center handle so the user can move it. If it is a large image, focus
     * on the side handles so user can grab them. Corresponds to the appearance of the
     * RuleOfThirdsGuidelines.
     *
     * @return true if it is small enough such that it should focus on the center; less than
     * show_guidelines limit
     */
    private fun focusCenter() = !showGuidelines()
    // endregion
}
