package com.canhub.cropper.sample.options_dialog.domain

import com.canhub.cropper.CropImageView.CropShape
import com.canhub.cropper.CropImageView.Guidelines
import com.canhub.cropper.CropImageView.ScaleType

internal interface SOptionsContract {

    interface View {
        fun updateOptions(options: SOptionsDomain)
        fun closeWithResult(options: SOptionsDomain)
    }

    interface Presenter {
        fun bind(view: View)
        fun unbind()
        fun onViewCreated(options: SOptionsDomain?)
        fun onDismiss()
        fun onScaleTypeSelect(scaleType: ScaleType)
        fun onCropShapeSelect(cropShape: CropShape)
        fun onCropRoundedCornersSelect(size: Float)
        fun onCropRoundedBorderCornersSelect(size: Float)
        fun onGuidelinesSelect(guidelines: Guidelines)
        fun onRatioSelect(ratio: Pair<Int, Int>?)
        fun onAutoZoomSelect(enable: Boolean)
        fun onMaxZoomLvlSelect(maxZoom: Int)
        fun onMultiTouchSelect(enable: Boolean)
        fun onCenterMoveSelect(enable: Boolean)
        fun onCropOverlaySelect(show: Boolean)
        fun onProgressBarSelect(show: Boolean)
        fun onFlipHorizontalSelect(enable: Boolean)
        fun onFlipVerticallySelect(enable: Boolean)
    }
}
