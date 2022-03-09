package com.canhub.cropper

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.canhub.cropper.CropImageView.CropResult
import com.canhub.cropper.CropImageView.OnCropImageCompleteListener
import com.canhub.cropper.CropImageView.OnSetImageUriCompleteListener
import com.canhub.cropper.databinding.CropImageActivityBinding
import com.canhub.cropper.utils.getUriForFile
import java.io.File

open class CropImageActivity :
    AppCompatActivity(),
    OnSetImageUriCompleteListener,
    OnCropImageCompleteListener {

    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private var cropImageUri: Uri? = null

    /**
     * the options that were set for the crop image
     */
    private lateinit var cropImageOptions: CropImageOptions

    /** The crop image view library widget used in the activity */
    private var cropImageView: CropImageView? = null
    private lateinit var binding: CropImageActivityBinding
    private var latestTmpUri: Uri? = null
    private val pickImageGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onPickImageResult(uri)
        }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) onPickImageResult(latestTmpUri) else onPickImageResult(null)
    }
    private val intentChooser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityRes ->
            if (activityRes.resultCode == Activity.RESULT_OK) {
                (activityRes.data?.data ?: latestTmpUri)?.let { uri ->
                    onPickImageResult(uri)
                }
            } else setResultCancel()
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CropImageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCropImageView(binding.cropImageView)
        val bundle = intent.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
        cropImageUri = bundle?.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE)
        cropImageOptions =
            bundle?.getParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS) ?: CropImageOptions()

        if (savedInstanceState == null) {
            if (cropImageUri == null || cropImageUri == Uri.EMPTY) {
                if (cropImageOptions.showIntentChooser) {
                    val appPicker = getPickImageChooserIntent(
                        this, getString(R.string.pick_image_chooser_title),
                        includeCamera = cropImageOptions.imageSourceIncludeCamera,
                        includeGallery = cropImageOptions.imageSourceIncludeGallery,
                        includeDocuments = false
                    )
                    intentChooser.launch(appPicker)
                } else {
                    when {
                        cropImageOptions.imageSourceIncludeGallery &&
                            cropImageOptions.imageSourceIncludeCamera ->
                            showImageSourceDialog(::openSource)
                        cropImageOptions.imageSourceIncludeGallery ->
                            pickImageGallery.launch("image/*")
                        cropImageOptions.imageSourceIncludeCamera ->
                            openCamera()
                        else -> finish()
                    }
                }
            } else cropImageView?.setImageUriAsync(cropImageUri)
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

    private fun openSource(source: Source) {
        when (source) {
            Source.CAMERA -> openCamera()
            Source.GALLERY -> pickImageGallery.launch("image/*")
        }
    }

    private fun openCamera() {
        getTmpFileUri().let { uri ->
            latestTmpUri = uri
            takePicture.launch(uri)
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return getUriForFile(this, tmpFile)
    }

    /**
     * This method show the dialog for user source choice, it is an open function so can be override
     * and customised with the app layout if need.
     */
    open fun showImageSourceDialog(openSource: (Source) -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(R.string.pick_image_chooser_title)
            .setItems(
                arrayOf(
                    getString(R.string.pick_image_camera),
                    getString(R.string.pick_image_gallery),
                )
            ) { _, position -> openSource(if (position == 0) Source.CAMERA else Source.GALLERY) }
            .show()
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
        if (cropImageOptions.skipEditing) return true
        menuInflater.inflate(R.menu.crop_image_menu, menu)

        if (!cropImageOptions.allowRotation) {
            menu.removeItem(R.id.ic_rotate_left_24)
            menu.removeItem(R.id.ic_rotate_right_24)
        } else if (cropImageOptions.allowCounterRotation) {
            menu.findItem(R.id.ic_rotate_left_24).isVisible = true
        }

        if (!cropImageOptions.allowFlipping) menu.removeItem(R.id.ic_flip_24)

        if (cropImageOptions.cropMenuCropButtonTitle != null) {
            menu.findItem(R.id.crop_image_menu_crop).title =
                cropImageOptions.cropMenuCropButtonTitle
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
            updateMenuItemIconColor(
                menu,
                R.id.ic_rotate_left_24,
                cropImageOptions.activityMenuIconColor
            )
            updateMenuItemIconColor(
                menu,
                R.id.ic_rotate_right_24,
                cropImageOptions.activityMenuIconColor
            )
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
        when (resultUri) {
            null -> setResultCancel()
            else -> {
                cropImageUri = resultUri
                cropImageView?.setImageUriAsync(cropImageUri)
            }
        }
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        if (error == null) {
            if (cropImageOptions.initialCropWindowRectangle != null)
                cropImageView?.cropRect = cropImageOptions.initialCropWindowRectangle

            if (cropImageOptions.initialRotation > 0)
                cropImageView?.rotatedDegrees = cropImageOptions.initialRotation

            if (cropImageOptions.skipEditing) {
                cropImage()
            }
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
            saveCompressFormat = cropImageOptions.outputCompressFormat,
            saveCompressQuality = cropImageOptions.outputCompressQuality,
            reqWidth = cropImageOptions.outputRequestWidth,
            reqHeight = cropImageOptions.outputRequestHeight,
            options = cropImageOptions.outputRequestSizeOptions,
            customOutputUri = cropImageOptions.customOutputUri,
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

    enum class Source { CAMERA, GALLERY }

    /**
     * Create a chooser intent to select the source to get image from.<br></br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br></br>
     * All possible sources are added to the intent chooser.
     *
     * @param context          used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param title            the title to use for the chooser UI
     * @param includeCamera    if to include camera intents
     * @param includeGallery if to include Gallery app intents
     * @param includeDocuments if to include KitKat documents activity containing all sources
     */
    private fun getPickImageChooserIntent(
        context: Context,
        title: CharSequence?,
        includeCamera: Boolean,
        includeGallery: Boolean,
        includeDocuments: Boolean
    ): Intent {
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager = context.packageManager
        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context, packageManager))
        }
        if (includeGallery) {
            var galleryIntents =
                getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT, includeDocuments)
            if (galleryIntents.isEmpty()) {
                // if no intents found for get-content try pick intent action (Huawei P9).
                galleryIntents =
                    getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments)
            }
            allIntents.addAll(galleryIntents)
        }
        val target: Intent
        if (allIntents.isEmpty()) {
            target = Intent()
        } else {
            target = Intent(Intent.ACTION_CHOOSER, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (includeGallery) {
                target.action = Intent.ACTION_PICK
                target.type = "image/*"
            }
        }
        // Create a chooser from the main  intent
        val chooserIntent = Intent.createChooser(target, title)
        // Add all other intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>()
        )
        return chooserIntent
    }

    /**
     * Get all Camera intents for capturing image using device camera apps.
     */
    private fun getCameraIntents(context: Context, packageManager: PackageManager): List<Intent> {
        val allIntents: MutableList<Intent> = ArrayList()
        // Determine Uri of camera image to  save.
        val outputFileUri = getTmpFileUri()
        latestTmpUri = outputFileUri
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (context is Activity) {
                context.grantUriPermission(
                    res.activityInfo.packageName, outputFileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            allIntents.add(intent)
        }
        return allIntents
    }

    private val famousGalleryPackages = listOf(
        "com.google.android.apps.photos", // Google Photos
        "com.google.android.apps.photosgo", // Google Photos Gallery Go
        "com.sec.android.gallery3d", // Samsung Gallery
        "com.oneplus.gallery", // One Plus Gallery
        "com.miui.gallery", // MIUI Gallery
    )

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle
     * images.
     * Note: It currently get only the main camera app intent. Still have to figure out
     * how to get multiple camera apps to pick from (if available)
     */
    private fun getGalleryIntents(
        packageManager: PackageManager,
        action: String,
        includeDocuments: Boolean
    ): List<Intent> {
        val intents: MutableList<Intent> = ArrayList()
        val galleryIntent = if (action == Intent.ACTION_GET_CONTENT) Intent(action)
        else Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intents.add(intent)
        }
        // remove documents intent
        if (!includeDocuments) {
            for (intent in intents) {
                if (intent.component?.className == "com.android.documentsui.DocumentsActivity") {
                    intents.remove(intent)
                    break
                }
            }
        }
        // sort intents
        val priorityIntents = mutableListOf<Intent>()
        for (pkgName in famousGalleryPackages) {
            intents.firstOrNull { it.`package` == pkgName }?.let {
                intents.remove(it)
                priorityIntents.add(it)
            }
        }
        intents.addAll(0, priorityIntents)
        return intents
    }

    /**
     * Check if explicetly requesting camera permission is required.<br></br>
     * It is required in Android Marshmellow and above if "CAMERA" permission is requested in the
     * manifest.<br></br>
     * See [StackOverflow
     * question](http://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug).
     */
    private fun isExplicitCameraPermissionRequired(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            hasPermissionInManifest(context, "android.permission.CAMERA") &&
            context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if the app requests a specific permission in the manifest.
     *
     * @param permissionName the permission to check
     * @return true - the permission in requested in manifest, false - not.
     */
    private fun hasPermissionInManifest(context: Context, permissionName: String): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermisisons = packageInfo.requestedPermissions
            if (declaredPermisisons?.any { it?.equals(permissionName, true) == true } == true) {
                return true
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }
}
