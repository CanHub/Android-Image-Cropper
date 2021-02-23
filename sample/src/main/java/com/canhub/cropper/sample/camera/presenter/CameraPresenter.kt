package com.canhub.cropper.sample.camera.presenter

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.sample.camera.app.CameraFragment
import com.canhub.cropper.sample.camera.app.CameraFragment.Companion.CODE_PHOTO_CAMERA
import com.canhub.cropper.sample.camera.domain.CameraContract
import com.canhub.cropper.sample.camera.domain.CameraEnumDomain

internal class CameraPresenter : CameraContract.Presenter {

    private var view: CameraContract.View? = null
    private val minVersion = com.canhub.cropper.common.CommonVersionCheck.isAtLeastM23()
    private var request = false
    private var hasSystemFeature = false
    private var selfPermission = false
    private var context: Context? = null

    override fun bind(view: CameraContract.View) {
        this.view = view
    }

    override fun unbind() {
        view = null
    }

    override fun onPermissionResult(granted: Boolean) {
        view?.apply {
            when {
                granted -> dispatchTakePictureIntent()
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
                hasSystemFeature && selfPermission -> dispatchTakePictureIntent()
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

    override fun startActivityForResultClicked() {
        view?.startCropImage(CameraEnumDomain.START_FOR_RESULT)
    }

    override fun onOk() {
        view?.cameraPermissionLaunch()
    }

    override fun onCancel() {
        view?.showErrorMessage("onCancel")
    }

    override fun onActivityResult(resultCode: Int, requestCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    CropImage.getActivityResult(data)?.uri?.let {
                        view?.handleCropImageResult(it.toString().replace("file:", ""))
                    } ?: view?.showErrorMessage("CropImage getActivityResult return null")
                }
                CameraFragment.CUSTOM_REQUEST_CODE -> {
                    context?.let {
                        val uri = CropImage.getPickImageResultUri(it, data)
                        view?.handleCropImageResult(uri.toString().replace("file:", ""))
                    }
                }
                CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                    context?.let { ctx ->
                        val uri = CropImage.getPickImageResultUri(ctx, data)

                        if (uri != null) view?.handleCropImageResult(uri.toString())
                        else view?.showErrorMessage("Pick Image, null URI")
                    }
                }
                CODE_PHOTO_CAMERA -> view?.startCropImage(CameraEnumDomain.START_WITH_URI)
                else -> view?.showErrorMessage("requestCode = $requestCode")
            }
        } else view?.showErrorMessage("resultCode = $resultCode")
    }
}