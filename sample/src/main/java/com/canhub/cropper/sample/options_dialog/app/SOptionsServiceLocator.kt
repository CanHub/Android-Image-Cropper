package com.canhub.cropper.sample.options_dialog.app

import android.content.Context
import com.canhub.cropper.sample.options_dialog.domain.SOptionsContract
import com.canhub.cropper.sample.options_dialog.presenter.SOptionsPresenter

internal class SOptionsServiceLocator(private val context: Context) {

    fun getPresenter(): SOptionsContract.Presenter = SOptionsPresenter()
}
