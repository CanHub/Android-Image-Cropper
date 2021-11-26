package com.canhub.cropper.sample.crop_image.app

import android.Manifest
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.common.CommonValues
import com.canhub.cropper.common.CommonVersionCheck
import com.canhub.cropper.options
import com.canhub.cropper.sample.SCropResultActivity
import com.canhub.cropper.sample.crop_image.domain.CameraEnumDomain
import com.canhub.cropper.sample.crop_image.domain.SCropImageContract
import com.canhub.cropper.sample.crop_image.presenter.SCropImagePresenter
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentCameraBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class SCropImageFragment :
    Fragment(),
    SCropImageContract.View {

    companion object {

        fun newInstance() = SCropImageFragment()

        const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        const val FILE_NAMING_PREFIX = "JPEG_"
        const val FILE_NAMING_SUFFIX = "_"
        const val FILE_FORMAT = ".jpg"
        const val AUTHORITY_SUFFIX = ".fileprovider"
    }

    private lateinit var binding: FragmentCameraBinding
    private val presenter: SCropImageContract.Presenter = SCropImagePresenter()
    private var photoUri: Uri? = null
    private var customUri: Uri? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> presenter.onPermissionResult(isGranted) }
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            presenter.onPickImageResult(it)
        }
    private val cropImage =
        registerForActivityResult(CropImageContract()) { presenter.onCropImageResult(it) }
    private val customCropImage = registerForActivityResult(CropImageContract()) {
        presenter.onCustomCropImageResult(customUri)
    }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        presenter.onTakePictureResult(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.bind(this)

        binding.startWithUri.setOnClickListener {
            presenter.startWithUriClicked()
        }
        binding.startWithoutUri.setOnClickListener {
            presenter.startWithoutUriClicked()
        }
        binding.startPickImageActivity.setOnClickListener {
            presenter.startPickImageActivityClicked()
        }

        presenter.onCreate(activity, context)
    }

    override fun startCropImage(option: CameraEnumDomain) {
        when (option) {
            CameraEnumDomain.START_WITH_URI -> startCameraWithUri()
            CameraEnumDomain.START_WITHOUT_URI -> startCameraWithoutUri()
            CameraEnumDomain.START_PICK_IMG -> startPickImage()
        }
    }

    private fun startPickImage() {
        pickImage.launch("image/*")
    }

    private fun startCameraWithoutUri() {
        if (customUri == null) customUri = buildUri()

        customCropImage.launch(
            options {
                setScaleType(CropImageView.ScaleType.CENTER)
                setCropShape(CropImageView.CropShape.OVAL)
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(4, 16)
                setMaxZoom(8)
                setAutoZoomEnabled(false)
                setMultiTouchEnabled(false)
                setCenterMoveEnabled(true)
                setShowCropOverlay(false)
                setAllowFlipping(false)
                setSnapRadius(10f)
                setTouchRadius(30f)
                setInitialCropWindowPaddingRatio(0.3f)
                setBorderLineThickness(5f)
                setBorderLineColor(R.color.black)
                setBorderCornerThickness(6f)
                setBorderCornerOffset(2f)
                setBorderCornerLength(20f)
                setBorderCornerColor(RED)
                setGuidelinesThickness(5f)
                setGuidelinesColor(RED)
                setBackgroundColor(Color.argb(119, 30, 60, 90))
                setMinCropWindowSize(20, 20)
                setMinCropResultSize(16, 16)
                setMaxCropResultSize(999, 999)
                setActivityTitle("CUSTOM title")
                setActivityMenuIconColor(RED)
                setOutputUri(customUri)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                setOutputCompressQuality(50)
                setRequestedSize(100, 100)
                setRequestedSize(100, 100, CropImageView.RequestSizeOptions.RESIZE_FIT)
                setInitialCropWindowRectangle(null)
                setInitialRotation(180)
                setAllowCounterRotation(true)
                setFlipHorizontally(true)
                setFlipVertically(true)
                setCropMenuCropButtonTitle("Custom name")
                setCropMenuCropButtonIcon(R.drawable.ic_gear_24)
                setAllowRotation(false)
                setNoOutputImage(false)
                setFixAspectRatio(true)
            }
        )
    }

    private fun startCameraWithUri() {
        cropImage.launch(
            options(photoUri) {
                setScaleType(CropImageView.ScaleType.FIT_CENTER)
                setCropShape(CropImageView.CropShape.RECTANGLE)
                setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                setAspectRatio(1, 1)
                setMaxZoom(4)
                setAutoZoomEnabled(true)
                setMultiTouchEnabled(true)
                setCenterMoveEnabled(true)
                setShowCropOverlay(true)
                setAllowFlipping(true)
                setSnapRadius(3f)
                setTouchRadius(48f)
                setInitialCropWindowPaddingRatio(0.1f)
                setBorderLineThickness(3f)
                setBorderLineColor(Color.argb(170, 255, 255, 255))
                setBorderCornerThickness(2f)
                setBorderCornerOffset(5f)
                setBorderCornerLength(14f)
                setBorderCornerColor(WHITE)
                setGuidelinesThickness(1f)
                setGuidelinesColor(R.color.white)
                setBackgroundColor(Color.argb(119, 0, 0, 0))
                setMinCropWindowSize(24, 24)
                setMinCropResultSize(20, 20)
                setMaxCropResultSize(99999, 99999)
                setActivityTitle("")
                setActivityMenuIconColor(0)
                setOutputUri(null)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                setOutputCompressQuality(90)
                setRequestedSize(0, 0)
                setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                setInitialCropWindowRectangle(null)
                setInitialRotation(90)
                setAllowCounterRotation(false)
                setFlipHorizontally(false)
                setFlipVertically(false)
                setCropMenuCropButtonTitle(null)
                setCropMenuCropButtonIcon(0)
                setAllowRotation(true)
                setNoOutputImage(false)
                setFixAspectRatio(false)
            }
        )
    }

    override fun showErrorMessage(message: String) {
        Log.e("Camera Error:", message)
        Toast.makeText(activity, "Crop failed: $message", Toast.LENGTH_SHORT).show()
    }

    override fun startTakePicture() {
        context?.let { ctx ->
            val authorities = "${ctx.applicationContext?.packageName}$AUTHORITY_SUFFIX"
            photoUri = FileProvider.getUriForFile(ctx, authorities, createImageFile())
            takePicture.launch(photoUri)
        }
    }

    override fun cameraPermissionLaunch() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun showDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(R.string.missing_camera_permission_title)
            setMessage(R.string.missing_camera_permission_body)
            setPositiveButton(R.string.ok) { _, _ -> presenter.onOk() }
            setNegativeButton(R.string.cancel) { _, _ -> presenter.onCancel() }
            create()
            show()
        }
    }

    override fun handleCropImageResult(uri: String) {
        SCropResultActivity.start(this, null, Uri.parse(uri), null)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "$FILE_NAMING_PREFIX${timeStamp}$FILE_NAMING_SUFFIX",
            FILE_FORMAT,
            storageDir
        )
    }

    private fun buildUri(): Uri? =
        try {
            context?.let { context ->
                // We have this because of a HUAWEI path bug when we use getUriForFile
                if (CommonVersionCheck.isAtLeastQ29()) {
                    try {
                        val file = File.createTempFile(
                            "cropped",
                            ".jpg",
                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        )
                        FileProvider.getUriForFile(
                            context,
                            context.packageName + CommonValues.authority,
                            file
                        )
                    } catch (e: Exception) {
                        Log.e("AIC", "${e.message}")
                        val file = File.createTempFile("cropped", ".jpg", context.cacheDir)
                        FileProvider.getUriForFile(
                            context,
                            context.packageName + CommonValues.authority,
                            file
                        )
                    }
                } else Uri.fromFile(File.createTempFile("cropped", ".jpg", context.cacheDir))
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to create temp file for output image", e)
        }
}
