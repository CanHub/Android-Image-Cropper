package com.canhub.cropper.sample.options_dialog.presenter

import android.util.Pair
import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.options_dialog.domain.OptionsActivityEnum
import com.canhub.cropper.sample.options_dialog.domain.OptionsContract
import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain

internal class OptionsPresenter : OptionsContract.Presenter {

    private var view: OptionsContract.View? = null

    override fun bind(view: OptionsContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    override fun onViewCreated(options: OptionsDomain?) {

        options?.let { view?.updateOptions(it) }
    }

    override fun onApplyClicked() {
        TODO("Not yet implemented")
    }

    override fun onActivityTypeSelect(activityType: OptionsActivityEnum) {
        TODO("Not yet implemented")
    }

    override fun onScaleTypeSelect(scaleType: CropImageView.ScaleType) {
        TODO("Not yet implemented")
    }

    override fun onCropShapeSelect(cropShape: CropImageView.CropShape) {
        TODO("Not yet implemented")
    }

    override fun onGuidelinesSelect(guidelines: CropImageView.Guidelines) {
        TODO("Not yet implemented")
    }

    override fun onRatioSelect(ratio: Pair<Int, Int>) {
        TODO("Not yet implemented")
    }

    override fun onAutoZoomSelect(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onMaxZoomLvlSelect(maxZoom: Int) {
        TODO("Not yet implemented")
    }

    override fun onAspectRatioSelect(isFix: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onMultiTouchSelect(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onCropOverlaySelect(show: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onProgressBarSelect(show: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onFlipHorizontalSelect(enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onFlipVerticallySelect(enable: Boolean) {
        TODO("Not yet implemented")
    }
}