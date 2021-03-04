package com.canhub.cropper.sample.crop_image_view.presenter

import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.crop_image_view.domain.CropImageViewContract
import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain

internal class CropImageViewPresenter : CropImageViewContract.Presenter {

    private var view: CropImageViewContract.View? = null

    override fun bind(view: CropImageViewContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    override fun onViewCreated() {
        view?.setOptions(getOptions())
    }

    private fun getOptions(): OptionsDomain = OptionsDomain(
        CropImageView.ScaleType.FIT_CENTER,
        CropImageView.CropShape.RECTANGLE,
        CropImageView.Guidelines.ON,
        Pair(1, 1),
        autoZoom = true,
        maxZoomLvl = 2,
        multiTouch = true,
        centerMove = true,
        showCropOverlay = true,
        showProgressBar = true,
        flipHorizontal = false,
        flipVertically = false
    )
}
