package com.canhub.cropper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.canhub.cropper.common.CommonVersionCheck
import com.canhub.cropper.databinding.CropImageActivityBinding

/**
 * Built-in activity for image cropping.<br></br>
 * Use [CropImage.activity] to create a builder to start this activity.
 */
open class CropImageActivity :
    AppCompatActivity(),
    OnSetImageUriCompleteListener,
    OnCropImageCompleteListener {

    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    var cropImageUri: Uri? = null

    /**
     * the options that were set for the crop image
     */
    lateinit var cropImageOptions: CropImageOptions
    lateinit var pickImageOptions: PickImageContractOptions

    /** The crop image view library widget used in the activity */
    private var cropImageView: CropImageView? = null
    private lateinit var binding: CropImageActivityBinding
    private val pickImage = registerForActivityResult(PickImageContract()) { onPickImageResult(it) }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CropImageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCropImageView(binding.cropImageView)
        val bundle = intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
        cropImageUri = bundle?.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE)
        cropImageOptions =
            bundle?.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS) ?: CropImageOptions()
        pickImageOptions =
            bundle?.getParcelable(CropImage.PICK_IMAGE_SOURCE_OPTIONS) ?: PickImageContractOptions(
            includeCamera = true
        )

        if (savedInstanceState == null) {
            if (cropImageUri == null || cropImageUri == Uri.EMPTY) {
                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE
                    )
                } else {
                    pickImage.launch(pickImageOptions)
                }
            } else if (
                cropImageUri?.let {
                    CropImage.isReadExternalStoragePermissionsRequired(this, it)
                } == true &&
                CommonVersionCheck.isAtLeastM23()
            ) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                )
            } else {
                // no permissions required or already granted, can start crop image activity
                cropImageView?.setImageUriAsync(cropImageUri)
            }
        }

        supportActionBar?.let {
            title =
                if (cropImageOptions.activityTitle.isNotEmpty())
                    cropImageOptions.activityTitle
                else
                    resources.getString(R.string.crop_image_activity_title)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    public override fun onStart() {
        super.onStart()
        cropImageView?.setOnSetImageUriCompleteListener(this)
        cropImageView?.setOnCropImageCompleteListener(this)
    }

    public override fun onStop() {
        super.onStop()
        cropImageView?.setOnSetImageUriCompleteListener(null)
        cropImageView?.setOnCropImageCompleteListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.crop_image_menu, menu)

        if (!cropImageOptions.allowRotation) {
            menu.removeItem(R.id.ic_rotate_left_24)
            menu.removeItem(R.id.ic_rotate_right_24)
        } else if (cropImageOptions.allowCounterRotation) {
            menu.findItem(R.id.ic_rotate_left_24).isVisible = true
        }

        if (!cropImageOptions.allowFlipping) menu.removeItem(R.id.ic_flip_24)

        if (cropImageOptions.cropMenuCropButtonTitle != null) {
            menu.findItem(R.id.crop_image_menu_crop).title = cropImageOptions.cropMenuCropButtonTitle
        }
        var cropIcon: Drawable? = null
        try {
            if (cropImageOptions.cropMenuCropButtonIcon != 0) {
                cropIcon = ContextCompat.getDrawable(this, cropImageOptions.cropMenuCropButtonIcon)
                menu.findItem(R.id.crop_image_menu_crop).icon = cropIcon
            }
        } catch (e: Exception) {
            Log.w("AIC", "Failed to read menu crop drawable", e)
        }
        if (cropImageOptions.activityMenuIconColor != 0) {
            updateMenuItemIconColor(menu, R.id.ic_rotate_left_24, cropImageOptions.activityMenuIconColor)
            updateMenuItemIconColor(menu, R.id.ic_rotate_right_24, cropImageOptions.activityMenuIconColor)
            updateMenuItemIconColor(menu, R.id.ic_flip_24, cropImageOptions.activityMenuIconColor)

            if (cropIcon != null) {
                updateMenuItemIconColor(
                    menu,
                    R.id.crop_image_menu_crop,
                    cropImageOptions.activityMenuIconColor
                )
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.crop_image_menu_crop -> cropImage()
            R.id.ic_rotate_left_24 -> rotateImage(-cropImageOptions.rotationDegrees)
            R.id.ic_rotate_right_24 -> rotateImage(cropImageOptions.rotationDegrees)
            R.id.ic_flip_24_horizontally -> cropImageView?.flipImageHorizontally()
            R.id.ic_flip_24_vertically -> cropImageView?.flipImageVertically()
            android.R.id.home -> setResultCancel()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResultCancel()
    }

    protected open fun onPickImageResult(resultUri: Uri?) {
        if (resultUri == null) setResultCancel()
        if (resultUri != null) {
            cropImageUri = resultUri
            // For API >= 23 we need to check specifically that we have permissions to read external
            // storage.
            if (cropImageUri?.let {
                CropImage.isReadExternalStoragePermissionsRequired(this, it)
            } == true &&
                CommonVersionCheck.isAtLeastM23()
            ) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                )
            } else {
                // no permissions required or already granted, can start crop image activity
                cropImageView?.setImageUriAsync(cropImageUri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (cropImageUri != null &&
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // required permissions granted, start crop image activity
                cropImageView?.setImageUriAsync(cropImageUri)
            } else {
                Toast
                    .makeText(this, R.string.crop_image_activity_no_permissions, Toast.LENGTH_LONG)
                    .show()
                setResultCancel()
            }
        } else if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            // Irrespective of whether camera permission was given or not, we show the picker
            // The picker will not add the camera intent if permission is not available
            pickImage.launch(pickImageOptions)
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        if (error == null) {
            if (cropImageOptions.initialCropWindowRectangle != null)
                cropImageView?.cropRect = cropImageOptions.initialCropWindowRectangle

            if (cropImageOptions.initialRotation > -1)
                cropImageView?.rotatedDegrees = cropImageOptions.initialRotation
        } else setResult(null, error, 1)
    }

    override fun onCropImageComplete(view: CropImageView, result: CropResult) {
        setResult(result.uriContent, result.error, result.sampleSize)
    }

    /**
     * Execute crop image and save the result tou output uri.
     */
    open fun cropImage() {
        if (cropImageOptions.noOutputImage) setResult(null, null, 1)
        else cropImageView?.croppedImageAsync(
            cropImageOptions.outputCompressFormat,
            cropImageOptions.outputCompressQuality,
            cropImageOptions.outputRequestWidth,
            cropImageOptions.outputRequestHeight,
            cropImageOptions.outputRequestSizeOptions,
            cropImageOptions.customOutputUri,
        )
    }

    /**
     * When extending this activity, please set your own ImageCropView
     */
    open fun setCropImageView(cropImageView: CropImageView) {
        this.cropImageView = cropImageView
    }

    /**
     * Rotate the image in the crop image view.
     */
    open fun rotateImage(degrees: Int) {
        cropImageView?.rotateImage(degrees)
    }

    /**
     * Result with cropped image data or error if failed.
     */
    open fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        setResult(
            error?.let { CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE } ?: RESULT_OK,
            getResultIntent(uri, error, sampleSize)
        )
        finish()
    }

    /**
     * Cancel of cropping activity.
     */
    open fun setResultCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    /**
     * Get intent instance to be used for the result of this activity.
     */
    open fun getResultIntent(uri: Uri?, error: Exception?, sampleSize: Int): Intent {
        val result = CropImage.ActivityResult(
            cropImageView?.imageUri,
            uri,
            error,
            cropImageView?.cropPoints,
            cropImageView?.cropRect,
            cropImageView?.rotatedDegrees ?: 0,
            cropImageView?.wholeImageRect,
            sampleSize
        )
        val intent = Intent()
        intent.putExtras(getIntent())
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result)
        return intent
    }

    /**
     * Update the color of a specific menu item to the given color.
     */
    open fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        val menuItem = menu.findItem(itemId)
        if (menuItem != null) {
            val menuItemIcon = menuItem.icon
            if (menuItemIcon != null) {
                try {
                    menuItemIcon.apply {
                        mutate()
                        colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            color,
                            BlendModeCompat.SRC_ATOP
                        )
                    }
                    menuItem.icon = menuItemIcon
                } catch (e: Exception) {
                    Log.w("AIC", "Failed to update menu item color", e)
                }
            }
        }
    }
}
