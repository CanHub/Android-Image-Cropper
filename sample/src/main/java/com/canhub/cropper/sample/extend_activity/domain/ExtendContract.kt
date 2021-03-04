package com.canhub.cropper.sample.extend_activity.domain

internal interface ExtendContract {

    interface View {

        fun updateRotationCounter(counter: String)
        fun rotate(counter: Int)
    }

    interface Presenter {

        fun bindView(view: View)
        fun unbindView()
        fun onRotateClick()
    }
}
