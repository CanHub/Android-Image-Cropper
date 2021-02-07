package com.canhub.cropper.sample.main.presenter

import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.main.domain.MainContract
import com.canhub.cropper.sample.options_dialog.domain.OptionsActivityEnum
import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain

internal class MainPresenter : MainContract.Presenter {

    private var view: MainContract.View? = null

    override fun bind(view: MainContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    override fun onViewCreated() {
        view?.setOptions(getOptions())
    }

    private fun getOptions(): OptionsDomain = OptionsDomain(
        OptionsActivityEnum.DEFAULT,
        CropImageView.ScaleType.FIT_CENTER,
        CropImageView.CropShape.RECTANGLE,
        CropImageView.Guidelines.ON,
        Pair(1, 1),
        autoZoom = true,
        maxZoomLvl = 2,
        multiTouch = true,
        showCropOverlay = true,
        showProgressBar = true,
        flipHorizontal = false,
        flipVertically = false
    )
}