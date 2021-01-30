package com.canhub.cropper.sample

import android.util.Pair
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines

/** The crop image view options that can be changed live.  */
class CropImageViewOptions {

    @JvmField
    var scaleType = CropImageView.ScaleType.CENTER_INSIDE

    @JvmField
    var cropShape = CropShape.RECTANGLE

    @JvmField
    var guidelines = Guidelines.ON_TOUCH

    @JvmField
    var aspectRatio = Pair(1, 1)

    @JvmField
    var autoZoomEnabled = false

    @JvmField
    var maxZoomLevel = 0

    @JvmField
    var fixAspectRatio = false

    @JvmField
    var multitouch = false

    @JvmField
    var showCropOverlay = false

    @JvmField
    var showProgressBar = false

    @JvmField
    var flipHorizontally = false

    @JvmField
    var flipVertically = false
}