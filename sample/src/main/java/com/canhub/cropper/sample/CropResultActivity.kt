package com.canhub.cropper.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.croppersample.R
import com.example.croppersample.databinding.ActivityCropResultBinding

class CropResultActivity : Activity() {

    companion object {

        fun start(fragment: Fragment, imgBitmap: Bitmap?, uri: Uri?, sampleSize: Int) {
            val intent = Intent(fragment.context, CropResultActivity::class.java)
                .putExtra(SAMPLE_SIZE, sampleSize)
                .putExtra(URI, uri)
                .putExtra(IMG_BITMAP, imgBitmap)

            fragment.startActivity(intent)
        }

        const val SAMPLE_SIZE = "SAMPLE_SIZE"
        private const val URI = "URI"
        private const val IMG_BITMAP = "IMG_BITMAP"
    }

    private lateinit var binding: ActivityCropResultBinding
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityCropResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resultImageView.setBackgroundResource(R.drawable.backdrop)

        image = intent.getParcelableExtra(IMG_BITMAP)

        image?.let {
            binding.resultImageView.setImageBitmap(it)
            val sampleSize = intent.getIntExtra(SAMPLE_SIZE, 1)
            val ratio = (10 * it.width / it.height.toDouble()).toInt() / 10.0
            val byteCount: Int = it.byteCount / 1024
            val desc =
                "(${it.width}, ${it.height}), Sample: $sampleSize, Ratio: $ratio, Bytes: $byteCount K"

            binding.resultImageText.text = desc
        } ?: run {
            val imageUri = intent.getParcelableExtra<Uri>(URI)

            if (imageUri != null) binding.resultImageView.setImageURI(imageUri)
            else Toast.makeText(this, "No image is set to show", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        releaseBitmap()
        super.onBackPressed()
    }

    fun onImageViewClicked() {
        releaseBitmap()
        finish()
    }

    private fun releaseBitmap() {
        image?.let {
            it.recycle()
            image = null
        }
    }
}