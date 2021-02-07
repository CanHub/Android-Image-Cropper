package com.canhub.cropper.sample.crop_image_view.domain

import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain

internal interface CropImageViewContract {

    interface View {

        fun setOptions(options: OptionsDomain)
    }

    interface Presenter {

        fun bind(view: View)
        fun unbind()

        fun onViewCreated()
    }
}
