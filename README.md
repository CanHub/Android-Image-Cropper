[![CanHub](https://github.com/CanHub/Android-Image-Cropper/blob/main/.documentation/art/canhub_logo_horizontal_transparent.png?raw=true)](https://github.com/canhub)

[![](https://jitpack.io/v/CanHub/Android-Image-Cropper.svg)](https://jitpack.io/#CanHub/Android-Image-Cropper)

☕ [Using the library in Java](https://github.com/CanHub/Android-Image-Cropper/blob/main/.documentation/java_usage.md)

Android Image Cropper
=======
- **Powerful** (Zoom, Rotation, Multi-Source);
- **Customizable** (Shape, Limits, Style);
- **Optimized** (Async, Sampling, Matrix);
- **Simple** image cropping library for Android.
[Features List](https://github.com/CanHub/Android-Image-Cropper/blob/main/.documentation/features.md)

![Crop](https://github.com/CanHub/Android-Image-Cropper/blob/main/.documentation/art/demo.gif?raw=true)

# Add to your project
### Step 1. Add the JitPack repository to your root build.gradle
```gradle
  allprojects {
     repositories {
       ....
       maven { url 'https://jitpack.io' }
     }
  }
```

### Step 2. Add the dependency
```gradle
  dependencies {
       implementation 'com.github.CanHub:Android-Image-Cropper:${version}'
  }
```
[Latest Release Version](https://github.com/CanHub/Android-Image-Cropper/releases)

### Step 3. Add permissions to manifest
Only need if you run on devices under OS10 (SDK 29)
 ```xml
<manifest>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />
</manifest>
 ```

### Step 4. Set source compatibility version to Java 11
- The library is up to date with the latest releases, if you are not using Java 11 yet please check the release page for previous working versions.
- Go to app level `build.gradle` file
- Add this line inside ```android``` in build.gradle
	```gradle
	compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
 
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
	```
 - This expects Gradle 7.0+

# Using the Library
There is 3 ways of using the library:
- Calling crop directly [Below]
- [Using the CropView](https://github.com/CanHub/Android-Image-Cropper/blob/main/.documentation/crop_view.md)
- [Extending the activity](https://github.com/CanHub/Android-Image-Cropper/blob/main/.documentation/custom_activity.md)
Your choice depends on how you want your layout to look.

## Calling crop directly
[Sample code](https://github.com/CanHub/Android-Image-Cropper/tree/main/sample/src/main/java/com/canhub/cropper/sample/SampleCrop)

- Register for activity result with `CropImageContract`
 ```kotlin
class MainActivity {
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // use the returned uri
                val uriContent = result.uriContent 
                val uriFilePath = result.getUriFilePath(context) // optional usage
            } else {
                // an error occurred
                val exception = result.error
            }
        }

    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(Guidelines.ON)
            }
        )

        //start picker to get image for cropping from only gallery and then use the image in
        //cropping activity
        cropImage.launch(
            options {
                setImagePickerContractOptions(
                    PickImageContractOptions(includeGallery = true, includeCamera = false)
                )
            }
        )

        // start cropping activity for pre-acquired image saved on the device and customize settings
        cropImage.launch(
            options(uri = imageUri) {
                setGuidelines(Guidelines.ON)
                setOutputCompressFormat(CompressFormat.PNG)
            }
        )
    }
}
 ```

## Posts
 - [Android cropping image from camera or gallery](http://theartofdev.com/2015/02/15/android-cropping-image-from-camera-or-gallery/)
 - [Android Image Cropper async support and custom progress UI](http://theartofdev.com/2016/01/15/android-image-cropper-async-support-and-custom-progress-ui/)
 - [Adding auto-zoom feature to Android-Image-Cropper](https://theartofdev.com/2016/04/25/adding-auto-zoom-feature-to-android-image-cropper/)

## Wanna help the project? Amazing!
- [Contributing Guide](https://github.com/CanHub/Android-Image-Cropper/blob/main/CONTRIBUTING.md)

## License
Forked from [ArthurHub](https://github.com/ArthurHub/Android-Image-Cropper)
Originally forked from [edmodo/cropper](https://github.com/edmodo/cropper).

Copyright 2016, Arthur Teplitzki, 2013, Edmodo, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the   License.
You may obtain a copy of the License in the LICENSE file, or at:

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS   IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
