package com.canhub.cropper

import android.content.res.Resources
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Parcelable
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageView.RequestSizeOptions
import kotlinx.parcelize.Parcelize

private val COLOR_PURPLE = Color.rgb(153, 51, 153)
private val COLOR_WHITE_170 = Color.argb(170, 255, 255, 255)
private val COLOR_BLACK_119 = Color.argb(119, 0, 0, 0)
private const val COLOR_WHITE = Color.WHITE

internal const val DEGREES_360 = 360

/**
 * All the possible options that can be set to customize crop image.<br></br>
 * Initialized with default values.
 */
@Parcelize data class CropImageOptions @JvmOverloads constructor(
    /**
     * When library picking and image if this value is true user will be prompt with option to
     * retrieve the image from gallery. The rule used is "image/<*>"
     *
     * Default value: true
     */
    @JvmField
    val imageSourceIncludeGallery: Boolean = true,

    /**
     * When library picking and image if this value is true user will be prompt with option to
     * retrieve the image from camera(take picture).
     *
     * Default value: true
     */
    @JvmField
    val imageSourceIncludeCamera: Boolean = true,

    /** The shape of the cropping window.  */
    @JvmField
    val cropShape: CropShape = CropShape.RECTANGLE,
    /**
     * The shape of cropper corners
     */
    @JvmField
    val cornerShape: CropImageView.CropCornerShape = CropImageView.CropCornerShape.RECTANGLE,
    /**
     * The radius of the circular crop corner
     */
    @JvmField
    val cropCornerRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, Resources.getSystem().displayMetrics),

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (in pixels)
     */
    @JvmField
    val snapRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),

    /**
     * The radius of the touchable area around the handle. (in pixels)<br></br>
     * We are basing this value off of the recommended 48dp Rhythm.<br></br>
     * See: http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm
     */
    @JvmField
    val touchRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, Resources.getSystem().displayMetrics),

    /** whether the guidelines should be on, off, or only showing when resizing.  */
    @JvmField
    val guidelines: Guidelines = Guidelines.ON_TOUCH,

    /** The initial scale type of the image in the crop image view  */
    @JvmField
    val scaleType: CropImageView.ScaleType = CropImageView.ScaleType.FIT_CENTER,

    /**
     * if to show crop overlay UI what contains the crop window UI surrounded by background over the
     * cropping image.<br></br>
     * default: true, may disable for animation or frame transition.
     */
    @JvmField
    val showCropOverlay: Boolean = true,

    /**
     * If enabled, show a text label on top of crop overlay UI, which gets moved along with the cropper
     */
    @JvmField
    val showCropLabel: Boolean = false,

    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    @JvmField
    val showProgressBar: Boolean = true,

    /** The color of the progress bar. Only works on API level 21 and upwards. */
    @JvmField
    @ColorInt
    val progressBarColor: Int = COLOR_PURPLE,

    /**
     * if auto-zoom functionality is enabled.<br></br>
     * default: true.
     */
    @JvmField
    val autoZoomEnabled: Boolean = true,

    /** if multitouch should be enabled on the crop box default: false  */
    @JvmField
    val multiTouchEnabled: Boolean = false,

    /** if the crop window can be moved by dragging the center; default: true  */
    @JvmField
    val centerMoveEnabled: Boolean = true,

    /** The max zoom allowed during cropping.  */
    @JvmField
    val maxZoom: Int = 4,

    /**
     * The initial crop window padding from image borders in percentage of the cropping image
     * dimensions.
     */
    @JvmField
    val initialCropWindowPaddingRatio: Float = 0.1f,

    /** whether the width to height aspect ratio should be maintained or free to change.  */
    @JvmField
    val fixAspectRatio: Boolean = false,

    /** the X value of the aspect ratio.  */
    @JvmField
    val aspectRatioX: Int = 1,

    /** the Y value of the aspect ratio.  */
    @JvmField
    val aspectRatioY: Int = 1,

    /** the thickness of the guidelines lines in pixels. (in pixels)  */
    @JvmField
    val borderLineThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),

    /** the color of the guidelines lines  */
    @JvmField
    @ColorInt
    val borderLineColor: Int = COLOR_WHITE_170,

    /** thickness of the corner line. (in pixels)  */
    @JvmField
    val borderCornerThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, Resources.getSystem().displayMetrics),

    /** the offset of corner line from crop window border. (in pixels)  */
    @JvmField
    val borderCornerOffset: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, Resources.getSystem().displayMetrics),

    /** the length of the corner line away from the corner. (in pixels)  */
    @JvmField
    val borderCornerLength: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, Resources.getSystem().displayMetrics),

    /** the color of the corner line  */
    @JvmField
    @ColorInt
    val borderCornerColor: Int = COLOR_WHITE,
    /**
     * The fill color of circle corner
     */
    @JvmField
    val circleCornerFillColorHexValue: Int = COLOR_WHITE,

    /** the thickness of the guidelines lines. (in pixels)  */
    @JvmField
    val guidelinesThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().displayMetrics),

    /** the color of the guidelines lines  */
    @JvmField
    @ColorInt
    val guidelinesColor: Int = COLOR_WHITE_170,

    /**
     * the color of the overlay background around the crop window cover the image parts not in the
     * crop window.
     */
    @JvmField
    @ColorInt
    val backgroundColor: Int = COLOR_BLACK_119,

    /** the min width the crop window is allowed to be. (in pixels)  */
    @JvmField
    val minCropWindowWidth: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, Resources.getSystem().displayMetrics).toInt(),

    /** the min height the crop window is allowed to be. (in pixels)  */
    @JvmField
    val minCropWindowHeight: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, Resources.getSystem().displayMetrics).toInt(),

    /**
     * the min width the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    @JvmField
    val minCropResultWidth: Int = 40,

    /**
     * the min height the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    @JvmField
    val minCropResultHeight: Int = 40,

    /**
     * the max width the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    @JvmField
    val maxCropResultWidth: Int = 99999,

    /**
     * the max height the resulting cropping image is allowed to be, affects the cropping window
     * limits. (in pixels)
     */
    @JvmField
    val maxCropResultHeight: Int = 99999,

    /** the title of the [CropImageActivity]  */
    @JvmField
    val activityTitle: CharSequence = "",

    /** the color to use for action bar items icons  */
    @JvmField
    @ColorInt
    val activityMenuIconColor: Int = 0,

    /** the Android Uri to save the cropped image to  */
    @JvmField
    val customOutputUri: Uri? = null,

    /** the compression format to use when writing the image  */
    @JvmField
    val outputCompressFormat: CompressFormat = CompressFormat.JPEG,

    /** the quality (if applicable) to use when writing the image (0 - 100)  */
    @JvmField
    val outputCompressQuality: Int = 90,

    /** the width to resize the cropped image to (see options)  */
    @JvmField
    val outputRequestWidth: Int = 0,

    /** the height to resize the cropped image to (see options)  */
    @JvmField
    val outputRequestHeight: Int = 0,

    /** the resize method to use on the cropped bitmap (see options documentation)  */
    @JvmField
    val outputRequestSizeOptions: RequestSizeOptions = RequestSizeOptions.NONE,

    /** if the result of crop image activity should not save the cropped image bitmap  */
    @JvmField
    val noOutputImage: Boolean = false,

    /** the initial rectangle to set on the cropping image after loading  */
    @JvmField
    val initialCropWindowRectangle: Rect? = null,

    /** the initial rotation to set on the cropping image after loading (0-360 degrees clockwise)  */
    @JvmField
    val initialRotation: Int = -1,

    /** if to allow (all) rotation during cropping (activity)  */
    @JvmField
    val allowRotation: Boolean = true,

    /** if to allow (all) flipping during cropping (activity)  */
    @JvmField
    val allowFlipping: Boolean = true,

    /** if to allow counter-clockwise rotation during cropping (activity)  */
    @JvmField
    val allowCounterRotation: Boolean = false,

    /** the amount of degrees to rotate clockwise or counter-clockwise  */
    @JvmField
    val rotationDegrees: Int = 90,

    /** whether the image should be flipped horizontally  */
    @JvmField
    val flipHorizontally: Boolean = false,

    /** whether the image should be flipped vertically  */
    @JvmField
    val flipVertically: Boolean = false,

    /** optional, the text of the crop menu crop button  */
    @JvmField
    val cropMenuCropButtonTitle: CharSequence? = null,

    /** optional image resource to be used for crop menu crop icon instead of text  */
    @JvmField
    val cropMenuCropButtonIcon: Int = 0,

    /**
     * Allows you to skip the editing (cropping, flipping or rotating) option.
     * This returns the entire selected image directly
     */
    @JvmField
    val skipEditing: Boolean = false,

    /**
     * Enabling this option replaces the current AlertDialog to choose the image source
     * with an Intent chooser
     */
    @JvmField
    val showIntentChooser: Boolean = false,

    /**
     * optional, Sets a custom title for the intent chooser
     */
    @JvmField
    val intentChooserTitle: String? = null,

    /**
     * optional, reorders intent list displayed with the app package names
     * passed here in order
     */
    @JvmField
    val intentChooserPriorityList: List<String>? = emptyList(),

    /** The initial text size of cropper label **/
    @JvmField
    val cropperLabelTextSize: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, Resources.getSystem().displayMetrics),

    /** The default cropper label text color **/
    @JvmField
    @ColorInt
    val cropperLabelTextColor: Int = COLOR_WHITE,

    /** The default cropper label text **/
    @JvmField
    val cropperLabelText: String? = "",
) : Parcelable {
    init {
        require(maxZoom >= 0) { "Cannot set max zoom to a number < 1" }
        require(touchRadius >= 0) { "Cannot set touch radius value to a number <= 0 " }
        require(!(initialCropWindowPaddingRatio < 0 || initialCropWindowPaddingRatio >= 0.5)) { "Cannot set initial crop window padding value to a number < 0 or >= 0.5" }
        require(aspectRatioX > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
        require(aspectRatioY > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
        require(borderLineThickness >= 0) { "Cannot set line thickness value to a number less than 0." }
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
}
