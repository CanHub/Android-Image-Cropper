Version 4.5.0 *(2022-11-02)*
----------------------------

- README: Slightly improve the Migration section. [\#520](https://github.com/CanHub/Android-Image-Cropper/pull/520) ([vanniktech](https://github.com/vanniktech))
- API: Deprecate CropImageView.croppedImage [\#519](https://github.com/CanHub/Android-Image-Cropper/pull/519) ([vanniktech](https://github.com/vanniktech))
- API: Deprecate CropImageView.isSaveBitmapToInstanceState [\#518](https://github.com/CanHub/Android-Image-Cropper/pull/518) ([vanniktech](https://github.com/vanniktech))
- API: Remove deprecated methods from Version 4.4.0 in CropImageContractOption. [\#513](https://github.com/CanHub/Android-Image-Cropper/pull/513) ([vanniktech](https://github.com/vanniktech))
- API: Remove deprecated methods from Version 4.4.0 in CropImage. [\#512](https://github.com/CanHub/Android-Image-Cropper/pull/512) ([vanniktech](https://github.com/vanniktech))
- Behavior Change: Always try to return resized bitmap in CropResult. [\#509](https://github.com/CanHub/Android-Image-Cropper/pull/509) ([vanniktech](https://github.com/vanniktech))
- Behavior Change: The crop area for CropShapes \(Rectangle, Rectangle vertical only, Rectangle horizontal only\) can now also be adjusted by dragging outside the Crop Area just like it's always been possible for CropShape.OVAL. [\#508](https://github.com/CanHub/Android-Image-Cropper/pull/508) ([vanniktech](https://github.com/vanniktech))

Version 4.4.0 *(2022-10-24)*
----------------------------

- API: CropImageOptions is now a data class. Currently with mutable properties, this will change to immutable soon. [\#490](https://github.com/CanHub/Android-Image-Cropper/pull/490) ([vanniktech](https://github.com/vanniktech))
- API: CropImageView gets a setImageCropOptions method. [\#500](https://github.com/CanHub/Android-Image-Cropper/pull/500) ([vanniktech](https://github.com/vanniktech))
- API: Deprecate CropImageContractOptions functions to modify CropImageOptions instance. [\#492](https://github.com/CanHub/Android-Image-Cropper/pull/492) ([vanniktech](https://github.com/vanniktech))
- API: Make BitmapCroppingWorkerJob internal. [\#478](https://github.com/CanHub/Android-Image-Cropper/pull/478) ([vanniktech](https://github.com/vanniktech))
- API: Make BitmapLoadingWorkerJob internal. [\#465](https://github.com/CanHub/Android-Image-Cropper/pull/465) ([vanniktech](https://github.com/vanniktech))
- API: Make CropException inner objects internal. [\#470](https://github.com/CanHub/Android-Image-Cropper/pull/470) ([vanniktech](https://github.com/vanniktech))
- API: Make CropImageIntentChooser internal. [\#485](https://github.com/CanHub/Android-Image-Cropper/pull/485) ([vanniktech](https://github.com/vanniktech))
- API: Make CropImageOptions class final. [\#487](https://github.com/CanHub/Android-Image-Cropper/pull/487) ([vanniktech](https://github.com/vanniktech))
- API: Make CropOverlayView internal. [\#468](https://github.com/CanHub/Android-Image-Cropper/pull/468) ([vanniktech](https://github.com/vanniktech))
- API: Make CropWindowMoveHandler & CropWindowHandler internal. [\#467](https://github.com/CanHub/Android-Image-Cropper/pull/467) ([vanniktech](https://github.com/vanniktech))
- Behavior Change: By default always show the guidelines. [\#494](https://github.com/CanHub/Android-Image-Cropper/pull/494) ([vanniktech](https://github.com/vanniktech))
- Behavior change: By default show crop window on the entire image. [\#474](https://github.com/CanHub/Android-Image-Cropper/pull/474) ([vanniktech](https://github.com/vanniktech))
- Deprecate: CropImage\#get\* functions are now deprecated as they seem to be unused. [\#469](https://github.com/CanHub/Android-Image-Cropper/pull/469) ([vanniktech](https://github.com/vanniktech))
- New API: CropImageView\#expectedImageSize which returns the expected image size, if cropping the image right now. [\#496](https://github.com/CanHub/Android-Image-Cropper/pull/496) ([vanniktech](https://github.com/vanniktech))
- Raise minSdk from 16 to 21. [\#456](https://github.com/CanHub/Android-Image-Cropper/pull/456) ([vanniktech](https://github.com/vanniktech))
- Sample: Enable StrictMode. [\#464](https://github.com/CanHub/Android-Image-Cropper/pull/464) ([vanniktech](https://github.com/vanniktech))
- Sample: Showcase expectedImageSize in CustomActivity. [\#503](https://github.com/CanHub/Android-Image-Cropper/pull/503) ([vanniktech](https://github.com/vanniktech))
- Strict Mode: Fix UnsafeIntentLaunchViolation when using CropImageActivity. [\#502](https://github.com/CanHub/Android-Image-Cropper/pull/502) ([vanniktech](https://github.com/vanniktech))

In addition, this release contains a lot of documentation fixes and refactorings.

Version 4.3.3 *(2022-10-19)*
----------------------------

⚠️ Maven Coordinates have changed ⚠️

This library will no longer be published to Jitpack. The Maven Coordinates have changed, and the library is now on Maven Central. In order to consume the new update please change:

```diff
-implementation("com.github.CanHub:Android-Image-Cropper:4.3.2")
+implementation("com.vanniktech:android-image-cropper:4.3.3")
```

For now, everything else stays the same. [vanniktech](https://github.com/vanniktech) has taken over the maintenance of this library.

- Kotlin Build Script, proper Maven Publishing, new workflows, Gradle Catalogue & much more [\#450](https://github.com/CanHub/Android-Image-Cropper/pull/450) ([vanniktech](https://github.com/vanniktech))
- Correctly close resources in BitmapUtils. [\#440](https://github.com/CanHub/Android-Image-Cropper/pull/440) ([vanniktech](https://github.com/vanniktech))
- Bugfix/437 setting toolbar color to white does nothing [\#438](https://github.com/CanHub/Android-Image-Cropper/pull/438) ([Devenom1](https://github.com/Devenom1))

## [4.3.2] - 08/09/2022
### Fixed
- Fixed the mistake in hindi conversion of "Crop" [#402](https://github.com/CanHub/Android-Image-Cropper/issues/402)
- Added the option to set custom color to toolbar of CropImageActivity [#421](https://github.com/CanHub/Android-Image-Cropper/issues/421)
- Added the option to set custom background color to activity of CropImageActivity [#421](https://github.com/CanHub/Android-Image-Cropper/issues/421)
- Fixed accidentally swiping back on newer Android devices when trying to resize the crop window [#423](https://github.com/CanHub/Android-Image-Cropper/issues/423)
- Fixed an issue on sample project where back button would not work when dialog is shown [#427](https://github.com/CanHub/Android-Image-Cropper/issues/427)
- Fixed an issue on sample project where cancelling/going back would go to a screen with empty image [#427](https://github.com/CanHub/Android-Image-Cropper/issues/427)

## [4.3.1] - 20/07/2022
### Fix
- CropImageView: Added support for handling all EXIF orientation values. [#408](https://github.com/CanHub/Android-Image-Cropper/issues/408)
- CropImageView: Use customOutputUri instance property as a fallback in startCropWorkerTask. [#401](https://github.com/CanHub/Android-Image-Cropper/issues/401)

### Added
- CropImageOptions: Option to change progress bar color. [#390](https://github.com/CanHub/Android-Image-Cropper/issues/390)

## [4.3.0] - 10/06/2022
### Added
- Added a helper text on top of crop overlay which moves along with it. [#381](https://github.com/CanHub/Android-Image-Cropper/issues/381)

### Fixed
- The translation of `Camera` and `Gallery` does not exist in some languages.[#358](https://github.com/CanHub/Android-Image-Cropper/issues/358)

## [4.2.1] - 04/04/2022
### Added
- Added support for optionally displaying an intent chooser when selecting image source. [#325](https://github.com/CanHub/Android-Image-Cropper/issues/325)
### Changed
- CropException sealed class with cancellation and Image exceptions  [#332](https://github.com/CanHub/Android-Image-Cropper/issues/332)
### Fixed
- Fix disable closing AlertDialog when touching outside the dialog [#334](https://github.com/CanHub/Android-Image-Cropper/issues/334)

## [4.2.0] - 21/03/2022
### Added
- Added an option to skip manual editing and return entire image when required [#324](https://github.com/CanHub/Android-Image-Cropper/pull/324)
### Fixed
- Added missing support for `ScaleType.CENTER_CROP` [#220](https://github.com/CanHub/Android-Image-Cropper/issues/220)
- State is now preserved across configuration changes [#296](https://github.com/CanHub/Android-Image-Cropper/issues/296)
- Fix shadow bug [#261](https://github.com/CanHub/Android-Image-Cropper/issues/261)
### Changed
- Update portuguese strings [#321](https://github.com/CanHub/Android-Image-Cropper/issues/321)

## [4.1.0] - 02/02/2022
### Fixed
- When TakePictureContract returns false or null return null result.  [#287](https://github.com/CanHub/Android-Image-Cropper/issues/287)
### Added
- Added provision to add Oval crop corners when the crop style is Rectangle [#305](https://github.com/CanHub/Android-Image-Cropper/issues/305)


## [4.0.0] - 30/11/21
### Fixed
- Issue where some devices based on MIUI would not retrieve image from gallery [#253](https://github.com/CanHub/Android-Image-Cropper/issues/253)

### Changed
- `minSdkVersion = 16`, previous versions have been deprecated by Google.

### Added
- Added option to enable users specify image source [#226](https://github.com/CanHub/Android-Image-Cropper/issues/226)

### Removed
- Pick Image, now the library will handle this without expose the contract [#271](https://github.com/CanHub/Android-Image-Cropper/issues/271)
- Need for `READ_EXTERNAL_STORAGE` permission [#271](https://github.com/CanHub/Android-Image-Cropper/issues/271)
- Previous deprecated `ActivityBuilder`, `startPickImageActivity`, `getActivityResult` and `onActivityResult` [#145](https://github.com/CanHub/Android-Image-Cropper/issues/145)
- Removed ProGuard rule from ReadMe [#257](https://github.com/CanHub/Android-Image-Cropper/issues/257)
- Removed unused dependencies and settings from Gradle files [#265](https://github.com/CanHub/Android-Image-Cropper/issues/265)

## [3.3.5] - 07/09/21
### Fixed
- Set output uri ignored [#207](https://github.com/CanHub/Android-Image-Cropper/issues/207)

## [3.3.4] - 02/09/21
### Changed
- Update to Android 12
- Update library to gradle 7.0.1 and Java 11 [#191](https://github.com/CanHub/Android-Image-Cropper/issues/191)
- Any crop action should return uri content [#180](https://github.com/CanHub/Android-Image-Cropper/issues/180)

### Fixed
- Implement onBackPressed() in sample code for handle backButton pressed [#174](https://github.com/CanHub/Android-Image-Cropper/issues/174)

## [3.2.2] - 31/07/21
### Fixed
- After cropping a camera image, cancelling library picker shows again the last cropped image [#162](https://github.com/CanHub/Android-Image-Cropper/issues/162)

## [3.2.1] - 14/07/21
### Fixed
- Unable to get camera image from contract [#160](https://github.com/CanHub/Android-Image-Cropper/issues/160)

## [3.2.0] - 03/07/21
### Added
- `CropImageContract` and `PickImageContract` [#145](https://github.com/CanHub/Android-Image-Cropper/issues/145)
- added dependency to `androidx.activity:activity-ktx:.2.3` [#145](https://github.com/CanHub/Android-Image-Cropper/issues/145)

### Changed
- `CropImageActivity.onActivityResult` no longer receives any result. Override `onPickImageResult` instead. [#145](https://github.com/CanHub/Android-Image-Cropper/issues/145)

### Deprecated
- deprecated old methods that depend on the deprecated `onActivityResult`. Use `CropImageContract` and `PickImageContract` instead. [#145](https://github.com/CanHub/Android-Image-Cropper/issues/145)

## [3.1.3] - 10/06/21
### Fixed
- ContextWrapper cannot be cast to FragmentActivity [#136](https://github.com/CanHub/Android-Image-Cropper/issues/136)

## [3.1.2] - 07/06/21
### Fixed
- Missing file extension under Android 10 [#138](https://github.com/CanHub/Android-Image-Cropper/issues/138)
- Crashing when using the code `CropImage.activity().start(requireActivity(), this)` resolved [#133](https://github.com/CanHub/Android-Image-Cropper/issues/133)

## [3.1.1] - 17/05/21
### Fixed
- Make isReadExternalStoragePermissionsRequired and 2 other functions visible in Java [#129](https://github.com/CanHub/Android-Image-Cropper/issues/129)

## [3.1.0] - 09/05/21
### Added
- Add Java Sample code [#125](https://github.com/CanHub/Android-Image-Cropper/pull/125)

### Fixed
- Cannot call library method from Java language [#113](https://github.com/CanHub/Android-Image-Cropper/issues/113) [#123](https://github.com/CanHub/Android-Image-Cropper/issues/123)

### Changed
- New option for different file names [#122](https://github.com/CanHub/Android-Image-Cropper/pull/122)

## [3.0.1] - 21/04/21
### Added
- Estonian language [#119](https://github.com/CanHub/Android-Image-Cropper/pull/119)

## [3.0.0] - 13/04/21
### Removed
- Methods `getUri`.

### Add
- Methods `getFilePath` and `getUriContent`.

### Fixed
- ENOENT (no such file or directory) [#99](https://github.com/CanHub/Android-Image-Cropper/issues/99)
- `content://` instead of `file://` [#83](https://github.com/CanHub/Android-Image-Cropper/issues/83) [#84](https://github.com/CanHub/Android-Image-Cropper/issues/84)

## [2.3.2-alpha] - 12/04/21
### Added
- @JvmStatic annotation in CropImage.activity() and fun activity(uri) [#108](https://github.com/CanHub/Android-Image-Cropper/issues/108)

## [2.3.1] - 01/04/21
### Changed
- Added "fun" for all Kotlin interfaces when possible [#102](https://github.com/CanHub/Android-Image-Cropper/issues/102)

## [2.3.0] - 30/03/21
### Changed
- CropOverlayView to Kotlin [#38](https://github.com/CanHub/Android-Image-Cropper/issues/38)
- CropImageView to Kotlin [#39](https://github.com/CanHub/Android-Image-Cropper/issues/39)
- CropImage to Kotlin [#41](https://github.com/CanHub/Android-Image-Cropper/issues/41)
- BitmapUtils to Kotlin [#35](https://github.com/CanHub/Android-Image-Cropper/issues/35)

## [2.2.2] - 19/03/21
### Changed
- CropWindowMoveHandler to kotlin [#36](https://github.com/CanHub/Android-Image-Cropper/issues/36)

### Fixed
- Split appCompat version [#85](https://github.com/CanHub/Android-Image-Cropper/issues/85)

### Update
- Kotlin Version from `1.4.21` to `1.4.30`
- lifecycle-runtime-ktx Version from `2.2.0` to `2.3.0`

## [2.2.1] - 04/03/21
### Added
- Sample code extending Activity [#46](https://github.com/CanHub/Android-Image-Cropper/issues/46)

### Fixed
- Bug when crop using Custom Activity (extend) [43](https://github.com/CanHub/Android-Image-Cropper/issues/43)

## [2.2.0] - 04/03/21
### Added
- Vertical-only and horizontal-only cropping modes [#76]((https://github.com/CanHub/Android-Image-Cropper/pull/76))
- Option to disable movement of the crop window by dragging the center [#79](https://github.com/CanHub/Android-Image-Cropper/pull/79)

### Fixed
-  Turkish Translations [#72](https://github.com/CanHub/Android-Image-Cropper/pull/72)

## [2.1.1] - 27/02/21
### Added
- CropImage.getActivityResult(data).getBitmap(context) [#49](https://github.com/CanHub/Android-Image-Cropper/issues/49)

### Fixed
- CropImageView incorrectly restored on rotation [#68](https://github.com/CanHub/Android-Image-Cropper/issues/68)

## [2.1.0] - 11/02/21
### Changed
- From Java to Kotlin: [CropImageOptions](https://github.com/CanHub/Android-Image-Cropper/issues/40), [CropWindowHandler](https://github.com/CanHub/Android-Image-Cropper/issues/37)

### Fixed
- Null CompressFormat [#44](https://github.com/CanHub/Android-Image-Cropper/issues/44)
- [Galley option not showing](https://github.com/CanHub/Android-Image-Cropper/issues/20)
- [Camera option not showing](https://github.com/CanHub/Android-Image-Cropper/issues/52)

## [2.0.3] - 27/01/21
Versions `2.0.1` and `2.0.2` are similar, issues with jitpack.
### Fixed
- Make CropImageActivity open for extensions

## [2.0.0] - 12/01/21
### Changed
- AsyncTask to Kotlin Coroutines [#9](https://github.com/CanHub/Android-Image-Cropper/issues/9)

### Fixed
- Uri for camera capture option is now invariant for Android 10 and above [#21](https://github.com/CanHub/Android-Image-Cropper/issues/21)

## [1.1.1] - 03/01/21
### Added
- Ktlint
- Release using JitPack

### Changed
- Java to kotlin
- Change icons from PNG to vectors

## [1.1.0] - 13/12/20
### Changed
- Update many library versions

### Fixed
- Android 10, 11 Permissions
- Android 10, 11 Scope Storage

## [1.0.0] - 21/11/20
- Copy from previous repo [ArthurHub](https://github.com/ArthurHub/Android-Image-Cropper/)
