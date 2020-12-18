package com.canhub.cropper.test

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.example.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cta.setOnClickListener {
            CropImage
                .activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
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
                else -> result?.error?.toString() ?: "Unknown"
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
