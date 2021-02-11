package com.canhub.cropper.sample.crop_image_view.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.canhub.cropper.sample.CropResultActivity
import com.canhub.cropper.sample.crop_image_view.domain.CropImageViewContract
import com.canhub.cropper.sample.crop_image_view.presenter.CropImageViewPresenter
import com.canhub.cropper.sample.options_dialog.app.OptionsDialogBottomSheet
import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentMainBinding

internal class CropImageViewFragment :
    Fragment(),
    CropImageViewContract.View,
    OptionsDialogBottomSheet.Listener,
    OnSetImageUriCompleteListener,
    OnCropImageCompleteListener {

    companion object {

        fun newInstance() = CropImageViewFragment()
    }

    private lateinit var binding: FragmentMainBinding
    private val presenter = CropImageViewPresenter()
    private var options: OptionsDomain? = null
    private var cropImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bind(this)
        presenter.onViewCreated()

        binding.cropImageView.let {
            it.setOnSetImageUriCompleteListener(this)
            it.setOnCropImageCompleteListener(this)
            if (savedInstanceState == null) it.imageResource = R.drawable.cat
        }

        binding.settings.setOnClickListener {
            OptionsDialogBottomSheet.show(childFragmentManager, options, this)
        }

        binding.searchImage.setOnClickListener {
            context?.let { ctx ->
                if (CropImage.isExplicitCameraPermissionRequired(ctx)) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE
                    )
                } else context?.let { context -> CropImage.startPickImageActivity(context, this) }
            }
        }

        binding.reset.setOnClickListener {
            binding.cropImageView.apply {
                resetCropRect()
                options = options?.copy(
                    scaleType = CropImageView.ScaleType.FIT_CENTER,
                    flipHorizontal = false,
                    flipVertically = false,
                    autoZoom = true,
                    maxZoomLvl = 2
                )
                imageResource = R.drawable.cat
            }
        }
    }

    override fun onOptionsApplySelected(options: OptionsDomain) {
        this.options = options

        binding.cropImageView.apply {
            scaleType = options.scaleType
            cropShape = options.cropShape
            guidelines = options.guidelines
            if (options.ratio == null) setFixedAspectRatio(false)
            else {
                setFixedAspectRatio(true)
                setAspectRatio(options.ratio.first, options.ratio.second)
            }
            setMultiTouchEnabled(options.multiTouch)
            isShowCropOverlay = options.showCropOverlay
            isShowProgressBar = options.showProgressBar
            isAutoZoomEnabled = options.autoZoom
            maxZoom = options.maxZoomLvl
            isFlippedHorizontally = options.flipHorizontal
            isFlippedVertically = options.flipVertically
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
                binding.cropImageView.getCroppedImageAsync()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ->
                    handleCropResult(CropImage.getActivityResult(data))
                CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                    val ctx = context
                    val imageUri = ctx?.let { CropImage.getPickImageResultUri(it, data) }

                    if (imageUri != null &&
                        CropImage.isReadExternalStoragePermissionsRequired(ctx, imageUri)
                    ) {
                        cropImageUri = imageUri
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                        )
                    } else {
                        binding.cropImageView.setImageUriAsync(imageUri)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activity?.let { CropImage.startPickImageActivity(it) }
            } else {
                Toast
                    .makeText(context, "Cancelling, permissions not granted", Toast.LENGTH_LONG)
                    .show()
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (cropImageUri != null &&
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                binding.cropImageView.setImageUriAsync(cropImageUri)
            } else {
                Toast
                    .makeText(context, "Cancelling, permissions not granted", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun handleCropResult(result: CropResult?) {
        if (result != null && result.error == null) {
            val imageBitmap =
                if (binding.cropImageView.cropShape == CropImageView.CropShape.OVAL)
                    CropImage.toOvalBitmap(result.bitmap)
                else result.bitmap

            CropResultActivity.start(this, imageBitmap, result.uri, result.sampleSize)
        } else {
            Log.e("AIC", "Failed to crop image", result?.error)
            Toast
                .makeText(activity, "Crop failed: ${result?.error?.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun setOptions(options: OptionsDomain) {
        binding.cropImageView.cropRect = Rect(100, 300, 500, 1200)
        onOptionsApplySelected(options)
    }
}
