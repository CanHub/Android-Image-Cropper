package com.canhub.cropper.sample.extend_activity.presenter

import com.canhub.cropper.sample.extend_activity.domain.ExtendContract

internal class ExtendPresenter : ExtendContract.Presenter {

    private var view: ExtendContract.View? = null
    private var counter = 0

    override fun bindView(view: ExtendContract.View) {
        this.view = view
        this.view?.updateRotationCounter(counter.toString())
    }

    override fun unbindView() {
        view = null
    }

    override fun onRotateClick() {
        counter += 90
        view?.rotate(90)
        if (counter == 360) counter = 0
        view?.updateRotationCounter(counter.toString())
    }
}