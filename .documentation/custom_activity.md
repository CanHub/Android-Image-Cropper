## Extend to make a custom activity
[Sample code](https://github.com/CanHub/Android-Image-Cropper/tree/main/sample/src/main/java/com/canhub/cropper/sample/SampleCustomActivity.kt)

If you want to extend the `CropImageActivity` please be aware you will need to setup your `CropImageView`

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

### Custom dialog for image source pick
When calling crop directly the library will prompt a dialog for the user choose between gallery or camera (If you keep both enable).
We use the Android default AlertDialog for this. If you wanna customised it with your app theme you need to override the method `showImageSourceDialog(..)` when extending the activity _(above)_
```kotlin 
override fun showImageSourceDialog(openSource: (Source) -> Unit) {
     super.showImageSourceDialog(openCamera)
}
```