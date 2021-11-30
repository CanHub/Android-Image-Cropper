package com.canhub.cropper.sample.crop_image.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.crop_image.domain.SCropImageContract

internal class SCropImagePresenter : SCropImageContract.Presenter {

    private var view: SCropImageContract.View? = null
    private var request = false
    private var hasSystemFeature = false
    private var context: Context? = null

    override fun bind(view: SCropImageContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    override fun onCreate(activity: FragmentActivity?, context: Context?) {
        if (activity == null || context == null) {
            view?.showErrorMessage("onCreate activity and/or context are null")
            return
        }
        this.context = context

        request = ActivityCompat.shouldShowRequestPermissionRationale(
            activity as Activity,
            Manifest.permission.CAMERA
        )
        hasSystemFeature = context.packageManager
            ?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ?: false
    }

    override fun onCropImageResult(result: CropImageView.CropResult) {
        when {
            result.isSuccessful -> {
                Log.v("Bitmap", result.bitmap.toString())
                Log.v("File Path", context?.let { result.getUriFilePath(it) }.toString())
                view?.handleCropImageResult(result.uriContent.toString().replace("file:", ""))
            }
            result is CropImage.CancelledResult -> {
                view?.showErrorMessage("cropping image was cancelled by the user")
            }
            else -> {
                view?.showErrorMessage("cropping image failed")
            }
        }
    }

    override fun onCustomCropImageResult(customUri: Uri?) {
        view?.handleCropImageResult(customUri.toString().replace("file:", ""))
    }

    override fun onTakePictureResult(success: Boolean) {
        if (success) view?.startCameraWithUri()
        else view?.showErrorMessage("taking picture failed")
    }
}
