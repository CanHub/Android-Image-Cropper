package com.canhub.cropper.sample.options_dialog.presenter

import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.options_dialog.domain.OptionsContract
import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain

internal class OptionsPresenter : OptionsContract.Presenter {

    private var view: OptionsContract.View? = null
    private var options = defaultOptions()

    override fun bind(view: OptionsContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    override fun onViewCreated(options: OptionsDomain?) {
        options?.let { this.options = options }
        view?.updateOptions(this.options)
    }

    override fun onDismiss() {
        view?.closeWithResult(options)
    }

    override fun onScaleTypeSelect(scaleType: CropImageView.ScaleType) {
        options = options.copy(scaleType = scaleType)
    }

    override fun onCropShapeSelect(cropShape: CropImageView.CropShape) {
        options = options.copy(cropShape = cropShape)
    }

    override fun onGuidelinesSelect(guidelines: CropImageView.Guidelines) {
        options = options.copy(guidelines = guidelines)
    }

    override fun onRatioSelect(ratio: Pair<Int, Int>?) {
        options = options.copy(ratio = ratio)
    }

    override fun onAutoZoomSelect(enable: Boolean) {
        options = options.copy(autoZoom = enable)
    }

    override fun onMaxZoomLvlSelect(maxZoom: Int) {
        options = options.copy(maxZoomLvl = maxZoom)
    }

    override fun onMultiTouchSelect(enable: Boolean) {
        options = options.copy(multiTouch = enable)
    }

    override fun onCropOverlaySelect(show: Boolean) {
        options = options.copy(showCropOverlay = show)
    }

    override fun onProgressBarSelect(show: Boolean) {
        options = options.copy(showProgressBar = show)
    }

    override fun onFlipHorizontalSelect(enable: Boolean) {
        options = options.copy(flipHorizontal = enable)
    }

    override fun onFlipVerticallySelect(enable: Boolean) {
        options = options.copy(flipVertically = enable)
    }

    private fun defaultOptions() = OptionsDomain(
        CropImageView.ScaleType.FIT_CENTER,
        CropImageView.CropShape.RECTANGLE,
        CropImageView.Guidelines.ON,
        Pair(1, 1),
        maxZoomLvl = 2,
        autoZoom = true,
        multiTouch = true,
        showCropOverlay = true,
        showProgressBar = true,
        flipHorizontal = false,
        flipVertically = false
    )
}