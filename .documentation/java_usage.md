- [Java Sample Code](https://github.com/CanHub/Android-Image-Cropper/tree/main/sample/src/main/java/com/canhub/cropper/sample/SampleCropJava.java)

Still stuck? Try This Discussion here to a **Plug and Play** Solution to get you started.

- [🚨 Calling an Activity Directly To Handle this using Java](https://github.com/CanHub/Android-Image-Cropper/discussions/236)

So here are the steps to make the migration work for verison [3.3.6](https://github.com/CanHub/Android-Image-Cropper/releases/tag/3.3.6) or below.

Follow this guide first [🚨 How to migrate Android Image Cropper 🚨](https://github.com/CanHub/Android-Image-Cropper/wiki/%F0%9F%9A%A8-How-to-migrate-Android-Image-Cropper--%F0%9F%9A%A8)

Then follow the following strps to make it work.

Note: You need to use a single kotlin file and also enable kotlin compiler for your project to make it work.

### **Enable Compliler**

goto Tools>Kotlin>Configure kotlin in this project and gradle sync the project

![2022-04-18_01-53-44](https://user-images.githubusercontent.com/49026890/163731038-63a524c4-8123-4d35-8318-c1c44b8ccb9f.jpg)

### **Add Kotlin Code**

Now create a kotlin file _GetUri.kt_

add following code `fun CropImage.ActivityResult?.getUri(): Uri? { return this?.uriContent }`
![2022-04-20_01-21-42](https://user-images.githubusercontent.com/49026890/164084483-bd66744c-4b92-4d2d-9c04-8f288e53de19.jpg)

### **Get Result**

then in main `onActivityResult()`

```
 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = GetUriKt.getUri(CropImage.getActivityResult(data));
                Log.e(TAG, "onActivityResult: "+resultUri.toString());
            }
        }
    }
```
![2022-04-20_01-22-59](https://user-images.githubusercontent.com/49026890/164084758-b0d49e72-d198-4575-912d-971e52979d09.jpg)
