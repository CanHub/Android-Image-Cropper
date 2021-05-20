package com.canhub.cropper.sample

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.canhub.cropper.sample.camera.app.SCameraFragment
import com.canhub.cropper.sample.camera_java.app.SCameraFragmentJava
import com.canhub.cropper.sample.crop_image_view.app.SCropImageViewFragment
import com.canhub.cropper.sample.extend_activity.app.SExtendActivity
import com.example.croppersample.R
import com.example.croppersample.databinding.ActivityMainBinding

internal class SMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.sampleCropImageView.setOnClickListener {
            hideButtons(binding)
            supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, SCropImageViewFragment.newInstance())
                .commit()
        }

        binding.sampleCustomActivity.setOnClickListener {
            SExtendActivity.start(this)
        }

        binding.sampleCropImage.setOnClickListener {
            hideButtons(binding)
            supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, SCameraFragment.newInstance())
                .commit()
        }

        binding.sampleCropImageJava.setOnClickListener {
            hideButtons(binding)
            supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, SCameraFragmentJava.newInstance())
                .commit()
        }

        onBackPressedDispatcher.addCallback {
            if (binding.container.visibility == View.VISIBLE) {
                binding.image.visibility = View.VISIBLE
                binding.buttons.visibility = View.VISIBLE
                binding.container.visibility = View.GONE
                binding.container.removeAllViews()
            } else {
                finish()
            }
        }
    }

    private fun hideButtons(binding: ActivityMainBinding) {
        binding.image.visibility = View.GONE
        binding.buttons.visibility = View.GONE
        binding.container.visibility = View.VISIBLE
    }
}
