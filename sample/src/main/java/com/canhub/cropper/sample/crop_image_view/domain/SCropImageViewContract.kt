package com.canhub.cropper.sample.crop_image_view.domain

import com.canhub.cropper.sample.options_dialog.domain.SOptionsDomain

internal interface SCropImageViewContract {
    fun interface View {
        fun setOptions(options: SOptionsDomain)
    }

    interface Presenter {
        fun bind(view: View)
        fun unbind()
        fun onViewCreated()
    }
}
