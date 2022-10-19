package com.canhub.cropper.sample

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.canhub.cropper.sample.options_dialog.SampleOptionsBottomSheet
import com.canhub.cropper.sample.options_dialog.SampleOptionsEntity
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentCropImageViewBinding

internal class SampleUsingImageView :
    Fragment(),
    SampleOptionsBottomSheet.Listener,
    OnSetImageUriCompleteListener,
    OnCropImageCompleteListener {

    companion object {

        fun newInstance() = SampleUsingImageView()
    }

    private lateinit var binding: FragmentCropImageViewBinding
    private var options: SampleOptionsEntity? = null
    private val openPicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            binding.cropImageView.setImageUriAsync(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentCropImageViewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOptions()

        binding.cropImageView.let {
            it.setOnSetImageUriCompleteListener(this)
            it.setOnCropImageCompleteListener(this)
            if (savedInstanceState == null) it.imageResource = R.drawable.cat
        }

        binding.settings.setOnClickListener {
            SampleOptionsBottomSheet.show(childFragmentManager, options, this)
        }

        binding.searchImage.setOnClickListener {
            openPicker.launch("image/*")
        }

        binding.reset.setOnClickListener {
            binding.cropImageView.apply {
                resetCropRect()
                options = options?.copy(
                    scaleType = CropImageView.ScaleType.FIT_CENTER,
                    flipHorizontally = false,
                    flipVertically = false,
                    autoZoom = true,
                    maxZoomLvl = 2
                )
                imageResource = R.drawable.cat
            }
        }
    }

    override fun onOptionsApplySelected(options: SampleOptionsEntity) {
        this.options = options

        binding.cropImageView.apply {
            cornerShape = options.cornerShape
            scaleType = options.scaleType
            cropShape = options.cropShape
            guidelines = options.guidelines
            if (options.ratio == null) setFixedAspectRatio(false)
            else {
                setFixedAspectRatio(true)
                setAspectRatio(options.ratio.first, options.ratio.second)
            }
            setMultiTouchEnabled(options.multiTouch)
            setCenterMoveEnabled(options.centerMove)
            isShowCropOverlay = options.showCropOverlay
            isShowProgressBar = options.showProgressBar
            isAutoZoomEnabled = options.autoZoom
            maxZoom = options.maxZoomLvl
            isFlippedHorizontally = options.flipHorizontally
            isFlippedVertically = options.flipVertically
            isShowCropLabel = options.showCropLabel
        }

        if (options.scaleType == CropImageView.ScaleType.CENTER_INSIDE)
            binding.cropImageView.imageResource = R.drawable.cat_small
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_action_crop -> {
                binding.cropImageView.croppedImageAsync()
                return true
            }
            R.id.main_action_rotate -> {
                binding.cropImageView.rotateImage(90)
                return true
            }
            R.id.main_action_flip_horizontally -> {
                binding.cropImageView.flipImageHorizontally()
                return true
            }
            R.id.main_action_flip_vertically -> {
                binding.cropImageView.flipImageVertically()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDetach() {
        super.onDetach()
        binding.cropImageView.setOnSetImageUriCompleteListener(null)
        binding.cropImageView.setOnCropImageCompleteListener(null)
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        if (error != null) {
            Log.e("AIC", "Failed to load image by URI", error)
            Toast.makeText(activity, "Image load failed: " + error.message, Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onCropImageComplete(view: CropImageView, result: CropResult) {
        handleCropResult(result)
    }

    private fun handleCropResult(result: CropResult?) {
        if (result != null && result.error == null) {
            val imageBitmap =
                if (binding.cropImageView.cropShape == CropImageView.CropShape.OVAL)
                    result.bitmap?.let { CropImage.toOvalBitmap(it) }
                else result.bitmap
            context?.let { Log.v("File Path", result.getUriFilePath(it).toString()) }
            SampleResultScreen.start(this, imageBitmap, result.uriContent, result.sampleSize)
        } else {
            Log.e("AIC", "Failed to crop image", result?.error)
            Toast
                .makeText(activity, "Crop failed: ${result?.error?.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setOptions() {
        binding.cropImageView.cropRect = Rect(100, 300, 500, 1200)
        onOptionsApplySelected(defaultOptions)
    }

    private val defaultOptions: SampleOptionsEntity = SampleOptionsEntity(
        scaleType = CropImageView.ScaleType.FIT_CENTER,
        cropShape = CropImageView.CropShape.RECTANGLE,
        cornerShape = CropImageView.CropCornerShape.RECTANGLE,
        guidelines = CropImageView.Guidelines.ON,
        ratio = Pair(1, 1),
        autoZoom = true,
        maxZoomLvl = 2,
        multiTouch = true,
        centerMove = true,
        showCropOverlay = true,
        showProgressBar = true,
        flipHorizontally = false,
        flipVertically = false,
        showCropLabel = false
    )
}
