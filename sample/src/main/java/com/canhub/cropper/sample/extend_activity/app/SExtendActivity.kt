package com.canhub.cropper.sample.extend_activity.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.core.app.ActivityCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.sample.extend_activity.domain.SExtendContract
import com.canhub.cropper.sample.extend_activity.presenter.SExtendPresenter
import com.example.croppersample.R
import com.example.croppersample.databinding.ExtendedActivityBinding

internal class SExtendActivity : CropImageActivity(), SExtendContract.View {

    companion object {
        fun start(activity: Activity) {
            ActivityCompat.startActivity(
                activity,
                Intent(activity, SExtendActivity::class.java),
                null
            )
        }
    }

    private lateinit var binding: ExtendedActivityBinding
    private val presenter: SExtendContract.Presenter = SExtendPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ExtendedActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        presenter.bindView(this)

        binding.saveBtn.setOnClickListener {
            cropImage() // CropImageActivity.cropImage()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed() // CropImageActivity.onBackPressed()
        }
        binding.rotateText.setOnClickListener {
            presenter.onRotateClick()
        }

        setCropImageView(binding.cropImageView)
    }

    override fun showImageSourceDialog(openCamera: () -> Unit, openGallery: () -> Unit) {
        // Override this if you wanna a custom dialog layout
        super.showImageSourceDialog(openCamera, openGallery)
    }

    override fun setContentView(view: View) {
        // Override this to use your custom layout
        super.setContentView(binding.root)
    }

    override fun onDestroy() {
        presenter.unbindView()
        super.onDestroy()
    }

    override fun rotate(counter: Int) {
        binding.cropImageView.rotateImage(counter)
    }

    override fun updateRotationCounter(counter: String) {
        binding.rotateText.text = getString(R.string.rotation_value, counter)
    }

    override fun onPickImageResult(resultUri: Uri?) {
        super.onPickImageResult(resultUri)

        if (resultUri != null) {
            binding.cropImageView.setImageUriAsync(resultUri)
        }
    }

    // Override this to add more information into the intent
    override fun getResultIntent(uri: Uri?, error: java.lang.Exception?, sampleSize: Int): Intent {
        val result = super.getResultIntent(uri, error, sampleSize)
        return result.putExtra("EXTRA_KEY", "Extra data")
    }

    override fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val result = CropImage.ActivityResult(
            binding.cropImageView.imageUri,
            uri,
            error,
            binding.cropImageView.cropPoints,
            binding.cropImageView.cropRect,
            binding.cropImageView.rotatedDegrees,
            binding.cropImageView.wholeImageRect,
            sampleSize
        )

        Log.v("File Path", result.getUriFilePath(this).toString())
        binding.cropImageView.setImageUriAsync(result.uriContent)
    }

    override fun setResultCancel() {
        Log.i("extend", "User this override to change behaviour when cancel")
        super.setResultCancel()
    }

    override fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        Log.i(
            "extend",
            "If not using your layout, this can be one option to change colours. Check README and wiki for more"
        )
        super.updateMenuItemIconColor(menu, itemId, color)
    }
}
