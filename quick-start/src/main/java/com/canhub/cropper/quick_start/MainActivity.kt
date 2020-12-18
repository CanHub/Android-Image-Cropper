package com.canhub.cropper.quick_start

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.quick.start.R
import com.canhub.cropper.quick.start.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCta.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.OVAL)
                .setCropMenuCropButtonTitle("Done")
                .setRequestedSize(400, 400)
                .setCropMenuCropButtonIcon(R.drawable.ic_launcher)
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)
            val message = when (resultCode) {
                RESULT_OK -> {
                    binding.quickStartCroppedImage.setImageURI(result?.uri)
                    "Cropping successful, Sample: ${result?.sampleSize}"
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE ->
                    "Cropping failed: ${result?.error}"
                else -> result?.error.toString()
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
