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
