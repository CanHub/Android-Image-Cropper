package com.canhub.cropper.sample.optionsdialog

import android.os.Parcelable
import com.canhub.cropper.CropImageView
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
internal data class SampleOptionsEntity(
  val scaleType: CropImageView.ScaleType = CropImageView.ScaleType.FIT_CENTER,
  val cropShape: CropImageView.CropShape = CropImageView.CropShape.RECTANGLE,
  val cornerShape: CropImageView.CropCornerShape = CropImageView.CropCornerShape.RECTANGLE,
  val guidelines: CropImageView.Guidelines = CropImageView.Guidelines.ON,
  val ratio: @RawValue Pair<Int, Int>? = Pair(1, 1),
  val maxZoomLvl: Int = 2,
  val autoZoom: Boolean = true,
  val multiTouch: Boolean = true,
  val centerMove: Boolean = true,
  val showCropOverlay: Boolean = true,
  val showProgressBar: Boolean = true,
  val flipHorizontally: Boolean = false,
  val flipVertically: Boolean = false,
  val showCropLabel: Boolean = true,
) : Parcelable
