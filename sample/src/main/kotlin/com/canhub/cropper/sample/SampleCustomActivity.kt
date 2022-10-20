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
  private var counter = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ExtendedActivityBinding.inflate(layoutInflater)

    super.onCreate(savedInstanceState)
    updateRotationCounter(counter.toString())

    binding.saveBtn.setOnClickListener { cropImage() }
    binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    binding.rotateText.setOnClickListener { onRotateClick() }

    setCropImageView(binding.cropImageView)
  }

  override fun setContentView(view: View) {
    // Override this to use your custom layout
    super.setContentView(binding.root)
  }

  private fun updateRotationCounter(counter: String) {
    binding.rotateText.text = getString(R.string.rotation_value, counter)
  }

  override fun onPickImageResult(resultUri: Uri?) {
    super.onPickImageResult(resultUri)

    if (resultUri != null) binding.cropImageView.setImageUriAsync(resultUri)
  }

  // Override this to add more information into the intent
  override fun getResultIntent(uri: Uri?, error: java.lang.Exception?, sampleSize: Int): Intent {
    val result = super.getResultIntent(uri, error, sampleSize)
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

    Timber.tag("File Path").v(result.getUriFilePath(this).toString())
    binding.cropImageView.setImageUriAsync(result.uriContent)
  }

  override fun setResultCancel() {
    Timber.tag("extend").i("User this override to change behaviour when cancel")
    super.setResultCancel()
  }

  override fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
    Timber.tag("extend")
      .i(
        "If not using your layout, this can be one option to change colours. Check README and wiki for more",
      )
    super.updateMenuItemIconColor(menu, itemId, color)
  }

  private fun onRotateClick() {
    counter += 90
    binding.cropImageView.rotateImage(90)
    if (counter == 360) counter = 0
    updateRotationCounter(counter.toString())
  }
}
