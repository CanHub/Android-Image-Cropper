package com.canhub.cropper.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.canhub.cropper.parcelable
import com.example.croppersample.R
import com.example.croppersample.databinding.ActivityCropResultBinding

class SampleResultScreen : Activity() {
  private lateinit var binding: ActivityCropResultBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    binding = ActivityCropResultBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.resultImageView.setBackgroundResource(R.drawable.backdrop)
    binding.resultImageView.setOnClickListener {
      releaseBitmap()
      finish()
    }

    image?.let {
      binding.resultImageView.setImageBitmap(it)
      val sampleSize = intent.getIntExtra(SAMPLE_SIZE, 1)
      val ratio = (10 * it.width / it.height.toDouble()).toInt() / 10.0
      val byteCount: Int = it.byteCount / 1024
      val desc =
        "(${it.width}, ${it.height}), Sample: $sampleSize, Ratio: $ratio, Bytes: $byteCount K"

      binding.resultImageText.text = desc
    } ?: run {
      val imageUri = intent.parcelable<Uri>(URI)

      if (imageUri != null) {
        binding.resultImageView.setImageURI(imageUri)
      } else {
        Toast.makeText(this, "No image is set to show", Toast.LENGTH_LONG).show()
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    releaseBitmap()
  }

  private fun releaseBitmap() {
    image?.let {
      it.recycle()
      image = null
    }
  }

  companion object {
    fun start(fragment: Fragment, imageBitmap: Bitmap?, uri: Uri?, sampleSize: Int?) {
      image = imageBitmap

      fragment.startActivity(
        Intent(fragment.context, SampleResultScreen::class.java)
          .putExtra(SAMPLE_SIZE, sampleSize)
          .putExtra(URI, uri),
      )
    }

    // This is used, because bitmap is huge and cannot be passed in Intent without throw and exception
    private var image: Bitmap? = null

    private const val SAMPLE_SIZE = "SAMPLE_SIZE"
    private const val URI = "URI"
  }
}
