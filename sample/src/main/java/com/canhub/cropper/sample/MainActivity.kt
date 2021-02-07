package com.canhub.cropper.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.sample.crop_image_view.app.CropImageViewFragment
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

        binding.sampleCropImage.setOnClickListener {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG).show()
            // hideButtons(binding)
        }

        binding.sampleCustomActivity.setOnClickListener {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_LONG).show()
            // hideButtons(binding)
        }
    }

    private fun hideButtons(binding: ActivityMainBinding) {
        binding.sampleCropImageView.visibility = View.GONE
        binding.sampleCropImage.visibility = View.GONE
        binding.sampleCustomActivity.visibility = View.GONE
    }
}