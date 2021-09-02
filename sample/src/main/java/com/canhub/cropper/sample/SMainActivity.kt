package com.canhub.cropper.sample

import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.canhub.cropper.sample.crop_image.app.SCropImageFragment
import com.canhub.cropper.sample.crop_image_java.app.SCropImageFragmentJava
import com.canhub.cropper.sample.crop_image_view.app.SCropImageViewFragment
import com.canhub.cropper.sample.extend_activity.app.SExtendActivity
import com.example.croppersample.databinding.ActivityMainBinding

internal class SMainActivity : AppCompatActivity() {

    private val croppingRectangle: Rect = Rect(1239, 544, 2386, 1036)

    private val cropImage: ActivityResultLauncher<CropImageContractOptions> =
        registerForActivityResult(
            CropImageContract()
        ) { result: CropImageView.CropResult ->
//            Place a breakpoint in the next line
            val rect: Rect? = result.cropRect //The rectangle cropped out. If things go right, it should be equal to Rect(1239, 544, 2386, 1036).
        }

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

        binding.reproduceBug.setOnClickListener {
            val resources: Resources =
                applicationContext.resources
            val resourceId = resources.getIdentifier("reproduce_bug", "drawable", packageName)
            val uri: Uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build()
            val height = croppingRectangle.bottom-croppingRectangle.top
            val width = croppingRectangle.right-croppingRectangle.left
            cropImage.launch(
                options(uri) {
                    setInitialCropWindowRectangle(croppingRectangle)
                    setRequestedSize(width, height)
                    setMinCropResultSize(width, height)
                    setMaxCropResultSize(width, height)
                    setMinCropWindowSize(width, height)
                    setFixAspectRatio(false)
                }
            )
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
