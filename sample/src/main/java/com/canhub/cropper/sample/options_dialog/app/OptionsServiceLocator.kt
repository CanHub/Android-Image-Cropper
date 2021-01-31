package com.canhub.cropper.sample.options_dialog.app

import android.content.Context
import com.canhub.cropper.sample.options_dialog.domain.OptionsContract
import com.canhub.cropper.sample.options_dialog.presenter.OptionsPresenter

internal class OptionsServiceLocator(private val context: Context) {

    fun getPresenter(): OptionsContract.Presenter = OptionsPresenter()
}