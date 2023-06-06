package com.canhub.cropper.sample.options_dialog.domain

import android.os.Parcelable
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.HorizontalControllers
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
internal data class SOptionsDomain(
    val scaleType: CropImageView.ScaleType,
    val cropShape: CropImageView.CropShape,
    val cropRoundedCorners: Float,
    val cropBorderRoundedCorners: Float,
    val horizontalControllers: HorizontalControllers,
    val guidelines: CropImageView.Guidelines,
    val ratio: @RawValue Pair<Int, Int>?,
    val maxZoomLvl: Int,
    val autoZoom: Boolean,
    val multiTouch: Boolean,
    val centerMove: Boolean,
    val showCropOverlay: Boolean,
    val showProgressBar: Boolean,
    val flipHorizontal: Boolean,
    val flipVertically: Boolean,
) : Parcelable
