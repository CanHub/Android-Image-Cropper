package com.canhub.cropper

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.annotation.DrawableRes
import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageView.RequestSizeOptions

data class CropImageContractOptions(
    val uri: Uri?,
    val options: CropImageOptions
) {

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
     * [setFixAspectRatio] } (with argument `false`) and
     * [setInitialCropWindowPaddingRatio] (with argument `0f).
     */
    fun setCropShape(cropShape: CropShape): CropImageContractOptions {
        options.cropShape = cropShape
        return this
    }

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box
     * when the crop window edge is less than or equal to this distance (in pixels) away from the
     * bounding box edge (in pixels).<br></br>
     * *Default: 3dp*
     */
    fun setSnapRadius(snapRadius: Float): CropImageContractOptions {
        options.snapRadius = snapRadius
        return this
    }

    /**
     * The radius of the touchable area around the handle (in pixels).<br></br>
     * We are basing this value off of the recommended 48dp Rhythm.<br></br>
     * See: http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm<br></br>
     * *Default: 48dp*
     */
    fun setTouchRadius(touchRadius: Float): CropImageContractOptions {
        options.touchRadius = touchRadius
        return this
    }

    /**
     * whether the guidelines should be on, off, or only showing when resizing.<br></br>
     * *Default: ON_TOUCH*
     */
    fun setGuidelines(guidelines: Guidelines): CropImageContractOptions {
        options.guidelines = guidelines
        return this
    }

    /**
     * The initial scale type of the image in the crop image view<br></br>
     * *Default: FIT_CENTER*
     */
    fun setScaleType(scaleType: CropImageView.ScaleType): CropImageContractOptions {
        options.scaleType = scaleType
        return this
    }

    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * *default: true, may disable for animation or frame transition.*
     */
    fun setShowCropOverlay(showCropOverlay: Boolean): CropImageContractOptions {
        options.showCropOverlay = showCropOverlay
        return this
    }

    /**
     * if auto-zoom functionality is enabled.<br></br>
     * default: true.
     */
    fun setAutoZoomEnabled(autoZoomEnabled: Boolean): CropImageContractOptions {
        options.autoZoomEnabled = autoZoomEnabled
        return this
    }

    /**
     * if multi touch functionality is enabled.<br></br>
     * default: true.
     */
    fun setMultiTouchEnabled(multiTouchEnabled: Boolean): CropImageContractOptions {
        options.multiTouchEnabled = multiTouchEnabled
        return this
    }

    /**
     * if the crop window can be moved by dragging the center.<br></br>
     * default: true
     */
    fun setCenterMoveEnabled(centerMoveEnabled: Boolean): CropImageContractOptions {
        options.centerMoveEnabled = centerMoveEnabled
        return this
    }

    /**
     * The max zoom allowed during cropping.<br></br>
     * *Default: 4*
     */
    fun setMaxZoom(maxZoom: Int): CropImageContractOptions {
        options.maxZoom = maxZoom
        return this
    }

    /**
     * The initial crop window padding from image borders in percentage of the cropping image
     * dimensions.<br></br>
     * *Default: 0.1*
     */
    fun setInitialCropWindowPaddingRatio(initialCropWindowPaddingRatio: Float): CropImageContractOptions {
        options.initialCropWindowPaddingRatio = initialCropWindowPaddingRatio
        return this
    }

    /**
     * whether the width to height aspect ratio should be maintained or free to change.<br></br>
     * *Default: false*
     */
    fun setFixAspectRatio(fixAspectRatio: Boolean): CropImageContractOptions {
        options.fixAspectRatio = fixAspectRatio
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
    fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int): CropImageContractOptions {
        options.aspectRatioX = aspectRatioX
        options.aspectRatioY = aspectRatioY
        options.fixAspectRatio = true
        return this
    }

    /**
     * the thickness of the guidelines lines (in pixels).<br></br>
     * *Default: 3dp*
     */
    fun setBorderLineThickness(borderLineThickness: Float): CropImageContractOptions {
        options.borderLineThickness = borderLineThickness
        return this
    }

    /**
     * the color of the guidelines lines.<br></br>
     * *Default: Color.argb(170, 255, 255, 255)*
     */
    fun setBorderLineColor(borderLineColor: Int): CropImageContractOptions {
        options.borderLineColor = borderLineColor
        return this
    }

    /**
     * thickness of the corner line (in pixels).<br></br>
     * *Default: 2dp*
     */
    fun setBorderCornerThickness(borderCornerThickness: Float): CropImageContractOptions {
        options.borderCornerThickness = borderCornerThickness
        return this
    }

    /**
     * the offset of corner line from crop window border (in pixels).<br></br>
     * *Default: 5dp*
     */
    fun setBorderCornerOffset(borderCornerOffset: Float): CropImageContractOptions {
        options.borderCornerOffset = borderCornerOffset
        return this
    }

    /**
     * the length of the corner line away from the corner (in pixels).<br></br>
     * *Default: 14dp*
     */
    fun setBorderCornerLength(borderCornerLength: Float): CropImageContractOptions {
        options.borderCornerLength = borderCornerLength
        return this
    }

    /**
     * the color of the corner line.<br></br>
     * *Default: WHITE*
     */
    fun setBorderCornerColor(borderCornerColor: Int): CropImageContractOptions {
        options.borderCornerColor = borderCornerColor
        return this
    }

    /**
     * the thickness of the guidelines lines (in pixels).<br></br>
     * *Default: 1dp*
     */
    fun setGuidelinesThickness(guidelinesThickness: Float): CropImageContractOptions {
        options.guidelinesThickness = guidelinesThickness
        return this
    }

    /**
     * the color of the guidelines lines.<br></br>
     * *Default: Color.argb(170, 255, 255, 255)*
     */
    fun setGuidelinesColor(guidelinesColor: Int): CropImageContractOptions {
        options.guidelinesColor = guidelinesColor
        return this
    }

    /**
     * the color of the overlay background around the crop window cover the image parts not in the
     * crop window.<br></br>
     * *Default: Color.argb(119, 0, 0, 0)*
     */
    fun setBackgroundColor(backgroundColor: Int): CropImageContractOptions {
        options.backgroundColor = backgroundColor
        return this
    }

    /**
     * the min size the crop window is allowed to be (in pixels).<br></br>
     * *Default: 42dp, 42dp*
     */
    fun setMinCropWindowSize(
        minCropWindowWidth: Int,
        minCropWindowHeight: Int
    ): CropImageContractOptions {
        options.minCropWindowWidth = minCropWindowWidth
        options.minCropWindowHeight = minCropWindowHeight
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
    ): CropImageContractOptions {
        options.minCropResultWidth = minCropResultWidth
        options.minCropResultHeight = minCropResultHeight
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
    ): CropImageContractOptions {
        options.maxCropResultWidth = maxCropResultWidth
        options.maxCropResultHeight = maxCropResultHeight
        return this
    }

    /**
     * the title of the [CropImageActivity].<br></br>
     * *Default: ""*
     */
    fun setActivityTitle(activityTitle: CharSequence): CropImageContractOptions {
        options.activityTitle = activityTitle
        return this
    }

    /**
     * the color to use for action bar items icons.<br></br>
     * *Default: NONE*
     */
    fun setActivityMenuIconColor(activityMenuIconColor: Int): CropImageContractOptions {
        options.activityMenuIconColor = activityMenuIconColor
        return this
    }

    /**
     * the Android Uri to save the cropped image to.<br></br>
     * *Default: NONE, will create a temp file*
     */
    fun setOutputUri(outputUri: Uri?): CropImageContractOptions {
        options.outputUri = outputUri
        return this
    }

    /**
     * the compression format to use when writting the image.<br></br>
     * *Default: JPEG*
     */
    fun setOutputCompressFormat(outputCompressFormat: Bitmap.CompressFormat): CropImageContractOptions {
        options.outputCompressFormat = outputCompressFormat
        return this
    }

    /**
     * the quility (if applicable) to use when writting the image (0 - 100).<br></br>
     * *Default: 90*
     */
    fun setOutputCompressQuality(outputCompressQuality: Int): CropImageContractOptions {
        options.outputCompressQuality = outputCompressQuality
        return this
    }

    /**
     * the size to resize the cropped image to.<br></br>
     * Uses [CropImageView.RequestSizeOptions.RESIZE_INSIDE] option.<br></br>
     * *Default: 0, 0 - not set, will not resize*
     */
    fun setRequestedSize(reqWidth: Int, reqHeight: Int): CropImageContractOptions {
        return setRequestedSize(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE)
    }

    /**
     * the size to resize the cropped image to.<br></br>
     * *Default: 0, 0 - not set, will not resize*
     */
    fun setRequestedSize(
        reqWidth: Int,
        reqHeight: Int,
        reqSizeOptions: RequestSizeOptions,
    ): CropImageContractOptions {
        options.outputRequestWidth = reqWidth
        options.outputRequestHeight = reqHeight
        options.outputRequestSizeOptions = reqSizeOptions
        return this
    }

    /**
     * if the result of crop image activity should not save the cropped image bitmap.<br></br>
     * Used if you want to crop the image manually and need only the crop rectangle and rotation
     * data.<br></br>
     * *Default: false*
     */
    fun setNoOutputImage(noOutputImage: Boolean): CropImageContractOptions {
        options.noOutputImage = noOutputImage
        return this
    }

    /**
     * the initial rectangle to set on the cropping image after loading.<br></br>
     * *Default: NONE - will initialize using initial crop window padding ratio*
     */
    fun setInitialCropWindowRectangle(initialCropWindowRectangle: Rect?): CropImageContractOptions {
        options.initialCropWindowRectangle = initialCropWindowRectangle
        return this
    }

    /**
     * the initial rotation to set on the cropping image after loading (0-360 degrees clockwise).
     * <br></br>
     * *Default: NONE - will read image exif data*
     */
    fun setInitialRotation(initialRotation: Int): CropImageContractOptions {
        options.initialRotation = (initialRotation + 360) % 360
        return this
    }

    /**
     * if to allow rotation during cropping.<br></br>
     * *Default: true*
     */
    fun setAllowRotation(allowRotation: Boolean): CropImageContractOptions {
        options.allowRotation = allowRotation
        return this
    }

    /**
     * if to allow flipping during cropping.<br></br>
     * *Default: true*
     */
    fun setAllowFlipping(allowFlipping: Boolean): CropImageContractOptions {
        options.allowFlipping = allowFlipping
        return this
    }

    /**
     * if to allow counter-clockwise rotation during cropping.<br></br>
     * Note: if rotation is disabled this option has no effect.<br></br>
     * *Default: false*
     */
    fun setAllowCounterRotation(allowCounterRotation: Boolean): CropImageContractOptions {
        options.allowCounterRotation = allowCounterRotation
        return this
    }

    /**
     * The amount of degreees to rotate clockwise or counter-clockwise (0-360).<br></br>
     * *Default: 90*
     */
    fun setRotationDegrees(rotationDegrees: Int): CropImageContractOptions {
        options.rotationDegrees = (rotationDegrees + 360) % 360
        return this
    }

    /**
     * whether the image should be flipped horizontally.<br></br>
     * *Default: false*
     */
    fun setFlipHorizontally(flipHorizontally: Boolean): CropImageContractOptions {
        options.flipHorizontally = flipHorizontally
        return this
    }

    /**
     * whether the image should be flipped vertically.<br></br>
     * *Default: false*
     */
    fun setFlipVertically(flipVertically: Boolean): CropImageContractOptions {
        options.flipVertically = flipVertically
        return this
    }

    /**
     * optional, set crop menu crop button title.<br></br>
     * *Default: null, will use resource string: crop_image_menu_crop*
     */
    fun setCropMenuCropButtonTitle(title: CharSequence?): CropImageContractOptions {
        options.cropMenuCropButtonTitle = title
        return this
    }

    /**
     * Image resource id to use for crop icon instead of text.<br></br>
     * *Default: 0*
     */
    fun setCropMenuCropButtonIcon(@DrawableRes drawableResource: Int): CropImageContractOptions {
        options.cropMenuCropButtonIcon = drawableResource
        return this
    }
}

fun options(
    uri: Uri? = null,
    builder: CropImageContractOptions.() -> (Unit) = {}
): CropImageContractOptions {
    val options = CropImageContractOptions(uri, CropImageOptions())
    options.run(builder)
    return options
}
