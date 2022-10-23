package com.canhub.cropper.sample

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentCameraBinding
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class SampleCropFragment : Fragment() {
  private var _binding: FragmentCameraBinding? = null
  private val binding get() = _binding!!

  private var outputUri: Uri? = null
  private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
    if (it) {
      startCameraWithUri()
    } else {
      showErrorMessage("taking picture failed")
    }
  }

  private val cropImage = registerForActivityResult(CropImageContract()) { result ->
    when {
      result.isSuccessful -> {
        Timber.tag("Bitmap").v(result.bitmap.toString())
        Timber.tag("File Path").v(context?.let { result.getUriFilePath(it) }.toString())
        handleCropImageResult(result.uriContent.toString())
      }
      result is CropImage.CancelledResult -> showErrorMessage("cropping image was cancelled by the user")
      else -> showErrorMessage("cropping image failed")
    }
  }

  private val customCropImage = registerForActivityResult(CropImageContract()) {
    if (it !is CropImage.CancelledResult) {
      handleCropImageResult(it.uriContent.toString())
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
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
      options {
        setImageSource(
          includeGallery = includeGallery,
          includeCamera = includeCamera,
        )
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
        setInitialCropWindowPaddingRatio(0.0f)
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
        setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
        setOutputCompressQuality(90)
        setRequestedSize(0, 0)
        setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
        setInitialCropWindowRectangle(null)
        setInitialRotation(0)
        setAllowCounterRotation(false)
        setFlipHorizontally(false)
        setFlipVertically(false)
        setCropMenuCropButtonTitle(null)
        setCropMenuCropButtonIcon(0)
        setAllowRotation(true)
        setNoOutputImage(false)
        setFixAspectRatio(false)
      },
    )
  }

  private fun startCameraWithUri() {
    cropImage.launch(
      options(outputUri) {
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
        setInitialCropWindowPaddingRatio(0.0f)
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
        setInitialRotation(0)
        setAllowCounterRotation(false)
        setFlipHorizontally(false)
        setFlipVertically(false)
        setCropMenuCropButtonTitle(null)
        setCropMenuCropButtonIcon(0)
        setAllowRotation(true)
        setNoOutputImage(false)
        setFixAspectRatio(false)
      },
    )
  }

  private fun showErrorMessage(message: String) {
    Timber.tag("Camera Error:").e(message)
    Toast.makeText(activity, "Crop failed: $message", Toast.LENGTH_SHORT).show()
  }

  private fun handleCropImageResult(uri: String) {
    SampleResultScreen.start(this, null, Uri.parse(uri.replace("file:", "")), null)
  }

  private fun setupOutputUri() {
    if (outputUri == null) {
      context?.let { ctx ->
        val authorities = "${ctx.applicationContext?.packageName}$AUTHORITY_SUFFIX"
        outputUri = FileProvider.getUriForFile(ctx, authorities, createImageFile())
      }
    }
  }

  private fun createImageFile(): File {
    val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
    val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
      "$FILE_NAMING_PREFIX${timeStamp}$FILE_NAMING_SUFFIX",
      FILE_FORMAT,
      storageDir,
    )
  }

  companion object {
    const val DATE_FORMAT = "yyyyMMdd_HHmmss"
    const val FILE_NAMING_PREFIX = "JPEG_"
    const val FILE_NAMING_SUFFIX = "_"
    const val FILE_FORMAT = ".jpg"
    const val AUTHORITY_SUFFIX = ".cropper.fileprovider"
  }
}
