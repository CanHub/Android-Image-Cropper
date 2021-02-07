package com.canhub.cropper.sample.main.domain

import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain

internal interface MainContract {

    interface View {

        fun setOptions(options: OptionsDomain)
    }

    interface Presenter {

        fun bind(view: View)
        fun unbind()

        fun onViewCreated()
    }
}