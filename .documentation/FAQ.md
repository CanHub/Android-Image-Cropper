# Frequently Asked Questions

## Can't see the crop button. So, can't crop the image.
Does your app theme has no action bar? If yes, you should add this to your manifest
```kt
<activity android:name="com.canhub.cropper.CropImageActivity"
  android:theme="@style/Base.Theme.AppCompat"/>
```

## Downloading the Cropped image to a file
**Ref:** [[BUG] - Uri Received lacks 'file' scheme](https://github.com/CanHub/Android-Image-Cropper/issues/368)

If you need to use the cropped Image as a file e.g. send it as a file to back-end server
You need to re-build the Uri as follow

**With Android/jetpack compose**
```kotlin
val imageCropLauncher = rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
    if (result.isSuccessful) {
        // use the cropped image
        resultImageUri = result.uriContent
        imageFilePath = result.getUriFilePath(context = context, uniqueName = true).toString()
        if (resultImageUri?.scheme?.contains("content") == true) {
            // replace Scheme to file
            val builder = Uri.Builder()
            builder.scheme("file")
                .appendPath(imageFilePath)
            val imageUri = builder.build()
            // Use the imageUri then to send the file
        }
    } else {
        // an error occurred cropping
        val exception = result.error
        Log.e(GLOBAL_TAG, "CropImageContract Exception $exception")
    }
}
```
**Convert the Uri to File**
```kotlin
val file: File = imageUri.toFile()
```
**With Retrofit/Okhttp3 you can now send it**
```kotlin
val response = postApi.createPost(
    postData = MultipartBody.Part
        .createFormData(
            "post_data",
            gson.toJson(request)
        ),
    postImage = MultipartBody.Part
        .createFormData(
            name = "post_image",
            filename = file.name,
            body = file.asRequestBody()
        )
)
```
**Note:**
be sure to test for Errors/Exceptions along the way!
Tested on Android Studio Chipmunk | 2021.2.1
Android API 31 Platform
android_image_cropper_version = "4.2.1"

## App is crashing when trying to start the library using `CropImageContract()` due to missing CAMERA permission
Basically the library does not need the CAMERA permission to work. It uses the `ActivityResultContracts.TakePicture()` contract which does not require the permission to take a single picture. There is one single drawback utilizing this way:

When you have `<uses-permission android:name="android.permission.CAMERA" />` in your `AndroidManifest.xml` because some other feature in your app needs it (and you can't use the `TakePicture()` contract) and you don't handle the permission request at runtime yourself, you will experience a crash like the following one:

`java.lang.SecurityException: Permission Denial: starting Intent { act=android.media.action.IMAGE_CAPTURE flg=0x3 cmp=com.android.camera2/com.android.camera.CaptureActivity clip={text/uri-list hasLabel(0) {U(content)}} (has extras) } from ProcessRecord{5163a5b 17423:<package>/u0a253} (pid=17423, uid=10253) with revoked permission android.permission.CAMERA`

This behaviour is also described in the official [Android Documentation](https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE) and discussed in the following issue: [[BUG] - Crash when camera permission is present in manifest #417](https://github.com/CanHub/Android-Image-Cropper/issues/417).

**Solution:** Make sure to check for granted CAMERA runtime permission before trying to start the image cropper library or remove the permission from the `AndroidManifest.xml` and make sure the rest of your app does not need it.
