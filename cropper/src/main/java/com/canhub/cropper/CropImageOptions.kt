package com.canhub.cropper

import android.content.res.Resources
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Parcelable
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageView.RequestSizeOptions
import kotlinx.parcelize.Parcelize

/**
 * All the possible options that can be set to customize crop image.<br></br>
 * Initialized with default values.
 */
@Parcelize
data class CropImageOptions(
    /**
     * When library picking and image if this value is true user will be prompt with option to
     * retrieve the image from gallery. The rule used is "image/<*>"
     *
     * Default value: true
     */
    var imageSourceIncludeGallery: Boolean = true,
    /**
     * When library picking and image if this value is true user will be prompt with option to
     * retrieve the image from camera(take picture).
     *
     * Default value: true
     */
    var imageSourceIncludeCamera: Boolean = true,
    /** The shape of the cropping window.  */
    var cropShape: CropShape = CropShape.RECTANGLE,
    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (in pixels)
     */
    var snapRadius: Float = 3f.px(),
    /**
     * The radius of the touchable area around the handle. (in pixels)<br></br>
     * We are basing this value off of the recommended 48dp Rhythm.<br></br>
     * See: http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm
     */
    var touchRadius: Float = 24f.px(),
    /** whether the guidelines should be on, off, or only showing when resizing.  */
    var guidelines: Guidelines = Guidelines.ON_TOUCH,
    /** The initial scale type of the image in the crop image view  */
    var scaleType: CropImageView.ScaleType = CropImageView.ScaleType.FIT_CENTER,
    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * default: true, may disable for animation or frame transition.
     */
    var showCropOverlay: Boolean = true,
    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    var showProgressBar: Boolean = true,
    /**
     * if auto-zoom functionality is enabled.<br></br>
     * default: true.
     */
    var autoZoomEnabled: Boolean = true,
    /** if multi-touch should be enabled on the crop box default: false  */
    var multiTouchEnabled: Boolean = false,
    /** if the the crop window can be moved by dragging the center; default: true  */
    var centerMoveEnabled: Boolean = true,
    /** The max zoom allowed during cropping.  */
    var maxZoom: Int = 4,
    /**
     * The initial crop window padding from image borders in percentage of the cropping image
     * dimensions.
     */
    var initialCropWindowPaddingRatio: Float = 0.1f,
    /** whether the width to height aspect ratio should be maintained or free to change.  */
    var fixAspectRatio: Boolean = false,
    /** the X value of the aspect ratio.  */
    var aspectRatioX: Int = 1,
    /** the Y value of the aspect ratio.  */
    var aspectRatioY: Int = 1,
    /** the thickness of the guidelines lines. (in pixels)  */
    var borderLineThickness: Float = 3f.px(),
    /** the corner radius of the guidelines lines. (in pixels)  */
    var borderLineCornerRadius: Float = 0f,
    /** the color of the guidelines lines  */
    @ColorInt
    var borderLineColor: Int = Color.argb(170, 255, 255, 255),
    /** thickness of the corner line. (in pixels)  */
    var borderCornerThickness: Float = 2f.px(),
    /** the offset of corner line from crop window border. (in pixels)  */
    var borderCornerOffset: Float = 5f.px(),
    /** the length of the corner line away from the corner. (in pixels)  */
    var borderCornerLength: Float = 14f.px(),
    /** the color of the corner line  */
    var borderCornerColor: Int = Color.WHITE,
    /** the thickness of the guidelines lines. (in pixels)  */
    var guidelinesThickness: Float = 1f.px(),
    /** the color of the guidelines lines  */
    @ColorInt
    var guidelinesColor: Int = Color.argb(170, 255, 255, 255),
    /**
     * the color of the overlay background around the crop window cover the image parts not in the
     * crop window.
     */
    @ColorInt
    var backgroundColor: Int = Color.argb(119, 0, 0, 0),
    /** the min width the crop window is allowed to be. (in pixels)  */
    var minCropWindowWidth: Int = 42f.px().toInt(),
    /** the min height the crop window is allowed to be. (in pixels)  */
    var minCropWindowHeight: Int = 42f.px().toInt(),
    /**
     * the min width the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var minCropResultWidth: Int = 40,
    /**
     * the min height the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var minCropResultHeight: Int = 40,
    /**
     * the max width the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var maxCropResultWidth: Int = 99999,
    /**
     * the max height the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    var maxCropResultHeight: Int = 99999,
    /** the title of the [CropImageActivity]  */
    var activityTitle: CharSequence = "",
    /** the color to use for action bar items icons  */
    var activityMenuIconColor: Int = 0,
    /** the Android Uri to save the cropped image to  */
    var customOutputUri: Uri? = null,
    /** the compression format to use when writing the image  */
    var outputCompressFormat: CompressFormat = CompressFormat.JPEG,
    /** the quality (if applicable) to use when writing the image (0 - 100)  */
    var outputCompressQuality: Int = 90,
    /** the width to resize the cropped image to (see options)  */
    var outputRequestWidth: Int = 0,
    /** the height to resize the cropped image to (see options)  */
    var outputRequestHeight: Int = 0,
    /** the resize method to use on the cropped bitmap (see options documentation)  */
    var outputRequestSizeOptions: RequestSizeOptions = RequestSizeOptions.NONE,
    /** if the result of crop image activity should not save the cropped image bitmap  */
    var noOutputImage: Boolean = false,
    /** the initial rectangle to set on the cropping image after loading  */
    var initialCropWindowRectangle: Rect? = null,
    /** the initial rotation to set on the cropping image after loading (0-360 degrees clockwise)  */
    var initialRotation: Int = -1,
    /** if to allow (all) rotation during cropping (activity)  */
    var allowRotation: Boolean = true,
    /** if to allow (all) flipping during cropping (activity)  */
    var allowFlipping: Boolean = true,
    /** if to allow counter-clockwise rotation during cropping (activity)  */
    var allowCounterRotation: Boolean = false,
    /** the amount of degrees to rotate clockwise or counter-clockwise  */
    @IntRange(from = 0L, to = 360L)
    var rotationDegrees: Int = 90,
    /** whether the image should be flipped horizontally  */
    var flipHorizontally: Boolean = false,
    /** whether the image should be flipped vertically  */
    var flipVertically: Boolean = false,
    /** optional, the text of the crop menu crop button  */
    var cropMenuCropButtonTitle: CharSequence? = null,
    /** optional image resource to be used for crop menu crop icon instead of text  */
    @DrawableRes
    var cropMenuCropButtonIcon: Int = 0
) : Parcelable {

    /**
     * Validate all the options are withing valid range.
     *
     * @throws IllegalArgumentException if any of the options is not valid
     */
    fun validate() {
        require(maxZoom >= 0) { "Cannot set max zoom to a number < 1" }
        require(touchRadius >= 0) { "Cannot set touch radius value to a number <= 0 " }
        require(!(initialCropWindowPaddingRatio < 0 || initialCropWindowPaddingRatio >= 0.5)) { "Cannot set initial crop window padding value to a number < 0 or >= 0.5" }
        require(aspectRatioX > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
        require(aspectRatioY > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
        require(borderLineThickness >= 0) { "Cannot set line thickness value to a number less than 0." }
        require(borderLineCornerRadius >= 0) { "Cannot set line corner radius value to a number less than 0." }
        require(borderCornerThickness >= 0) { "Cannot set corner thickness value to a number less than 0." }
        require(guidelinesThickness >= 0) { "Cannot set guidelines thickness value to a number less than 0." }
        require(minCropWindowHeight >= 0) { "Cannot set min crop window height value to a number < 0 " }
        require(minCropResultWidth >= 0) { "Cannot set min crop result width value to a number < 0 " }
        require(minCropResultHeight >= 0) { "Cannot set min crop result height value to a number < 0 " }
        require(maxCropResultWidth >= minCropResultWidth) { "Cannot set max crop result width to smaller value than min crop result width" }
        require(maxCropResultHeight >= minCropResultHeight) { "Cannot set max crop result height to smaller value than min crop result height" }
        require(outputRequestWidth >= 0) { "Cannot set request width value to a number < 0 " }
        require(outputRequestHeight >= 0) { "Cannot set request height value to a number < 0 " }
        require(!(rotationDegrees < 0 || rotationDegrees > DEGREES_360)) { "Cannot set rotation degrees value to a number < 0 or > 360" }
    }

    companion object {

        private fun Float.px(): Float {
            val dm = Resources.getSystem().displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, dm)
        }

        internal const val DEGREES_360 = 360
    }
}
