package com.canhub.cropper.sample.camera.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.camera.domain.CameraEnumDomain
import com.canhub.cropper.sample.camera.domain.SCameraContract

internal class SCameraPresenter : SCameraContract.Presenter {

    private var view: SCameraContract.View? = null
    private val minVersion = com.canhub.cropper.common.CommonVersionCheck.isAtLeastM23()
    private var request = false
    private var hasSystemFeature = false
    private var selfPermission = false
    private var context: Context? = null

    override fun bind(view: SCameraContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    override fun onPermissionResult(granted: Boolean) {
        view?.apply {
            when {
                granted -> startTakePicture()
                minVersion && request -> showDialog()
                else -> cameraPermissionLaunch()
            }
        }
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
        selfPermission = ContextCompat
            .checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
    }

    override fun startWithUriClicked() {
        view?.apply {
            when {
                hasSystemFeature && selfPermission -> startTakePicture()
                hasSystemFeature && minVersion && request -> showDialog()
                hasSystemFeature -> cameraPermissionLaunch()
                else -> showErrorMessage("onCreate no case apply")
            }
        }
    }

    override fun startWithoutUriClicked() {
        view?.startCropImage(CameraEnumDomain.START_WITHOUT_URI)
    }

    override fun startPickImageActivityClicked() {
        view?.startCropImage(CameraEnumDomain.START_PICK_IMG)
    }

    override fun onOk() {
        view?.cameraPermissionLaunch()
    }

    override fun onCancel() {
        view?.showErrorMessage("onCancel")
    }

    override fun onCropImageResult(result: CropImageView.CropResult) {
        if (result.isSuccessful) {
            view?.handleCropImageResult(result.uriContent.toString().replace("file:", ""))
        } else if (result is CropImage.CancelledResult) {
            view?.showErrorMessage("cropping image was cancelled by the user")
        } else {
            view?.showErrorMessage("cropping image failed")
        }
    }

    override fun onPickImageResult(resultUri: Uri?) {
        if (resultUri != null) {
            Log.v("File Path", resultUri.toString())
            view?.handleCropImageResult(resultUri.toString())
        } else {
            view?.showErrorMessage("picking image failed")
        }
    }

    override fun onTakePictureResult(success: Boolean) {
        if (success) {
            view?.startCropImage(CameraEnumDomain.START_WITH_URI)
        } else {
            view?.showErrorMessage("taking picture failed")
        }
    }
}
