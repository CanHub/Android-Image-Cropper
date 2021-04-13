[![CanHub](https://github.com/CanHub/Android-Image-Cropper/blob/main/art/canhub_logo_horizontal_transparent.png?raw=true)](https://github.com/canhub)

[![](https://jitpack.io/v/CanHub/Android-Image-Cropper.svg)](https://jitpack.io/#CanHub/Android-Image-Cropper)

[🚨How to migrate from ArthurHub/Android-Image-Cropper🚨](https://github.com/CanHub/Android-Image-Cropper/wiki/🚨-How-to-migrate-Android-Image-Cropper--🚨)

Wanna help the project? Amazing!
- [Contributing Guide](https://github.com/CanHub/Android-Image-Cropper/blob/main/CONTRIBUTING.md)

Android Image Cropper
=======
**Powerful** (Zoom, Rotation, Multi-Source);
**Customizable** (Shape, Limits, Style);
**Optimized** (Async, Sampling, Matrix);
**Simple** image cropping library for Android.

![Crop](https://github.com/CanHub/Android-Image-Cropper/blob/main/art/demo.gif?raw=true)

## Add to your project

[See GitHub Wiki for more info.](https://github.com/CanHub/Android-Image-Cropper/wiki)

#### Step 1. Add the JitPack repository to your root build.gradle

```gradle
  allprojects {
     repositories {
       ....
       maven { url 'https://jitpack.io' }
     }
  }
```

#### Step 2. Add the dependency

```gradle
  dependencies {
       implementation 'com.github.CanHub:Android-Image-Cropper:${version}'
  }
```
[Latest Release Version](https://github.com/CanHub/Android-Image-Cropper/releases)

#### Step 3. Add permissions to manifest 

Only need if you run on devices under OS10 (SDK 29)

 ```xml
<manifest>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />
</manifest>
 ```

 #### Step 4. Add this line to your Proguard config file

```
-keep class androidx.appcompat.widget.** { *; }
```

#### Step 5. Set source compatibility version to Java 8

- Go to app level `build.gradle` file

- Add this line inside ```android``` in build.gradle
	```gradle
	compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
	```
	
- This will set the java version to 8

## Using Activity

### Extend to make a custom activity
If you want to extend the `CropImageActivity` please be aware you will need to setup your `CropImageView`
You can check a sample code in this project `com.canhub.cropper.sample.extend_activity.app.ExtendActivity`

- Add `CropImageActivity` into your AndroidManifest.xml
 ```xml
 <activity android:name="com.canhub.cropper.CropImageActivity"
   android:theme="@style/Base.Theme.AppCompat"/> <!-- optional (needed if default theme has no action bar) -->
 ```
- Setup your `CropImageView` after call `super.onCreate(savedInstanceState)`
 ```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setCropImageView(binding.cropImageView)
}
 ```

### Start the default Activity
- Start `CropImageActivity` using builder pattern from your activity
 ```kotlin
class MainActivity {
    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage
            .activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)

        // start cropping activity for pre-acquired image saved on the device
        CropImage
            .activity(imageUri)
            .start(this)

        // for fragment (DO NOT use `getActivity()`)
        CropImage
            .activity()
            .start(requireContext(), this)
    }
}
 ```

- Override `onActivityResult` method in your activity to get crop result
 ```kotlin
class MainActivity {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
             val result = CropImage.getActivityResult(data)
             if (resultCode == Activity.RESULT_OK) {
                 val resultUri: Uri? = result?.uriContent
                 val resultFilePath: String? = result?.getFilePath(requireContext())
             } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                 val error = result!!.error
             }
         }
     }
}
 ```

## Using View
2. Add `CropImageView` into your activity
 ```xml
 <!-- Image Cropper fill the remaining available height -->
 <com.canhub.cropper.CropImageView
   android:id="@+id/cropImageView"
   android:layout_width="match_parent"
   android:layout_height="0dp"
   android:layout_weight="1"/>
 ```

3. Set image to crop
 ```kotlin
 cropImageView.setImageUriAsync(uri)
 // or (prefer using uri for performance and better user experience)
 cropImageView.setImageBitmap(bitmap)
 ```

4. Get cropped image
 ```kotlin
 // subscribe to async event using cropImageView.setOnCropImageCompleteListener(listener)
 cropImageView.getCroppedImageAsync()
 // or
 val cropped: Bitmap = cropImageView.getCroppedImage()
 ```

## Features
- Built-in `CropImageActivity`.
- Set cropping image as Bitmap, Resource or Android URI (Gallery, Camera, Dropbox, etc.).
- Image rotation/flipping during cropping.
- Auto zoom-in/out to relevant cropping area.
- Auto rotate bitmap by image Exif data.
- Set result image min/max limits in pixels.
- Set initial crop window size/location.
- Request cropped image resize to specific size.
- Bitmap memory optimization, OOM handling (should never occur)!
- API Level 14.
- More..

## Customizations
- Cropping window shape: Rectangular, Oval (square/circle by fixing aspect ratio), as well as
 rectangular modes which only allow vertical or horizontal cropping.
- Cropping window aspect ratio: Free, 1:1, 4:3, 16:9 or Custom.
- Guidelines appearance: Off / Always On / Show on Touch.
- Cropping window Border line, border corner and guidelines thickness and color.
- Cropping background color.

For more information, see the [GitHub Wiki](https://github.com/CanHub/Android-Image-Cropper/wiki).

## Posts
 - [Android cropping image from camera or gallery](http://theartofdev.com/2015/02/15/android-cropping-image-from-camera-or-gallery/)
 - [Android Image Cropper async support and custom progress UI](http://theartofdev.com/2016/01/15/android-image-cropper-async-support-and-custom-progress-ui/)
 - [Adding auto-zoom feature to Android-Image-Cropper](https://theartofdev.com/2016/04/25/adding-auto-zoom-feature-to-android-image-cropper/)

## License
Forked from [ArthurHub](https://github.com/ArthurHub/Android-Image-Cropper)
Originally forked from [edmodo/cropper](https://github.com/edmodo/cropper).

Copyright 2016, Arthur Teplitzki, 2013, Edmodo, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the   License.
You may obtain a copy of the License in the LICENSE file, or at:

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS   IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
