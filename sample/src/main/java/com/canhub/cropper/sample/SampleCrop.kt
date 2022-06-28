package com.canhub.cropper.sample

import android.graphics.Bitmap
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class SampleCrop : Fragment() {

    companion object {

        fun newInstance() = SampleCrop()

        const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        const val FILE_NAMING_PREFIX = "JPEG_"
        const val FILE_NAMING_SUFFIX = "_"
        const val FILE_FORMAT = ".jpg"
        const val AUTHORITY_SUFFIX = ".cropper.fileprovider"
    }

    private lateinit var binding: FragmentCameraBinding
    private var outputUri: Uri? = null
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) startCameraWithUri() else showErrorMessage("taking picture failed")
    }
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        when {
            result.isSuccessful -> {
                Log.v("Bitmap", result.bitmap.toString())
                Log.v("File Path", context?.let { result.getUriFilePath(it) }.toString())
                handleCropImageResult(result.uriContent.toString())
            }
            result is CropImage.CancelledResult -> {
                showErrorMessage("cropping image was cancelled by the user")
            }
            else -> {
                showErrorMessage("cropping image failed")
            }
        }
    }
    private val customCropImage = registerForActivityResult(CropImageContract()) {
        handleCropImageResult(it.uriContent.toString())
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

        binding.takePictureBeforeCallLibraryWithUri.setOnClickListener {
            setupOutputUri()
            takePicture.launch(outputUri)
        }
        binding.callLibraryWithoutUri.setOnClickListener {
            startCameraWithoutUri(includeCamera = true, includeGallery = true)
        }
        binding.callLibraryWithoutUriCameraOnly.setOnClickListener {
            startCameraWithoutUri(includeCamera = true, includeGallery = false)
        }
        binding.callLibraryWithoutUriGalleryOnly.setOnClickListener {
            startCameraWithoutUri(includeCamera = false, includeGallery = true)
        }
    }

    private fun startCameraWithoutUri(includeCamera: Boolean, includeGallery: Boolean) {
        customCropImage.launch(
            CropImageContractOptions(
                uri = null,
                CropImageOptions(
                    imageSourceIncludeGallery = includeGallery,
                    imageSourceIncludeCamera = includeCamera,

                    // Normal Settings
                    scaleType = CropImageView.ScaleType.FIT_CENTER,
                    cropShape = CropImageView.CropShape.RECTANGLE,
                    guidelines = CropImageView.Guidelines.ON_TOUCH,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    maxZoom = 4,
                    autoZoomEnabled = true,
                    multiTouchEnabled = true,
                    centerMoveEnabled = true,
                    showCropOverlay = true,
                    allowFlipping = true,
                    snapRadius = 3f,
                    touchRadius = 48f,
                    initialCropWindowPaddingRatio = 0.1f,
                    borderLineThickness = 3f,
                    borderLineColor = Color.argb(170, 255, 255, 255),
                    borderCornerThickness = 2f,
                    borderCornerOffset = 5f,
                    borderCornerLength = 14f,
                    borderCornerColor = WHITE,
                    guidelinesThickness = 1f,
                    guidelinesColor = ContextCompat.getColor(requireContext(), R.color.white),
                    backgroundColor = Color.argb(119, 0, 0, 0),
                    minCropWindowHeight = 24,
                    minCropWindowWidth = 24,
                    minCropResultHeight = 20,
                    minCropResultWidth = 20,
                    maxCropResultHeight = 99999,
                    maxCropResultWidth = 99999,
                    activityTitle = "",
                    activityMenuIconColor = 0,
                    outputCompressFormat = Bitmap.CompressFormat.JPEG,
                    outputCompressQuality = 90,
                    outputRequestWidth = 0,
                    outputRequestHeight = 0,
                    outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_INSIDE,
                    initialCropWindowRectangle = null,
                    initialRotation = 0,
                    allowCounterRotation = false,
                    flipHorizontally = false,
                    flipVertically = false,
                    cropMenuCropButtonTitle = null,
                    cropMenuCropButtonIcon = 0,
                    allowRotation = true,
                    noOutputImage = false,
                    fixAspectRatio = false,
                )
            )
        )
    }

    private fun startCameraWithUri() {
        cropImage.launch(
            CropImageContractOptions(
                outputUri,
                CropImageOptions(
                    scaleType = CropImageView.ScaleType.FIT_CENTER,
                    cropShape = CropImageView.CropShape.RECTANGLE,
                    guidelines = CropImageView.Guidelines.ON_TOUCH,
                    aspectRatioX = 1,
                    aspectRatioY = 1,
                    maxZoom = 4,
                    autoZoomEnabled = true,
                    multiTouchEnabled = true,
                    centerMoveEnabled = true,
                    showCropOverlay = true,
                    allowFlipping = true,
                    snapRadius = 3f,
                    touchRadius = 48f,
                    initialCropWindowPaddingRatio = 0.1f,
                    borderLineThickness = 3f,
                    borderLineColor = Color.argb(170, 255, 255, 255),
                    borderCornerThickness = 2f,
                    borderCornerOffset = 5f,
                    borderCornerLength = 14f,
                    borderCornerColor = WHITE,
                    guidelinesThickness = 1f,
                    guidelinesColor = ContextCompat.getColor(requireContext(), R.color.white),
                    backgroundColor = Color.argb(119, 0, 0, 0),
                    minCropWindowHeight = 24,
                    minCropWindowWidth = 24,
                    minCropResultHeight = 20,
                    minCropResultWidth = 20,
                    maxCropResultHeight = 99999,
                    maxCropResultWidth = 99999,
                    activityTitle = "",
                    activityMenuIconColor = 0,
                    customOutputUri = null,
                    outputCompressFormat = Bitmap.CompressFormat.JPEG,
                    outputCompressQuality = 90,
                    outputRequestWidth = 0,
                    outputRequestHeight = 0,
                    outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_INSIDE,
                    initialCropWindowRectangle = null,
                    initialRotation = 0,
                    allowCounterRotation = false,
                    flipHorizontally = false,
                    flipVertically = false,
                    cropMenuCropButtonTitle = null,
                    cropMenuCropButtonIcon = 0,
                    allowRotation = true,
                    noOutputImage = false,
                    fixAspectRatio = false,
                )
            )
        )
    }

    private fun showErrorMessage(message: String) {
        Log.e("Camera Error:", message)
        Toast.makeText(activity, "Crop failed: $message", Toast.LENGTH_SHORT).show()
    }

    private fun handleCropImageResult(uri: String) {
        SampleResultScreen.start(this, null, Uri.parse(uri.replace("file:", "")), null)
    }

    private fun setupOutputUri() {
        if (outputUri == null) context?.let { ctx ->
            val authorities = "${ctx.applicationContext?.packageName}$AUTHORITY_SUFFIX"
            outputUri = FileProvider.getUriForFile(ctx, authorities, createImageFile())
        }
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
