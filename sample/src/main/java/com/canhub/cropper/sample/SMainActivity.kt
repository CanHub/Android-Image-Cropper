package com.canhub.cropper.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.canhub.cropper.sample.crop_image.app.SCropImageFragment
import com.canhub.cropper.sample.crop_image_java.app.SCropImageFragmentJava
import com.canhub.cropper.sample.crop_image_view.app.SCropImageViewFragment
import com.canhub.cropper.sample.extend_activity.app.SExtendActivity
import com.example.croppersample.databinding.ActivityMainBinding

internal class SMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.sampleCropImageView.setOnClickListener {
            SCropImageViewFragment.newInstance().show()
        }

        binding.sampleCustomActivity.setOnClickListener {
            SExtendActivity.start(this)
        }

        binding.sampleCropImage.setOnClickListener {
            SCropImageFragment.newInstance().show()
        }

        binding.sampleCropImageJava.setOnClickListener {
            SCropImageFragmentJava.newInstance().show()
        }
    }

    private fun Fragment.show() {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.container.id, this)
            .commit()
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(binding.container.id)?.apply {
            supportFragmentManager.beginTransaction().remove(this).commit()
            return
        }
        super.onBackPressed()
    }
}
