package com.canhub.cropper.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.sample.camera.app.CameraFragment
import com.canhub.cropper.sample.crop_image_view.app.CropImageViewFragment
import com.canhub.cropper.sample.extend_activity.app.ExtendActivity
import com.example.croppersample.R
import com.example.croppersample.databinding.ActivityMainBinding

internal class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.sampleCropImageView.setOnClickListener {
            hideButtons(binding)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, CropImageViewFragment.newInstance())
                .commit()
        }

        binding.sampleCustomActivity.setOnClickListener {
            ExtendActivity.start(this)
        }

        binding.sampleCropImage.setOnClickListener {
            hideButtons(binding)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, CameraFragment.newInstance())
                .commit()
        }
    }

    private fun hideButtons(binding: ActivityMainBinding) {
        binding.image.visibility = View.GONE
        binding.buttons.visibility = View.GONE
    }
}
