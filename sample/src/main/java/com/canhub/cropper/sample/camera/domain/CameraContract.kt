package com.canhub.cropper.sample.camera.domain

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity

internal interface CameraContract {

    interface View {
        fun startCropImage(option: CameraEnumDomain)
        fun showErrorMessage(message: String)
        fun dispatchTakePictureIntent()
        fun cameraPermissionLaunch()
        fun showDialog()
        fun handleCropImageResult(uri: String)
    }

    interface Presenter {
        fun bind(view: View)
        fun unbind()
        fun onPermissionResult(granted: Boolean)
        fun onCreate(activity: FragmentActivity?, context: Context?)
        fun onOk()
        fun onCancel()
        fun onActivityResult(resultCode: Int, requestCode: Int, data: Intent?)
        fun startWithUriClicked()
        fun startWithoutUriClicked()
        fun startPickImageActivityClicked()
        fun startActivityForResultClicked()
    }

}