package com.canhub.cropper.sample.options_dialog.domain

import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageView.ScaleType

internal interface OptionsContract {

    interface View {

        fun updateOptions(options: OptionsDomain)
        fun closeWithResult(options: OptionsDomain)
    }

    interface Presenter {

        fun bind(view: View)
        fun unbind()
        fun onViewCreated(options: OptionsDomain?)
        fun onDismiss()

        fun onScaleTypeSelect(scaleType: ScaleType)
        fun onCropShapeSelect(cropShape: CropShape)
        fun onGuidelinesSelect(guidelines: Guidelines)
        fun onRatioSelect(ratio: Pair<Int, Int>?)
        fun onAutoZoomSelect(enable: Boolean)
        fun onMaxZoomLvlSelect(maxZoom: Int)
        fun onMultiTouchSelect(enable: Boolean)
        fun onTranslationSelect(enable: Boolean)
        fun onCropOverlaySelect(show: Boolean)
        fun onProgressBarSelect(show: Boolean)
        fun onFlipHorizontalSelect(enable: Boolean)
        fun onFlipVerticallySelect(enable: Boolean)
    }
}
