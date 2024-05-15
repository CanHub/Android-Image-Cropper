package com.canhub.cropper.sample

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.canhub.cropper.sample.optionsdialog.SampleOptionsBottomSheet
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentCropImageViewBinding
import timber.log.Timber

internal class SampleUsingImageViewFragment :
  Fragment(),
  SampleOptionsBottomSheet.Listener,
  OnSetImageUriCompleteListener,
  OnCropImageCompleteListener {
  private var _binding: FragmentCropImageViewBinding? = null
  private val binding get() = _binding!!

  private var options: CropImageOptions? = null
  private val openPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    binding.cropImageView.setImageUriAsync(uri)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    setHasOptionsMenu(true)
    _binding = FragmentCropImageViewBinding.inflate(layoutInflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding.cropImageView.setOnSetImageUriCompleteListener(null)
    binding.cropImageView.setOnCropImageCompleteListener(null)
    _binding = null
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setOptions()

    binding.cropImageView.setOnSetImageUriCompleteListener(this)
    binding.cropImageView.setOnCropImageCompleteListener(this)

    if (savedInstanceState == null) {
      binding.cropImageView.imageResource = R.drawable.cat
    }

    binding.settings.setOnClickListener {
      SampleOptionsBottomSheet.show(childFragmentManager, options, this)
    }

    binding.searchImage.setOnClickListener {
      openPicker.launch("image/*")
    }

    binding.reset.setOnClickListener {
      binding.cropImageView.resetCropRect()
      binding.cropImageView.imageResource = R.drawable.cat
      onOptionsApplySelected(CropImageOptions())
    }
  }

  override fun onOptionsApplySelected(options: CropImageOptions) {
    this.options = options

    binding.cropImageView.setImageCropOptions(options)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.main, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.main_action_crop -> {
      binding.cropImageView.croppedImageAsync()
      true
    }
    R.id.main_action_rotate -> {
      binding.cropImageView.rotateImage(90)
      true
    }
    R.id.main_action_flip_horizontally -> {
      binding.cropImageView.flipImageHorizontally()
      true
    }
    R.id.main_action_flip_vertically -> {
      binding.cropImageView.flipImageVertically()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
    if (error != null) {
      Timber.tag("AIC-Sample").e(error, "Failed to load image by URI")
      Toast.makeText(activity, "Image load failed: " + error.message, Toast.LENGTH_LONG)
        .show()
    }
  }

  override fun onCropImageComplete(view: CropImageView, result: CropResult) {
    if (result.error == null) {
      val imageBitmap = if (binding.cropImageView.cropShape == CropImageView.CropShape.OVAL) {
        result.bitmap?.let(CropImage::toOvalBitmap)
      } else {
        result.bitmap
      }
      Timber.tag("AIC-Sample").i("Original bitmap: ${result.originalBitmap}")
      Timber.tag("AIC-Sample").i("Original uri: ${result.originalUri}")
      Timber.tag("AIC-Sample").i("Output bitmap: $imageBitmap")
      Timber.tag("AIC-Sample").i("Output uri: ${result.getUriFilePath(view.context)}")
      SampleResultScreen.start(this, imageBitmap, result.uriContent, result.sampleSize)
    } else {
      Timber.tag("AIC-Sample").e(result.error, "Failed to crop image")
      Toast
        .makeText(activity, "Crop failed: ${result.error?.message}", Toast.LENGTH_SHORT)
        .show()
    }
  }

  private fun setOptions() {
    binding.cropImageView.cropRect = Rect(100, 300, 500, 1200)
    onOptionsApplySelected(CropImageOptions())
  }
}
