package com.canhub.cropper.sample.camera.app

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.RED
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.CropResultActivity
import com.canhub.cropper.sample.camera.domain.CameraContract
import com.canhub.cropper.sample.camera.domain.CameraEnumDomain
import com.canhub.cropper.sample.camera.presenter.CameraPresenter
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class CameraFragment :
    Fragment(),
    CameraContract.View {

    companion object {

        fun newInstance() = CameraFragment()

        const val CODE_PHOTO_CAMERA = 811917
        const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        const val FILE_NAMING_PREFIX = "JPEG_"
        const val FILE_NAMING_SUFFIX = "_"
        const val FILE_FORMAT = ".jpg"
        const val AUTHORITY_SUFFIX = ".fileprovider"
        const val CUSTOM_REQUEST_CODE = 8119153
    }

    private lateinit var binding: FragmentCameraBinding
    private val presenter: CameraContract.Presenter = CameraPresenter()
    private var photoUri: Uri? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> presenter.onPermissionResult(isGranted) }

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
        binding.startActivityForResult.setOnClickListener {
            presenter.startActivityForResultClicked()
        }

        presenter.onCreate(activity, context)
    }

    override fun startCropImage(option: CameraEnumDomain) {
        when (option) {
            CameraEnumDomain.START_WITH_URI -> startCameraWithUri()
            CameraEnumDomain.START_WITHOUT_URI -> startCameraWithoutUri()
            CameraEnumDomain.START_PICK_IMG -> startPickImage()
            CameraEnumDomain.START_FOR_RESULT -> startForResult()
        }
    }

    private fun startForResult() {
        context?.let {
            val intent = CropImage.getPickImageChooserIntent(it, "Selection Baby", true, false)

            this.startActivityForResult(intent, CUSTOM_REQUEST_CODE)
        }
    }

    private fun startPickImage() {
        context?.let { CropImage.startPickImageActivity(it, this) }
    }

    private fun startCameraWithoutUri() {
        context?.let { ctx ->
            CropImage.activity()
                .setScaleType(CropImageView.ScaleType.CENTER)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4, 16)
                .setMaxZoom(8)
                .setAutoZoomEnabled(false)
                .setMultiTouchEnabled(false)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(false)
                .setAllowFlipping(false)
                .setSnapRadius(10f)
                .setTouchRadius(30f)
                .setInitialCropWindowPaddingRatio(0.3f)
                .setBorderLineThickness(5f)
                .setBorderLineColor(R.color.black)
                .setBorderCornerThickness(6f)
                .setBorderCornerOffset(2f)
                .setBorderCornerLength(20f)
                .setBorderCornerColor(RED)
                .setGuidelinesThickness(5f)
                .setGuidelinesColor(RED)
                .setBackgroundColor(Color.argb(119, 30, 60, 90))
                .setMinCropWindowSize(20, 20)
                .setMinCropResultSize(16, 16)
                .setMaxCropResultSize(999, 999)
                .setActivityTitle("CUSTOM title")
                .setActivityMenuIconColor(RED)
                .setOutputUri(null)
                .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                .setOutputCompressQuality(50)
                .setRequestedSize(100, 100)
                .setRequestedSize(100, 100, CropImageView.RequestSizeOptions.RESIZE_FIT)
                .setInitialCropWindowRectangle(null)
                .setInitialRotation(180)
                .setAllowCounterRotation(true)
                .setFlipHorizontally(true)
                .setFlipVertically(true)
                .setCropMenuCropButtonTitle("Custom name")
                .setCropMenuCropButtonIcon(R.drawable.ic_gear_24)
                .setAllowRotation(false)
                .setNoOutputImage(false)
                .setFixAspectRatio(true)
                .start(ctx, this)
        }
    }

    private fun startCameraWithUri() {
        context?.let { ctx ->
            CropImage.activity(photoUri)
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
                .setMaxZoom(4)
                .setAutoZoomEnabled(true)
                .setMultiTouchEnabled(true)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(true)
                .setAllowFlipping(true)
                .setSnapRadius(3f)
                .setTouchRadius(48f)
                .setInitialCropWindowPaddingRatio(0.1f)
                .setBorderLineThickness(3f)
                .setBorderLineColor(Color.argb(170, 255, 255, 255))
                .setBorderCornerThickness(2f)
                .setBorderCornerOffset(5f)
                .setBorderCornerLength(14f)
                .setBorderCornerColor(WHITE)
                .setGuidelinesThickness(1f)
                .setGuidelinesColor(R.color.white)
                .setBackgroundColor(Color.argb(119, 0, 0, 0))
                .setMinCropWindowSize(24, 24)
                .setMinCropResultSize(20, 20)
                .setMaxCropResultSize(99999, 99999)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setOutputUri(null)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .setRequestedSize(0, 0)
                .setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                .setInitialCropWindowRectangle(null)
                .setInitialRotation(90)
                .setAllowCounterRotation(false)
                .setFlipHorizontally(false)
                .setFlipVertically(false)
                .setCropMenuCropButtonTitle(null)
                .setCropMenuCropButtonIcon(0)
                .setAllowRotation(true)
                .setNoOutputImage(false)
                .setFixAspectRatio(false)
                .start(ctx, this)
        }
    }

    override fun showErrorMessage(message: String) {
        Log.e("Camera Error:", message)
        Toast.makeText(activity, "Crop failed: $message", Toast.LENGTH_SHORT).show()
    }

    override fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            context?.let { ctx ->
                if (takePictureIntent.resolveActivity(ctx.packageManager) != null) {
                    val authorities = "${ctx.applicationContext?.packageName}$AUTHORITY_SUFFIX"
                    photoUri = FileProvider.getUriForFile(ctx, authorities, createImageFile())
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, CODE_PHOTO_CAMERA)
                }
            }
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
        CropResultActivity.start(this, null, Uri.parse(uri), null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(resultCode, requestCode, data)
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
}
