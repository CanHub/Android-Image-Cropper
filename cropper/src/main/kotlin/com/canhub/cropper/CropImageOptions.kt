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
  var imageSourceIncludeGallery: Boolean = true,

  /**
   * When library picking and image if this value is true user will be prompt with option to
   * retrieve the image from camera(take picture).
   *
   * Default value: true
   */
  @JvmField
  var imageSourceIncludeCamera: Boolean = true,

  /** The shape of the cropping window. */
  @JvmField
  var cropShape: CropShape = CropShape.RECTANGLE,

  /**
   * The shape of cropper corners
   */
  @JvmField
  var cornerShape: CropImageView.CropCornerShape = CropImageView.CropCornerShape.RECTANGLE,

  /**
   * The radius of the circular crop corner
   */
  @JvmField
  var cropCornerRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, Resources.getSystem().displayMetrics),

  /**
   * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
   * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
   * box edge. (in pixels)
   */
  @JvmField
  var snapRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),

  /**
   * The radius of the touchable area around the handle. (in pixels)<br></br>
   * We are basing this value off of the recommended 48dp Rhythm.<br></br>
   * See: http://developer.android.com/design/style/metrics-grids.html#48dp-rhythm
   */
  @JvmField
  var touchRadius: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, Resources.getSystem().displayMetrics),

  /** Whether the guidelines should be on, off, or only showing when resizing. */
  @JvmField
  var guidelines: Guidelines = Guidelines.ON_TOUCH,

  /** The initial scale type of the image in the crop image view. */
  @JvmField
  var scaleType: CropImageView.ScaleType = CropImageView.ScaleType.FIT_CENTER,

  /**
   * if to show crop overlay UI what contains the crop window UI surrounded by background over the
   * cropping image.<br></br>
   * default: true, may disable for animation or frame transition.
   */
  @JvmField
  var showCropOverlay: Boolean = true,

  /**
   * If enabled, show a text label on top of crop overlay UI, which gets moved along with the cropper
   */
  @JvmField
  var showCropLabel: Boolean = false,

  /**
   * if to show progress bar when image async loading/cropping is in progress.<br></br>
   * default: true, disable to provide custom progress bar UI.
   */
  @JvmField
  var showProgressBar: Boolean = true,

  /** The color of the progress bar. Only works on API level 21 and upwards. */
  @JvmField
  @ColorInt
  var progressBarColor: Int = COLOR_PURPLE,

  /**
   * if auto-zoom functionality is enabled.<br></br>
   * default: true.
   */
  @JvmField
  var autoZoomEnabled: Boolean = true,

  /** If multi-touch should be enabled on the crop box default: false. */
  @JvmField
  var multiTouchEnabled: Boolean = false,

  /** If the crop window can be moved by dragging the center; default: true. */
  @JvmField
  var centerMoveEnabled: Boolean = true,

  /** The max zoom allowed during cropping. */
  @JvmField
  var maxZoom: Int = 4,

  /**
   * The initial crop window padding from image borders in percentage of the cropping image
   * dimensions.
   */
  @JvmField
  var initialCropWindowPaddingRatio: Float = 0.0f,

  /** Whether the width to height aspect ratio should be maintained or free to change. */
  @JvmField
  var fixAspectRatio: Boolean = false,

  /** The X value of the aspect ratio. */
  @JvmField
  var aspectRatioX: Int = 1,

  /** The Y value of the aspect ratio. */
  @JvmField
  var aspectRatioY: Int = 1,

  /** The thickness of the guidelines lines in pixels. (in pixels) */
  @JvmField
  var borderLineThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),

  /** The color of the guidelines lines. */
  @JvmField
  @ColorInt
  var borderLineColor: Int = Color.argb(170, 255, 255, 255),

  /** Thickness of the corner line. (in pixels) */
  @JvmField
  var borderCornerThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, Resources.getSystem().displayMetrics),

  /** The offset of corner line from crop window border. (in pixels) */
  @JvmField
  var borderCornerOffset: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, Resources.getSystem().displayMetrics),

  /** The length of the corner line away from the corner. (in pixels) */
  @JvmField
  var borderCornerLength: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, Resources.getSystem().displayMetrics),

  /** The color of the corner line. */
  @JvmField
  @ColorInt
  var borderCornerColor: Int = Color.WHITE,

  /**
   * The fill color of circle corner
   */
  @JvmField
  var circleCornerFillColorHexValue: Int = Color.WHITE,

  /** The thickness of the guidelines lines. (in pixels) */
  @JvmField
  var guidelinesThickness: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, Resources.getSystem().displayMetrics),

  /** The color of the guidelines lines. */
  @JvmField
  @ColorInt
  var guidelinesColor: Int = Color.argb(170, 255, 255, 255),

  /**
   * the color of the overlay background around the crop window cover the image parts not in the
   * crop window.
   */
  @JvmField
  @ColorInt
  var backgroundColor: Int = Color.argb(119, 0, 0, 0),

  /** The min width the crop window is allowed to be. (in pixels) */
  @JvmField
  var minCropWindowWidth: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, Resources.getSystem().displayMetrics).toInt(),

  /** The min height the crop window is allowed to be. (in pixels) */
  @JvmField
  var minCropWindowHeight: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, Resources.getSystem().displayMetrics).toInt(),

  /**
   * the min width the resulting cropping image is allowed to be, affects the cropping window
   * limits. (in pixels)
   */
  @JvmField
  var minCropResultWidth: Int = 40,

  /**
   * the min height the resulting cropping image is allowed to be, affects the cropping window
   * limits. (in pixels)
   */
  @JvmField
  var minCropResultHeight: Int = 40,

  /**
   * the max width the resulting cropping image is allowed to be, affects the cropping window
   * limits. (in pixels)
   */
  @JvmField
  var maxCropResultWidth: Int = 99999,

  /**
   * the max height the resulting cropping image is allowed to be, affects the cropping window
   * limits. (in pixels)
   */
  @JvmField
  var maxCropResultHeight: Int = 99999,

  /** The title of the [CropImageActivity]  */
  @JvmField
  var activityTitle: CharSequence = "",

  /** The color to use for action bar items icons. */
  @JvmField
  @ColorInt
  var activityMenuIconColor: Int = 0,

  /** The color to use for action bar items texts. */
  @JvmField
  @ColorInt
  var activityMenuTextColor: Int? = null,

  /** The Android Uri to save the cropped image to. */
  @JvmField
  var customOutputUri: Uri? = null,

  /** The compression format to use when writing the image. */
  @JvmField
  var outputCompressFormat: CompressFormat = CompressFormat.JPEG,

  /** The quality (if applicable) to use when writing the image (0 - 100) */
  @JvmField
  var outputCompressQuality: Int = 90,

  /** The width to resize the cropped image to (see options) */
  @JvmField
  var outputRequestWidth: Int = 0,

  /** The height to resize the cropped image to (see options) */
  @JvmField
  var outputRequestHeight: Int = 0,

  /** The resize method to use on the cropped bitmap (see options documentation) */
  @JvmField
  var outputRequestSizeOptions: RequestSizeOptions = RequestSizeOptions.NONE,

  /** If the result of crop image activity should not save the cropped image bitmap. */
  @JvmField
  var noOutputImage: Boolean = false,

  /** The initial rectangle to set on the cropping image after loading. */
  @JvmField
  var initialCropWindowRectangle: Rect? = null,

  /** The initial rotation to set on the cropping image after loading (0-360 degrees clockwise) */
  @JvmField
  var initialRotation: Int = -1,

  /** If to allow (all) rotation during cropping (activity) */
  @JvmField
  var allowRotation: Boolean = true,

  /** If to allow (all) flipping during cropping (activity) */
  @JvmField
  var allowFlipping: Boolean = true,

  /** If to allow counter-clockwise rotation during cropping (activity) */
  @JvmField
  var allowCounterRotation: Boolean = false,

  /** The amount of degrees to rotate clockwise or counter-clockwise. */
  @JvmField
  var rotationDegrees: Int = 90,

  /** Whether the image should be flipped horizontally. */
  @JvmField
  var flipHorizontally: Boolean = false,

  /** Whether the image should be flipped vertically. */
  @JvmField
  var flipVertically: Boolean = false,

  /** Optional, the text of the crop menu crop button. */
  @JvmField
  var cropMenuCropButtonTitle: CharSequence? = null,

  /** Optional image resource to be used for crop menu crop icon instead of text. */
  @JvmField
  var cropMenuCropButtonIcon: Int = 0,

  /**
   * Allows you to skip the editing (cropping, flipping or rotating) option.
   * This returns the entire selected image directly
   */
  @JvmField
  var skipEditing: Boolean = false,

  /**
   * Enabling this option replaces the current AlertDialog to choose the image source
   * with an Intent chooser
   */
  @JvmField
  var showIntentChooser: Boolean = false,

  /**
   * optional, Sets a custom title for the intent chooser
   */
  @JvmField
  var intentChooserTitle: String? = null,

  /**
   * optional, reorders intent list displayed with the app package names
   * passed here in order
   */
  @JvmField
  var intentChooserPriorityList: List<String>? = emptyList(),

  /** The initial text size of cropper label **/
  @JvmField
  var cropperLabelTextSize: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, Resources.getSystem().displayMetrics),

  /** The default cropper label text color **/
  @JvmField
  @ColorInt
  var cropperLabelTextColor: Int = Color.WHITE,

  /** The default cropper label text **/
  @JvmField
  var cropperLabelText: String? = "",

  /** Crop Image background color **/
  @JvmField
  var activityBackgroundColor: Int = Color.WHITE,

  /** Toolbar color **/
  @JvmField
  var toolbarColor: Int? = null,

  /** Toolbar color **/
  @JvmField
  var toolbarTitleColor: Int? = null,

  /** Toolbar color **/
  @JvmField
  var toolbarBackButtonColor: Int? = null,

  /** Toolbar tint color **/
  @JvmField
  var toolbarTintColor: Int? = null,
) : Parcelable {
  fun validate() {
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

internal const val DEGREES_360 = 360
