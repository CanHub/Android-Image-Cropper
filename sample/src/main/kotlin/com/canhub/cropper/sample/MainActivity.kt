package com.canhub.cropper.sample

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.croppersample.databinding.ActivityMainBinding

internal class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)

    setContentView(binding.root)

    binding.sampleCropImageView.setOnClickListener { SampleUsingImageViewFragment().show() }
    binding.sampleCustomActivity.setOnClickListener { SampleCustomActivity.start(this) }
    binding.sampleCropImage.setOnClickListener { SampleCropFragment().show() }

    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          val fragment = supportFragmentManager.findFragmentById(binding.container.id)
          isEnabled = fragment != null

          if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
          } else {
            onBackPressedDispatcher.onBackPressed()
          }

          isEnabled = true
        }
      },
    )
  }

  private fun Fragment.show() {
    supportFragmentManager
      .beginTransaction()
      .replace(binding.container.id, this)
      .commit()
  }
}
