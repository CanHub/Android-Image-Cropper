package com.canhub.cropper.sample.options_dialog

import android.os.Parcelable
import com.canhub.cropper.CropImageView
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
internal data class SampleOptionsEntity(
    val scaleType: CropImageView.ScaleType,
    val cropShape: CropImageView.CropShape,
    val cornerShape: CropImageView.CropCornerShape,
    val guidelines: CropImageView.Guidelines,
    val ratio: @RawValue Pair<Int, Int>?,
    val maxZoomLvl: Int,
    val autoZoom: Boolean,
    val multiTouch: Boolean,
    val centerMove: Boolean,
    val showCropOverlay: Boolean,
    val showProgressBar: Boolean,
    val flipHorizontally: Boolean,
    val flipVertically: Boolean,
    val showCropLabel: Boolean
) : Parcelable
