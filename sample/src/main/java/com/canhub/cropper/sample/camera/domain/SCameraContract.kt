package com.canhub.cropper.sample.camera.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.canhub.cropper.CropImageView

internal interface SCameraContract {

    interface View {
        fun startCropImage(option: CameraEnumDomain)
        fun showErrorMessage(message: String)
        fun startTakePicture()
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
        fun onCropImageResult(result: CropImageView.CropResult)
        fun onPickImageResult(resultUri: Uri?)
        fun onTakePictureResult(success: Boolean)
        fun startWithUriClicked()
        fun startWithoutUriClicked()
        fun startPickImageActivityClicked()
    }
}
