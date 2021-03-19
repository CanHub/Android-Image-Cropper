package com.canhub.cropper.sample.extend_activity.presenter

import com.canhub.cropper.sample.extend_activity.domain.SExtendContract

internal class SExtendPresenter : SExtendContract.Presenter {

    private var view: SExtendContract.View? = null
    private var counter = 0

    override fun bindView(view: SExtendContract.View) {
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
