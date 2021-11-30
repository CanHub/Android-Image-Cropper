package com.canhub.cropper.sample.crop_image.domain

import android.content.Context
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.canhub.cropper.CropImageView

internal interface SCropImageContract {

    interface View {
        fun showErrorMessage(message: String)
        fun handleCropImageResult(uri: String)
        fun startCameraWithUri()
    }

    interface Presenter {
        fun bind(view: View)
        fun unbind()
        fun onCreate(activity: FragmentActivity?, context: Context?)
        fun onCropImageResult(result: CropImageView.CropResult)
        fun onCustomCropImageResult(customUri: Uri?)
        fun onTakePictureResult(success: Boolean)
    }
}
