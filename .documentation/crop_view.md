## Using CropView
[Sample code](https://github.com/CanHub/Android-Image-Cropper/tree/main/sample/src/main/java/com/canhub/cropper/sample/SampleUsingImageView.kt)

1. Add `CropImageView` into your activity
 ```xml
 <!-- Image Cropper fill the remaining available height -->
 <com.canhub.cropper.CropImageView
   android:id="@+id/cropImageView"
   android:layout_width="match_parent"
   android:layout_height="0dp"
   android:layout_weight="1"/>
 ```

2. Set image to crop
 ```kotlin
 cropImageView.setImageUriAsync(uri)
 // or (prefer using uri for performance and better user experience)
 cropImageView.setImageBitmap(bitmap)
 ```

3. Get cropped image
 ```kotlin
 // subscribe to async event using cropImageView.setOnCropImageCompleteListener(listener)
 cropImageView.getCroppedImageAsync()
 // or
 val cropped: Bitmap = cropImageView.getCroppedImage()
 ```
