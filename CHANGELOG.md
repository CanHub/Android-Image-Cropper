# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
- `Added` for new features.
- `Changed` for changes in existing functionality.
- `Deprecated` for soon-to-be removed features.
- `Removed` for now removed features.
- `Fixed` for any bug fixes.
- `Security` in case of vulnerabilities.

## [unreleased x.x.x] -

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
