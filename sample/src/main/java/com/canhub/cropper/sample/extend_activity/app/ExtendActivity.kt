package com.canhub.cropper.sample.extend_activity.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.core.app.ActivityCompat
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.sample.extend_activity.domain.ExtendContract
import com.canhub.cropper.sample.extend_activity.presenter.ExtendPresenter
import com.example.croppersample.R
import com.example.croppersample.databinding.ExtendedActivityBinding

internal class ExtendActivity : CropImageActivity(), ExtendContract.View {

    companion object {

        fun start(activity: Activity) {
            ActivityCompat.startActivity(
                activity,
                Intent(activity, ExtendActivity::class.java),
                null
            )
        }
    }

    private lateinit var binding: ExtendedActivityBinding
    private val presenter: ExtendContract.Presenter = ExtendPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ExtendedActivityBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        presenter.bindView(this)

        binding.saveBtn.setOnClickListener {
            cropImage() // CropImageActivity.cropImage()
        }
        binding.backBtn.setOnClickListener {
            onBackPressed() // CropImageActivity.onBackPressed()
        }
        binding.rotateText.setOnClickListener {
            presenter.onRotateClick()
        }
    }

    override fun setContentView(layoutResID: Int) {
        // Override this to use your custom layout
        super.setContentView(binding.root)
    }

    override fun onDestroy() {
        presenter.unbindView()
        super.onDestroy()
    }

    override fun rotate(counter: Int) {
        rotateImage(counter) // CropImageActivity.rotateImage(int)
    }

    override fun updateRotationCounter(counter: String) {
        binding.rotateText.text = getString(R.string.rotation_value, counter)
    }

    // Override this to add more information into the intent
    override fun getResultIntent(uri: Uri?, error: java.lang.Exception?, sampleSize: Int): Intent {
        val result = super.getResultIntent(uri, error, sampleSize)
        return result.putExtra("EXTRA_KEY", "Extra data")
    }

    override fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        Log.i("extend", "override this if you want to change the behaviour, like don't finish the activity")
        super.setResult(uri, error, sampleSize)
    }

    override fun setResultCancel() {
        Log.i("extend", "User this override to change behaviour when cancel")
        super.setResultCancel()
    }

    override fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        Log.i("extend", "If not using your layout, this can be one option to change colours. Check README and wiki for more")
        super.updateMenuItemIconColor(menu, itemId, color)
    }
}