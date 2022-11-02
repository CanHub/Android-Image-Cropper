[![CanHub](.documentation/art/cover.png?raw=true)](https://github.com/canhub)

Android Image Cropper
=====================

- **Powerful** (Zoom, Rotation, Multi-Source)
- **Customizable** (Shape, Limits, Style)
- **Optimized** (Async, Sampling, Matrix)
- **Simple** image cropping library for Android

![Crop demo](.documentation/art/showcase-1.gif?raw=true)

## Add to your project

```groovy
dependencies {
  implementation("com.vanniktech:android-image-cropper:4.5.0")
}
```

## Using the Library

There are 3 ways of using the library. Check out the sample app for all details.

### [1. Calling crop directly](./sample/src/main/kotlin/com/canhub/cropper/sample/SampleCrop.kt)

```kotlin
class MainActivity {
  private val cropImage = registerForActivityResult(CropImageContract()) { result ->
    if (result.isSuccessful) {
      // Use the returned uri.
      val uriContent = result.uriContent
      val uriFilePath = result.getUriFilePath(context) // optional usage
    } else {
      // An error occurred.
      val exception = result.error
    }
  }

  private fun startCrop() {
    // Start picker to get image for cropping and then use the image in cropping activity.
    cropImage.launch(
      options {
        setGuidelines(Guidelines.ON)
      }
    )

    // Start picker to get image for cropping from only gallery and then use the image in cropping activity.
    cropImage.launch(
      options {
        setImagePickerContractOptions(
          PickImageContractOptions(includeGallery = true, includeCamera = false)
        )
      }
    )

    // Start cropping activity for pre-acquired image saved on the device and customize settings.
    cropImage.launch(
      options(uri = imageUri) {
        setGuidelines(Guidelines.ON)
        setOutputCompressFormat(CompressFormat.PNG)
      }
    )
  }
}
```

### [2. Using CropView](./sample/src/main/kotlin/com/canhub/cropper/sample/SampleUsingImageView.kt)

- Add `CropImageView` into your activity

```xml
<!-- Image Cropper fill the remaining available height -->
<com.canhub.cropper.CropImageView
  android:id="@+id/cropImageView"
  android:layout_width="match_parent"
  android:layout_height="0dp"
  android:layout_weight="1"
  />
```

- Set image to crop

```kotlin
cropImageView.setImageUriAsync(uri)
// Or prefer using uri for performance and better user experience.
cropImageView.setImageBitmap(bitmap)
```

- Get cropped image

```kotlin
// Subscribe to async event using cropImageView.setOnCropImageCompleteListener(listener)
cropImageView.getCroppedImageAsync()
// Or.
val cropped: Bitmap = cropImageView.getCroppedImage()
```

### [3. Extend to make a custom activity](./sample/src/main/kotlin/com/canhub/cropper/sample/SampleCustomActivity.kt)

If you want to extend the `CropImageActivity` please be aware you will need to set up your `CropImageView`

- Add `CropImageActivity` into your AndroidManifest.xml
```xml
<!-- Theme is optional and only needed if default theme has no action bar. -->
<activity
  android:name="com.canhub.cropper.CropImageActivity"
  android:theme="@style/Base.Theme.AppCompat"
  />
```

- Set up your `CropImageView` after call `super.onCreate(savedInstanceState)`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setCropImageView(binding.cropImageView)
}
```

#### Custom dialog for image source pick

When calling crop directly the library will prompt a dialog for the user choose between gallery or camera (If you keep both enable).
We use the Android default AlertDialog for this. If you wanna customised it with your app theme you need to override the method `showImageSourceDialog(..)` when extending the activity _(above)_

```kotlin
override fun showImageSourceDialog(openSource: (Source) -> Unit) {
  super.showImageSourceDialog(openCamera)
}
```

## Posts

 - [Android cropping image from camera or gallery](https://canato.medium.com/android-cropping-image-from-camera-or-gallery-fbe732800b08)

## Migrating from Android Image Cropper

Start by using [Version 4.3.3](https://github.com/CanHub/Android-Image-Cropper/releases/tag/4.3.3):

```groovy
dependencies {
  implementation("com.vanniktech:android-image-cropper:4.3.3")
}
```

### Update all imports

```diff
-import com.theartofdev.edmodo.cropper.CropImage
-import com.theartofdev.edmodo.cropper.CropImageActivity
+import com.canhub.cropper.CropImage
+import com.canhub.cropper.CropImageActivity
```

### Update all XML references

```diff
-<com.theartofdev.edmodo.cropper.CropImageView
+<com.canhub.cropper.CropImageView
```

When using Activity Contracts, consult with the sample app on how to use our Activity Contracts since `onActivityResult` got deprecated.

Versions after 4.3.3 have changed the APIs quite a bit, it's best to upgrade to each minor version individually, remove deprecated API usages and continue upgrading. So after using 4.3.3, upgrade to 4.4.0, etc.

## License

Forked from [ArthurHub](https://github.com/ArthurHub/Android-Image-Cropper)
Originally forked from [edmodo/cropper](https://github.com/edmodo/cropper).

Copyright 2016, Arthur Teplitzki, 2013, Edmodo, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the   License.
You may obtain a copy of the License in the LICENSE file, or at:

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS   IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
