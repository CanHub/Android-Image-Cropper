package com.canhub.cropper.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.croppersample.databinding.ActivityMainBinding

internal class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.sampleCropImageView.setOnClickListener { SampleUsingImageView.newInstance().show() }
        binding.sampleCustomActivity.setOnClickListener { SampleCustomActivity.start(this) }
        binding.sampleCropImage.setOnClickListener { SampleCrop.newInstance().show() }
        binding.sampleCropImageJava.setOnClickListener { SampleCropJava.newInstance().show() }
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
