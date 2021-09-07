package com.canhub.cropper.sample.crop_image.domain

import android.content.Context
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.canhub.cropper.CropImageView

internal interface SCropImageContract {

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
        fun onCustomCropImageResult(customUri: Uri?)
        fun onPickImageResult(resultUri: Uri?)
        fun onPickImageResultCustom(resultUri: Uri?)
        fun onTakePictureResult(success: Boolean)
        fun startWithUriClicked()
        fun startWithoutUriClicked()
        fun startPickImageActivityClicked()
        fun startPickImageActivityCustomClicked()
    }
}
