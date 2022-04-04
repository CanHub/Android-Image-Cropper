package com.canhub.cropper

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class CropImageIntentChooser(
    private val activity: ComponentActivity,
    private val callback: ResultCallback
) {

    interface ResultCallback {

        fun onSuccess(uri: Uri?)

        fun onCancelled()
    }

    companion object {

        const val GOOGLE_PHOTOS = "com.google.android.apps.photos"
        const val GOOGLE_PHOTOS_GO = "com.google.android.apps.photosgo"
        const val SAMSUNG_GALLERY = "com.sec.android.gallery3d"
        const val ONEPLUS_GALLERY = "com.oneplus.gallery"
        const val MIUI_GALLERY = "com.miui.gallery"
    }

    private var title: String = activity.getString(R.string.pick_image_chooser_title)
    private var priorityIntentList = listOf(
        GOOGLE_PHOTOS,
        GOOGLE_PHOTOS_GO,
        SAMSUNG_GALLERY,
        ONEPLUS_GALLERY,
        MIUI_GALLERY
    )
    private var cameraImgUri: Uri? = null
    private val intentChooser =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityRes ->
            if (activityRes.resultCode == Activity.RESULT_OK) {
                /*
                    Here we don't know whether a gallery app or the camera app is selected
                    via the intent chooser. If a gallery app is selected and an image is
                    chosen then we get the result from activityRes.
                    If a camera app is selected we take the uri we passed to the camera
                    app for storing the captured image
                 */
                (activityRes.data?.data ?: cameraImgUri).let { uri ->
                    callback.onSuccess(uri)
                }
            } else {
                callback.onCancelled()
            }
        }

    /**
     * Create a chooser intent to select the source to get image from.<br></br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br></br>
     * All possible sources are added to the intent chooser.
     *
     * @param includeCamera    if to include camera intents
     * @param includeGallery if to include Gallery app intents
     * @param cameraImgUri required if includeCamera is set to true
     */
    fun showChooserIntent(
        includeCamera: Boolean,
        includeGallery: Boolean,
        cameraImgUri: Uri? = null
    ) {
        this.cameraImgUri = cameraImgUri
        val allIntents: MutableList<Intent> = ArrayList()
        val packageManager = activity.packageManager
        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(activity) && includeCamera) {
            allIntents.addAll(getCameraIntents(activity, packageManager))
        }
        if (includeGallery) {
            var galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT)
            if (galleryIntents.isEmpty()) {
                // if no intents found for get-content try pick intent action (Huawei P9).
                galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK)
            }
            allIntents.addAll(galleryIntents)
        }
        val target = if (allIntents.isEmpty()) Intent() else {
            Intent(Intent.ACTION_CHOOSER, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                if (includeGallery) {
                    action = Intent.ACTION_PICK
                    type = "image/*"
                }
            }
        }
        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(target, title)
        // Add all other intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>()
        )
        intentChooser.launch(chooserIntent)
    }

    /**
     * Get all Camera intents for capturing image using device camera apps.
     */
    private fun getCameraIntents(context: Context, packageManager: PackageManager): List<Intent> {
        val allIntents: MutableList<Intent> = ArrayList()
        // Determine Uri of camera image to  save.
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (resolveInfo in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(
                resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name
            )
            intent.setPackage(resolveInfo.activityInfo.packageName)
            if (context is Activity) {
                context.grantUriPermission(
                    resolveInfo.activityInfo.packageName, cameraImgUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImgUri)
            allIntents.add(intent)
        }
        return allIntents
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle
     * images.
     * Note: It currently get only the main camera app intent. Still have to figure out
     * how to get multiple camera apps to pick from (if available)
     */
    private fun getGalleryIntents(packageManager: PackageManager, action: String): List<Intent> {
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
        // sort intents
        val priorityIntents = mutableListOf<Intent>()
        for (pkgName in priorityIntentList) {
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
            hasCameraPermissionInManifest(context) &&
            context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if the app requests a specific permission in the manifest.
     *
     * @param context the context of your activity to check for permissions
     * @return true - the permission in requested in manifest, false - not.
     */
    private fun hasCameraPermissionInManifest(context: Context): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermissions = packageInfo.requestedPermissions
            return declaredPermissions
                ?.any { it?.equals("android.permission.CAMERA", true) == true } == true
        } catch (e: PackageManager.NameNotFoundException) {
            // Since the package name cannot be found we return false below
            // because this means that the camera permission hasn't been declared
            // by the user for this package so we can't show the camera app among
            // among the list of apps
            e.printStackTrace()
        }
        return false
    }

    /**
     * Set up a list of apps that you require to show first in the intent chooser
     * Apps will show in the order it is passed
     *
     * @param appsList - pass a list of package names of apps of your choice
     *
     * This overrides the existing apps list
     */
    fun setupPriorityAppsList(appsList: List<String>): CropImageIntentChooser = apply {
        priorityIntentList = appsList
    }

    /**
     * Set the title for the intent chooser
     *
     * @param title - the title for the intent chooser
     */
    fun setIntentChooserTitle(title: String): CropImageIntentChooser = apply {
        this.title = title
    }
}
