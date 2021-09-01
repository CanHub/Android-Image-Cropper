package com.canhub.cropper.sample.crop_image_view.app

import android.Manifest
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
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.canhub.cropper.PickImageContract
import com.canhub.cropper.sample.SCropResultActivity
import com.canhub.cropper.sample.crop_image_view.domain.SCropImageViewContract
import com.canhub.cropper.sample.crop_image_view.presenter.SCropImageViewPresenter
import com.canhub.cropper.sample.options_dialog.app.SOptionsDialogBottomSheet
import com.canhub.cropper.sample.options_dialog.domain.SOptionsDomain
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentCropImageViewBinding

internal class SCropImageViewFragment :
    Fragment(),
    SCropImageViewContract.View,
    SOptionsDialogBottomSheet.Listener,
    OnSetImageUriCompleteListener,
    OnCropImageCompleteListener {

    companion object {

        fun newInstance() = SCropImageViewFragment()
    }

    private lateinit var binding: FragmentCropImageViewBinding
    private val presenter = SCropImageViewPresenter()
    private var options: SOptionsDomain? = null
    private var cropImageUri: Uri? = null

    private val openPicker = registerForActivityResult(PickImageContract()) { imageUri ->
        if (imageUri != null && CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri)
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

        presenter.bind(this)
        presenter.onViewCreated()

        binding.cropImageView.let {
            it.setOnSetImageUriCompleteListener(this)
            it.setOnCropImageCompleteListener(this)
            if (savedInstanceState == null) it.imageResource = R.drawable.cat
        }

        binding.settings.setOnClickListener {
            SOptionsDialogBottomSheet.show(childFragmentManager, options, this)
        }

        binding.searchImage.setOnClickListener {
            context?.let { ctx ->
                if (CropImage.isExplicitCameraPermissionRequired(ctx)) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE
                    )
                } else openPicker.launch(true)
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

    override fun onOptionsApplySelected(options: SOptionsDomain) {
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
            setCenterMoveEnabled(options.centerMove)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPicker.launch(true)
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
                    result.bitmap?.let { CropImage.toOvalBitmap(it) }
                else result.bitmap
            context?.let { Log.v("File Path", result.getUriFilePath(it).toString()) }
            SCropResultActivity.start(this, imageBitmap, result.uriContent, result.sampleSize)
        } else {
            Log.e("AIC", "Failed to crop image", result?.error)
            Toast
                .makeText(activity, "Crop failed: ${result?.error?.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun setOptions(options: SOptionsDomain) {
        binding.cropImageView.cropRect = Rect(100, 300, 500, 1200)
        onOptionsApplySelected(options)
    }
}
