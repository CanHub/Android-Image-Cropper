package com.canhub.cropper.sample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.app.ActivityCompat
import com.canhub.cropper.CropImage.ActivityResult
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.CropImageView
import com.example.croppersample.R
import com.example.croppersample.databinding.ExtendedActivityBinding
import timber.log.Timber

internal class SampleCustomActivity : CropImageActivity() {

  companion object {
    fun start(activity: Activity) {
      ActivityCompat.startActivity(
        activity,
        Intent(activity, SampleCustomActivity::class.java),
        null,
      )
    }
  }

  private lateinit var binding: ExtendedActivityBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ExtendedActivityBinding.inflate(layoutInflater)

    super.onCreate(savedInstanceState)

    binding.saveBtn.setOnClickListener { cropImage() }
    binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    binding.rotateText.setOnClickListener { onRotateClick() }

    binding.cropImageView.setOnCropWindowChangedListener {
      updateExpectedImageSize()
    }

    setCropImageView(binding.cropImageView)
  }

  override fun onSetImageUriComplete(
    view: CropImageView,
    uri: Uri,
    error: Exception?,
  ) {
    super.onSetImageUriComplete(view, uri, error)

    updateRotationCounter()
    updateExpectedImageSize()
  }

  private fun updateExpectedImageSize() {
    binding.expectedImageSize.text = binding.cropImageView.expectedImageSize().toString()
  }

  override fun setContentView(view: View) {
    super.setContentView(binding.root)
  }

  private fun updateRotationCounter() {
    binding.rotateText.text = getString(R.string.rotation_value, binding.cropImageView.rotatedDegrees.toString())
  }

  override fun onPickImageResult(resultUri: Uri?) {
    super.onPickImageResult(resultUri)

    if (resultUri != null) {
      binding.cropImageView.setImageUriAsync(resultUri)
    }
  }

  override fun getResultIntent(uri: Uri?, error: java.lang.Exception?, sampleSize: Int): Intent {
    val result = super.getResultIntent(uri, error, sampleSize)
    // Adding some more information.
    return result.putExtra("EXTRA_KEY", "Extra data")
  }

  override fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
    val result = ActivityResult(
      originalUri = binding.cropImageView.imageUri,
      uriContent = uri,
      error = error,
      cropPoints = binding.cropImageView.cropPoints,
      cropRect = binding.cropImageView.cropRect,
      rotation = binding.cropImageView.rotatedDegrees,
      wholeImageRect = binding.cropImageView.wholeImageRect,
      sampleSize = sampleSize,
    )

    Timber.tag("AIC-Sample").i("Original bitmap: ${result.originalBitmap}")
    Timber.tag("AIC-Sample").i("Original uri: ${result.originalUri}")
    Timber.tag("AIC-Sample").i("Output bitmap: ${result.bitmap}")
    Timber.tag("AIC-Sample").i("Output uri: ${result.getUriFilePath(this)}")
    binding.cropImageView.setImageUriAsync(result.uriContent)
  }

  override fun setResultCancel() {
    Timber.tag("AIC-Sample").i("User this override to change behaviour when cancel")
    super.setResultCancel()
  }

  override fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
    Timber.tag("AIC-Sample").i("If not using your layout, this can be one option to change colours")
    super.updateMenuItemIconColor(menu, itemId, color)
  }

  private fun onRotateClick() {
    binding.cropImageView.rotateImage(90)
    updateRotationCounter()
  }
}
