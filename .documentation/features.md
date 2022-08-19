# Features

## Optional initial URI
Crop library can be called with or without image uri
```kt
cropImage.launch(
    options(outputUri) { ... }
)
//OR
cropImage.launch(
    options { ... }
)
```

## Image source
```kt
cropImage.launch(
    options { 
      setImageSource(
        includeGallery = true,
        includeCamera = true,
      )
    }
)
```
 
### List of options
```kt
cropImage.launch(
    options { 
      setScaleType(CropImageView.ScaleType.FIT_CENTER)
      setCropShape(CropImageView.CropShape.RECTANGLE)
      setGuidelines(CropImageView.Guidelines.ON_TOUCH)
      setAspectRatio(1, 1)
      setMaxZoom(4)
      setAutoZoomEnabled(true)
      setMultiTouchEnabled(true)
      setCenterMoveEnabled(true)
      setShowCropOverlay(true)
      setAllowFlipping(true)
      setSnapRadius(3f)
      setTouchRadius(48f)
      setInitialCropWindowPaddingRatio(0.1f)
      setBorderLineThickness(3f)
      setBorderLineColor(Color.argb(170, 255, 255, 255))
      setBorderCornerThickness(2f)
      setBorderCornerOffset(5f)
      setBorderCornerLength(14f)
      setBorderCornerColor(WHITE)
      setGuidelinesThickness(1f)
      setGuidelinesColor(R.color.white)
      setBackgroundColor(Color.argb(119, 0, 0, 0))
      setMinCropWindowSize(24, 24)
      setMinCropResultSize(20, 20)
      setMaxCropResultSize(99999, 99999)
      setActivityTitle("")
      setActivityMenuIconColor(0)
      setOutputUri(outputUri)
      setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
      setOutputCompressQuality(90)
      setRequestedSize(0, 0)
      setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
      setInitialCropWindowRectangle(null)
      setInitialRotation(0)
      setAllowCounterRotation(false)
      setFlipHorizontally(false)
      setFlipVertically(false)
      setCropMenuCropButtonTitle(null)
      setCropMenuCropButtonIcon(0)
      setAllowRotation(true)
      setNoOutputImage(false)
      setFixAspectRatio(false)
      setIntentChooserPriorityList(listOf("com.miui.gallery", "com.google.android.apps.photos"))
      setActivityBackgroundColor(Color.BLACK)
      setToolbarColor(Color.GRAY)
    }
)
```