# Android Image Cropper - Comprehensive Test Coverage Plan

**Created:** 2026-04-23  
**Last Updated:** 2026-04-24  
**Status:** In Progress - Phase 3 Complete  
**Goal:** Achieve comprehensive test coverage to catch bugs before CI/release

---

## Phase Status

| Phase | Status | Tests Added | Description |
|-------|--------|-------------|-------------|
| **Phase 1** | ✅ **Complete** | 69 tests | Security Foundation - URI and file handling |
| **Phase 2** | ✅ **Complete** | 65 tests | Configuration & Options validation |
| **Phase 3** | ✅ **Complete** | 32 tests | Async Operations (coroutines, worker jobs) |
| **Phase 4** | ⏳ Pending | - | Core Bitmap Operations |
| **Phase 5** | ⏳ Pending | - | Crop Window Logic |
| **Phase 6** | ⏳ Pending | - | UI Components |
| **Phase 7** | ⏳ Pending | - | Public API |
| **Phase 8** | ⏳ Pending | - | Supporting Features |

**Total Tests:** 166 tests across 9 test files  
**Coverage Target:** 70-80% overall, 90%+ for critical security files

### Completed Test Files
- ✅ `GetFilePathFromUriTest.kt` (27 tests) - Phase 1
- ✅ `GetUriForFileTest.kt` (25 tests) - Phase 1
- ✅ `BitmapUtilsTest.kt` (17+ tests expanded) - Phase 1
- ✅ `CropImageOptionsTest.kt` (33 tests) - Phase 2
- ✅ `CropExceptionTest.kt` (8 tests) - Phase 2
- ✅ `ParcelableUtilsTest.kt` (24 tests) - Phase 2
- ✅ `BitmapLoadingWorkerJobTest.kt` (19 tests) - Phase 3
- ✅ `BitmapCroppingWorkerJobTest.kt` (14 tests) - Phase 3
- ✅ `TestCoroutineExtensions.kt` (test helper) - Phase 3

---

## Executive Summary

**Current State:**
- 19 source files (7,047 total LOC)
- Only 4 test files covering 3 components
- Critical security-sensitive code (URI handling, file access) is UNTESTED
- Complex async coroutine operations have NO tests
- Main public API (CropImageView - 1,890 LOC) has only 1 snapshot test

**Risk Level:** HIGH - Production library with thousands of users handling security-sensitive operations without adequate test coverage.

**Estimated Timeline:**
- **Full Coverage:** 80-90 developer days (~4-4.5 months with 1 developer)
- **Critical Path Only (Security + Core):** 30-35 days
- **Accelerated (2 developers):** 2-2.5 months

---

## Table of Contents

1. [Testing Strategy Overview](#1-testing-strategy-overview)
2. [Priority Levels](#2-priority-levels)
3. [File-by-File Test Plan](#3-file-by-file-test-plan)
4. [New Test Files to Create](#4-new-test-files-to-create)
5. [Test Infrastructure](#5-test-infrastructure)
6. [Implementation Order](#6-implementation-order)
7. [Success Criteria](#7-success-criteria)
8. [Risk Areas & Mitigation](#8-risk-areas--mitigation)

---

## 1. Testing Strategy Overview

### Test Type Distribution

**Unit Tests (Priority 1 - 70% of effort):**
- Business logic, calculations, validators
- BitmapUtils operations, CropWindowHandler logic
- CropImageOptions validation
- Exception handling
- **Tools:** JUnit 4.13.2, MockK 1.13.12, Robolectric 4.12.1

**Integration Tests (Priority 2 - 20% of effort):**
- Async worker jobs with coroutines
- File I/O operations with URI conversion
- Android framework integration (Activities, Intents, Parcelables)
- **Tools:** Robolectric, MockK, Kotlin Coroutines Test (needs addition)

**Snapshot/UI Tests (Priority 3 - 10% of effort):**
- Crop overlay rendering
- Gesture handling visual states
- Animation interpolation
- **Tools:** Paparazzi 1.3.3 (already configured)

---

## 2. Priority Levels

### CRITICAL (Must Have - Week 1)
Security and data integrity issues that could lead to vulnerabilities or data loss:

1. **GetFilePathFromUri.kt** - Path traversal vulnerabilities
2. **GetUriForFile.kt** - File provider security issues
3. **BitmapUtils.kt** - Output URI validation (partial coverage exists, expand)
4. **CropImageOptions.kt** - Configuration validation edge cases
5. **BitmapCroppingWorkerJob.kt** - Async cropping error handling
6. **BitmapLoadingWorkerJob.kt** - Async loading error handling

### HIGH (Should Have - Week 2-3)
Core functionality that users directly interact with:

1. **CropImageView.kt** - Public API methods, lifecycle, state management
2. **CropWindowHandler.kt** - Crop window bounds calculations
3. **CropWindowMoveHandler.kt** - Touch gesture handling logic
4. **CropOverlayView.kt** - Crop overlay rendering and interaction
5. **CropImageActivity.kt** - Activity lifecycle (deprecated but still used)

### MEDIUM (Nice to Have - Week 4)
Supporting functionality and edge cases:

1. **CropImageAnimation.kt** - Animation interpolation
2. **CropImage.kt** - Helper utilities (toOvalBitmap, ActivityResult)
3. **CropImageIntentChooser.kt** - Intent selection logic
4. **ParcelableUtils.kt** - Parcelable extension functions
5. **CropException.kt** - Exception types
6. **CropFileProvider.kt** - File provider (minimal code)

---

## 3. File-by-File Test Plan

### CRITICAL Priority

#### GetFilePathFromUri.kt (67 LOC)
**Purpose:** Converts URIs to file paths, creates temp files from content URIs

**Security Risks:** Path traversal, file injection, temp file leaks

**Test Approach:** Unit tests with Robolectric

**Tests Needed:**
1. **Path Validation Tests:**
   - Valid content:// URIs → proper temp file creation
   - file:// URIs → extract path correctly
   - Malicious URIs with path traversal (`../../../etc/passwd`)
   - URIs with null schemes
   - URIs with encoded special characters

2. **Temp File Management:**
   - Unique temp file names generated correctly
   - Non-unique mode reuses filename
   - Temp files created in cache directory only
   - File extension extraction from MIME type
   - Unknown MIME types handled gracefully

3. **Stream Handling:**
   - InputStream copy completes successfully
   - IOException handling during copy
   - Streams closed properly (even on error)
   - Large file handling (memory efficiency)

4. **Edge Cases:**
   - Empty URIs
   - URIs with missing content resolver data
   - Permission denied scenarios (mock)

**Mocking Strategy:**
- Mock `Context`, `ContentResolver`
- Mock `InputStream` for controlled read scenarios
- Use Robolectric for file system operations

**Estimated Complexity:** Medium (3-4 days)

**Test File:** `GetFilePathFromUriTest.kt`

---

#### GetUriForFile.kt (97 LOC)
**Purpose:** Converts File to URI using FileProvider with extensive fallback logic

**Security Risks:** File provider authority vulnerabilities, external storage access on SDK < 29

**Test Approach:** Unit tests with Robolectric, multiple SDK level testing

**Tests Needed:**
1. **FileProvider Success Path:**
   - Standard FileProvider URI generation
   - Authority calculation (`packageName.cropper.fileprovider`)
   - Valid file returns content:// URI

2. **Fallback Logic (OS < 29):**
   - External cache directory fallback
   - File copying to CROP_LIB_CACHE
   - Manual URI construction when provider fails
   - file:// URI as last resort

3. **SDK Version Handling:**
   - SDK 26+ uses Files.createDirectories
   - SDK < 26 uses File.mkdirs
   - SDK 29+ blocks external storage access

4. **Error Handling:**
   - FileProvider exception handling
   - Copy failures recovery
   - Directory creation failures
   - All paths close streams properly

5. **Cache Management:**
   - Cache folder creation
   - File name preservation in cache
   - Cache location consistency

**Mocking Strategy:**
- Mock `Context` with different SDK levels
- Mock `FileProvider` to force exceptions
- Use Robolectric for file system

**Estimated Complexity:** High (4-5 days)

**Test File:** `GetUriForFileTest.kt`

---

#### BitmapUtils.kt (986 LOC) - Expanded Coverage
**Purpose:** Core image processing utilities

**Existing Coverage:** Rectangle calculations, URI validation (good start)

**Test Approach:** Unit tests with MockK and Robolectric

**Additional Tests Needed:**
1. **Image Decoding:**
   - `decodeSampledBitmap()` - sample size calculation
   - Invalid URIs throw CropException.FailedToLoadBitmap
   - OutOfMemoryError handling with sampling
   - EXIF orientation handling (all 8 orientations)
   - Large images trigger max texture size limits

2. **Image Cropping:**
   - `cropBitmap()` - URI-based cropping
   - `cropBitmapObjectHandleOOM()` - OOM retry logic with scaling
   - Crop points outside bitmap bounds
   - Zero/negative crop dimensions
   - Rotation (0°, 90°, 180°, 270°)
   - Flip horizontal/vertical

3. **Aspect Ratio:**
   - Fixed aspect ratio enforcement
   - Free aspect ratio cropping
   - Edge cases: 0 aspect ratio, very large ratios

4. **Image Resizing:**
   - `resizeBitmap()` with all RequestSizeOptions
   - Resize respecting max dimensions
   - No resize when dimensions smaller than request

5. **Image Writing:**
   - `writeBitmapToUri()` with different CompressFormats
   - Custom output URI vs generated URI
   - Write failures (disk full simulation)
   - Validation errors for mismatched extensions

6. **Max Texture Size:**
   - `getMaxTextureSize()` calculation
   - Fallback to 2048 when unavailable

**Mocking Strategy:**
- Mock `Context`, `ContentResolver`
- Mock `BitmapFactory`, `BitmapRegionDecoder`
- Use real Rect/RectF for geometry

**Estimated Complexity:** High (5-6 days)

**Test File:** Expand `BitmapUtilsTest.kt`

---

#### CropImageOptions.kt (129 LOC)
**Purpose:** Configuration data class with validation in init block

**Test Approach:** Unit tests (no Android dependencies)

**Tests Needed:**
1. **Validation Success:**
   - Default values create valid options
   - All valid boundary values accepted
   - Copy() preserves validation

2. **Validation Failures (IllegalArgumentException):**
   - `maxZoom < 0` → exception with message "Cannot set max zoom..."
   - `touchRadius < 0` → exception
   - `initialCropWindowPaddingRatio < 0` → exception
   - `initialCropWindowPaddingRatio >= 0.5` → exception
   - `aspectRatioX <= 0` → exception
   - `aspectRatioY <= 0` → exception
   - `borderLineThickness < 0` → exception
   - `borderCornerThickness < 0` → exception
   - `guidelinesThickness < 0` → exception
   - `minCropWindowHeight < 0` → exception
   - `minCropResultWidth < 0` → exception
   - `minCropResultHeight < 0` → exception
   - `maxCropResultWidth < minCropResultWidth` → exception
   - `maxCropResultHeight < minCropResultHeight` → exception
   - `outputRequestWidth < 0` → exception
   - `outputRequestHeight < 0` → exception
   - `rotationDegrees < 0` or `> 360` → exception

3. **Boundary Values:**
   - `maxZoom = 0` (valid minimum)
   - `initialCropWindowPaddingRatio = 0.49` (valid maximum)
   - `rotationDegrees = 360` (valid maximum)

4. **Parcelable:**
   - Round-trip parceling preserves all fields
   - Custom Parcelable types handled correctly

**Mocking Strategy:** None (pure Kotlin tests)

**Estimated Complexity:** Low (2 days)

**Test File:** `CropImageOptionsTest.kt`

---

#### BitmapCroppingWorkerJob.kt (152 LOC)
**Purpose:** Async coroutine-based bitmap cropping

**Test Approach:** Integration tests with coroutines testing

**Tests Needed:**
1. **Successful Cropping Flow:**
   - URI-based cropping completes successfully
   - Bitmap-based cropping completes successfully
   - Result contains bitmap, URI, sampleSize
   - Callback invoked on Main dispatcher

2. **Error Handling:**
   - Exception during cropping → Result.error populated
   - Null uri and bitmap → empty result
   - WeakReference cleared → bitmap recycled
   - Callback not invoked if view reference lost

3. **Coroutine Lifecycle:**
   - `start()` launches on Dispatchers.Default
   - `cancel()` cancels the job
   - Cancellation prevents callback
   - `isActive` checks prevent work after cancel

4. **Bitmap Memory Management:**
   - Unused bitmaps recycled (callback not called)
   - Successful result bitmap not recycled

5. **Context Switch:**
   - Cropping work on Default dispatcher
   - URI writing on IO dispatcher
   - Callback on Main dispatcher

**Mocking Strategy:**
- Mock `Context`, `CropImageView` (WeakReference)
- Mock BitmapUtils methods with MockK
- Use `runTest` from kotlinx-coroutines-test
- Use `TestCoroutineDispatcher`

**Estimated Complexity:** Medium-High (4 days)

**Test File:** `BitmapCroppingWorkerJobTest.kt`

**New Dependency Required:** `kotlinx-coroutines-test:1.8.1`

---

#### BitmapLoadingWorkerJob.kt (109 LOC)
**Purpose:** Async coroutine-based image loading with EXIF orientation

**Test Approach:** Integration tests with coroutines testing

**Tests Needed:**
1. **Successful Loading Flow:**
   - Image decoded at correct sample size
   - EXIF orientation applied
   - Result contains bitmap, rotation degrees, flip flags
   - Callback invoked on Main dispatcher

2. **Error Handling:**
   - Exception during decode → Result.error populated
   - Exception during EXIF read → Result.error populated
   - WeakReference cleared → bitmap recycled

3. **Density Calculation:**
   - High DPI screens (density > 1) → adjusted dimensions
   - Low DPI screens → no adjustment
   - Width/height calculated from display metrics

4. **Coroutine Lifecycle:**
   - `start()` launches on Dispatchers.Default
   - `cancel()` cancels the job
   - `isActive` checks at proper points

5. **EXIF Orientation:**
   - All 8 EXIF orientations produce correct rotation/flip
   - Missing EXIF data → no rotation/flip

**Mocking Strategy:**
- Mock `Context`, `CropImageView`, `Resources`, `DisplayMetrics`
- Mock BitmapUtils methods
- Use coroutines test library

**Estimated Complexity:** Medium (3-4 days)

**Test File:** `BitmapLoadingWorkerJobTest.kt`

---

### HIGH Priority

#### CropImageView.kt (1,890 LOC)
**Purpose:** Main public API - custom view for cropping

**Existing Coverage:** 1 Paparazzi snapshot test (toOvalBitmap helper)

**Test Approach:** Mix of unit tests (Robolectric) and snapshot tests (Paparazzi)

**Tests Needed:**
1. **Lifecycle & State:**
   - View initialization with XML attrs
   - `setImageUriAsync()` triggers loading job
   - `setImageBitmap()` sets image immediately
   - Save/restore instance state
   - Rotation configuration change handling

2. **Public API Methods:**
   - `getCroppedImage()` returns correct bitmap
   - `getCroppedImageAsync()` triggers cropping job
   - `setImageUriAsync()` with various URI schemes
   - `setAspectRatio()` updates crop window
   - `resetCropRect()` resets to default
   - `rotateImage()` by 90° increments
   - `flipImageHorizontally()` / `flipImageVertically()`

3. **Crop Window:**
   - `setCropRect()` updates crop window
   - `getCropRect()` returns correct bounds
   - `getCropPoints()` returns transformed points
   - `getWholeImageRect()` returns image bounds

4. **Zoom & Transform:**
   - Auto-zoom on image load
   - Max zoom enforcement
   - Manual zoom gestures (mock touch events)
   - Image matrix transformations

5. **Callbacks:**
   - `OnSetImageUriCompleteListener` invoked
   - `OnCropImageCompleteListener` invoked
   - `OnSetCropOverlayReleasedListener` invoked
   - `OnSetCropOverlayMovedListener` invoked

6. **Error Cases:**
   - Invalid URI → error callback
   - Bitmap decode failure → error callback
   - OOM during crop → error callback
   - Null/empty input handling

7. **Snapshot Tests (Paparazzi):**
   - Default crop window on image load
   - Oval crop shape rendering
   - Rectangle with rounded corners
   - Fixed aspect ratio (16:9, 4:3, 1:1)
   - Rotated image (90°, 180°, 270°)
   - Flipped image
   - Crop overlay hidden
   - Progress bar shown/hidden

**Mocking Strategy:**
- Use Robolectric for View lifecycle
- Mock worker jobs to control async behavior
- Mock BitmapUtils for deterministic results
- Real gesture events for interaction tests

**Estimated Complexity:** Very High (7-8 days)

**Test File:** Expand `CropImageViewTest.kt`

---

#### CropWindowHandler.kt (466 LOC)
**Purpose:** Manages crop window position and size constraints

**Test Approach:** Unit tests (geometry calculations, no Android dependencies)

**Tests Needed:**
1. **Initialization:**
   - Default rectangle creation
   - Set initial crop window rectangle
   - Set crop window with percent of image

2. **Constraint Calculations:**
   - `getMinCropWidth()` respects both window and result minimums
   - `getMinCropHeight()` considers scale factors
   - `getMaxCropWidth()` respects both window and result maximums
   - `getMaxCropHeight()` considers scale factors

3. **Fixed Aspect Ratio:**
   - Maintain aspect ratio during resize
   - Calculate target aspect ratio
   - Adjust rectangle to fit aspect ratio within bounds

4. **Crop Window Updates:**
   - Move crop window within bounds
   - Resize crop window respecting constraints
   - Snap to image edges within threshold
   - Handle oval crop shape constraints

5. **Scale Factors:**
   - Width scale factor calculation
   - Height scale factor calculation
   - Scale affects min/max crop sizes

6. **Edge Cases:**
   - Zero-width/height images
   - Crop window larger than image
   - Negative coordinates clamped
   - Floating-point precision issues

**Mocking Strategy:** None (pure math tests with RectF)

**Estimated Complexity:** Medium (3-4 days)

**Test File:** `CropWindowHandlerTest.kt`

---

#### CropWindowMoveHandler.kt (914 LOC)
**Purpose:** Handles touch gestures for moving/resizing crop window

**Test Approach:** Unit tests (geometry calculations)

**Tests Needed:**
1. **Move Type Initialization:**
   - All 9 move types calculate correct touch offset
   - Corner moves (TOP_LEFT, TOP_RIGHT, etc.)
   - Edge moves (LEFT, TOP, RIGHT, BOTTOM)
   - Center move

2. **Free Aspect Ratio Moves:**
   - Horizontal edge moves (LEFT/RIGHT)
   - Vertical edge moves (TOP/BOTTOM)
   - Corner moves resize both dimensions
   - Center moves translate entire window
   - Respect min/max width/height constraints
   - Snap to image bounds

3. **Fixed Aspect Ratio Moves:**
   - Corner moves maintain aspect ratio
   - Edge moves adjust opposite edge to maintain ratio
   - Complex aspect ratio enforcement
   - Bounds checking with aspect ratio

4. **Snap Behavior:**
   - Snap to edges within snap margin
   - No snap beyond snap margin
   - Snap to view edges
   - Snap to image bounds

5. **Constraint Enforcement:**
   - Min width/height enforced
   - Max width/height enforced
   - Stay within image bounds
   - Stay within view bounds

6. **Edge Cases:**
   - Touch offset maintains handle position
   - Very small crop windows
   - Very large crop windows
   - Crop window at image edge

**Mocking Strategy:**
- Mock `CropWindowHandler` to provide constraints
- Real RectF for geometry

**Estimated Complexity:** High (5-6 days)

**Test File:** `CropWindowMoveHandlerTest.kt`

---

#### CropOverlayView.kt (1,317 LOC)
**Purpose:** Renders crop overlay UI, handles touch events

**Test Approach:** Snapshot tests (Paparazzi) + Unit tests for logic

**Tests Needed:**
1. **Rendering (Snapshot Tests):**
   - Default rectangular crop overlay
   - Oval crop overlay
   - Rectangle with rounded corners
   - Circle corner shapes
   - Guidelines (ON, ON_TOUCH, OFF)
   - Different border colors/thicknesses
   - Background overlay opacity
   - Label text rendering

2. **Touch Event Handling (Unit Tests):**
   - Touch on corner → corner move handler created
   - Touch on edge → edge move handler created
   - Touch in center → center move handler created
   - Touch outside → no handler created
   - Multi-touch with scale gesture detector
   - Move events update crop window
   - Release events trigger callback

3. **Initialization:**
   - Init with CropImageOptions
   - Set crop shape (RECTANGLE, OVAL, etc.)
   - Set aspect ratio
   - Set min/max crop sizes
   - Set guidelines mode

4. **Bounds Management:**
   - `setBounds()` updates drawing bounds
   - Handle rotated image bounds
   - Crop window stays within bounds

5. **Multi-touch:**
   - Scale gesture detection
   - Simultaneous resize and drag
   - Multi-touch enabled/disabled

6. **Snap Behavior:**
   - Calculate snap radius
   - Touch radius for handles

**Mocking Strategy:**
- Use Paparazzi for snapshot tests
- Mock Canvas for unit tests
- Mock MotionEvent for touch tests
- Use Robolectric for View lifecycle

**Estimated Complexity:** High (5-6 days)

**Test Files:** `CropOverlayViewTest.kt` (unit), expand `CropImageViewTest.kt` (snapshots)

---

#### CropImageActivity.kt (487 LOC)
**Purpose:** Deprecated activity-based cropping (but still in use)

**Test Approach:** Integration tests with Robolectric

**Tests Needed:**
1. **Lifecycle:**
   - `onCreate()` with source URI → load image
   - `onCreate()` without URI → show source picker
   - `onSaveInstanceState()` / `onRestoreInstanceState()`
   - Back button → cancel result
   - Configuration change preserves state

2. **Image Source Selection:**
   - Gallery picker launched
   - Camera picker launched
   - Intent chooser shown
   - Source dialog shown
   - Skip editing mode

3. **Cropping Flow:**
   - Image loaded → crop view shown
   - Crop button → crop and return result
   - Rotate button → image rotated
   - Flip buttons → image flipped
   - Cancel → return cancelled result

4. **Result Handling:**
   - Successful crop → ActivityResult with URI
   - Crop error → ActivityResult with error
   - User cancel → CancelledResult

5. **Options Application:**
   - CropImageOptions applied to view
   - Menu customization (colors, icons)
   - Toolbar customization
   - Activity background color

6. **Permissions:**
   - Camera permission handling
   - Gallery access handling

**Mocking Strategy:**
- Use Robolectric for Activity testing
- Mock ActivityResultContracts
- Mock file system operations

**Estimated Complexity:** Medium-High (4-5 days)

**Test File:** `CropImageActivityTest.kt`

---

### MEDIUM Priority

#### CropImageAnimation.kt (84 LOC)
**Purpose:** Smooth zoom animation

**Test Approach:** Unit tests with time-based assertions

**Tests Needed:**
1. **Animation Setup:**
   - `setStartState()` captures initial state
   - `setEndState()` captures final state
   - Duration = 300ms
   - AccelerateDecelerateInterpolator used

2. **Interpolation:**
   - `applyTransformation()` at t=0 → start state
   - `applyTransformation()` at t=0.5 → midpoint
   - `applyTransformation()` at t=1 → end state
   - Crop window rect interpolated correctly
   - Bound points interpolated correctly
   - Image matrix values interpolated correctly

3. **Callbacks:**
   - `onAnimationStart()` called
   - `onAnimationEnd()` clears animation
   - Image view invalidated during animation

**Mocking Strategy:**
- Mock `ImageView`, `CropOverlayView`
- Mock `Animation.Transformation`

**Estimated Complexity:** Low-Medium (2-3 days)

**Test File:** `CropImageAnimationTest.kt`

---

#### CropImage.kt (155 LOC)
**Purpose:** Helper object with utilities and result classes

**Test Approach:** Unit tests

**Tests Needed:**
1. **toOvalBitmap():**
   - Rectangular bitmap → oval with transparent corners
   - Square bitmap → circle
   - Original bitmap recycled
   - Output has ARGB_8888 config

2. **ActivityResult:**
   - Constructor creates valid result
   - Parcelable round-trip preserves data
   - `isSuccessful` when no error
   - `isSuccessful = false` when error present

3. **CancelledResult:**
   - Contains CropException.Cancellation
   - `isSuccessful = false`
   - All fields have sensible defaults

**Mocking Strategy:** None for toOvalBitmap (uses real Bitmap), Mock Parcel

**Estimated Complexity:** Low (2 days)

**Test File:** `CropImageTest.kt`

---

#### CropImageIntentChooser.kt (241 LOC)
**Purpose:** Intent chooser for gallery/camera selection

**Test Approach:** Integration tests with Robolectric

**Tests Needed:**
1. **Intent Collection:**
   - `showChooserIntent()` with camera → camera intents included
   - `showChooserIntent()` with gallery → gallery intents included
   - `showChooserIntent()` with both → both included
   - Camera permission required → camera intents excluded

2. **Intent Prioritization:**
   - Priority list reorders intents correctly
   - Default priority: Google Photos > Samsung > OnePlus > MIUI
   - Custom priority list respected

3. **Callback Handling:**
   - Gallery result → `onSuccess()` with data URI
   - Camera result → `onSuccess()` with camera URI
   - Cancel → `onCancelled()`

4. **SDK Version Handling:**
   - SDK 23+ checks camera permission
   - SDK < 23 includes camera without check

**Mocking Strategy:**
- Mock `ComponentActivity`, `PackageManager`
- Mock `ActivityResultRegistry`
- Use Robolectric for Intent resolution

**Estimated Complexity:** Medium (3 days)

**Test File:** `CropImageIntentChooserTest.kt`

---

#### ParcelableUtils.kt (17 LOC)
**Purpose:** Extension functions for Parcelable extraction

**Test Approach:** Unit tests with Robolectric

**Tests Needed:**
1. **Bundle.parcelable():**
   - Existing parcelable → extracted correctly
   - Non-existent key → null
   - Wrong type → null (safe cast)
   - Null value → null

2. **Intent.parcelable():**
   - Existing parcelable extra → extracted correctly
   - Non-existent key → null
   - Wrong type → null
   - Null value → null

3. **Type Safety:**
   - Reified type parameter works correctly
   - Cast failures handled gracefully

**Mocking Strategy:** Use Robolectric for Bundle/Intent

**Estimated Complexity:** Very Low (1 day)

**Test File:** `ParcelableUtilsTest.kt`

---

#### CropException.kt (28 LOC)
**Purpose:** Sealed exception hierarchy

**Test Approach:** Unit tests

**Tests Needed:**
1. **Exception Types:**
   - `Cancellation` has correct message
   - `FailedToLoadBitmap` includes URI and message
   - `FailedToDecodeImage` includes URI
   - All extend CropException

2. **Serialization:**
   - Exceptions have serialVersionUID
   - Can be thrown and caught
   - Message formatting correct

**Mocking Strategy:** None

**Estimated Complexity:** Very Low (1 day)

**Test File:** `CropExceptionTest.kt`

---

#### CropFileProvider.kt (13 LOC)
**Purpose:** Empty FileProvider subclass

**Test Approach:** Minimal testing (boilerplate)

**Tests Needed:**
1. Verify class exists and extends FileProvider
2. Can be instantiated
3. Declared in AndroidManifest (integration test)

**Estimated Complexity:** Very Low (0.5 day)

**Test File:** `CropFileProviderTest.kt` (optional)

---

## 4. New Test Files to Create

All files in `/cropper/src/test/kotlin/com/canhub/cropper/`:

### Critical Priority
1. `GetFilePathFromUriTest.kt` - URI to file path conversion
2. `GetUriForFileTest.kt` - File to URI conversion with FileProvider
3. `CropImageOptionsTest.kt` - Configuration validation
4. `BitmapCroppingWorkerJobTest.kt` - Async cropping
5. `BitmapLoadingWorkerJobTest.kt` - Async loading

### High Priority
6. `CropWindowHandlerTest.kt` - Crop window constraints
7. `CropWindowMoveHandlerTest.kt` - Touch gesture handling
8. `CropOverlayViewTest.kt` - Overlay rendering and interaction
9. `CropImageActivityTest.kt` - Deprecated activity

### Medium Priority
10. `CropImageAnimationTest.kt` - Zoom animations
11. `CropImageTest.kt` - Helper utilities
12. `CropImageIntentChooserTest.kt` - Intent selection
13. `ParcelableUtilsTest.kt` - Parcelable extensions
14. `CropExceptionTest.kt` - Exception types

### Expand Existing
15. `BitmapUtilsTest.kt` - Add decoding, cropping, resizing tests
16. `CropImageViewTest.kt` - Add unit tests and more snapshots
17. `CropImageContractTest.kt` - May need expansion for edge cases

---

## 5. Test Infrastructure

### New Dependencies Required

Add to `cropper/build.gradle.kts`:

```kotlin
dependencies {
  // Existing test dependencies...
  
  // ADD: Coroutines testing
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
  
  // ADD: Truth assertions (optional, better than JUnit asserts)
  testImplementation("com.google.truth:truth:1.1.5")
  
  // ADD: Turbine for Flow testing (if needed)
  testImplementation("app.cash.turbine:turbine:1.0.0")
}
```

### Test Helpers & Utilities

Create `cropper/src/test/kotlin/com/canhub/cropper/test/`:

1. **TestCoroutineExtensions.kt**
```kotlin
// Helper to test coroutines with proper dispatchers
fun runTestWithDispatchers(block: suspend TestScope.() -> Unit)
```

2. **BitmapTestUtils.kt**
```kotlin
// Create test bitmaps with specific dimensions
fun createTestBitmap(width: Int, height: Int): Bitmap

// Create bitmap from test resources
fun loadTestBitmap(resourceName: String): Bitmap
```

3. **UriTestUtils.kt**
```kotlin
// Create content URIs for testing
fun createMockContentUri(path: String): Uri

// Create file URIs with path traversal attempts
fun createMaliciousUri(attack: String): Uri
```

4. **MockContextProvider.kt**
```kotlin
// Robolectric context with pre-configured mocks
fun createTestContext(): Context

// Context with specific SDK version
fun createTestContextForSdk(sdkInt: Int): Context
```

5. **SnapshotTestHelpers.kt**
```kotlin
// Standard Paparazzi setup with common configurations
fun createPaparazziRule(config: DeviceConfig): Paparazzi

// Create CropImageView with options for snapshots
fun createCropViewForSnapshot(options: CropImageOptions): CropImageView
```

### Test Fixtures

Create `cropper/src/test/resources/`:

1. **Test Images:**
   - `small-tree.jpg` (already exists)
   - `large-image.jpg` (4000x3000 to test sampling)
   - `portrait.jpg` (vertical orientation)
   - `rotated-90.jpg` (EXIF rotation)
   - `corrupted.jpg` (invalid data)
   - `tiny.png` (1x1 pixel)

2. **Test Data:**
   - `test-crop-options.json` (various CropImageOptions configs)
   - `malicious-uris.txt` (list of URI attack vectors)

---

## 6. Implementation Order

### Phase 1: Security Foundation ✅ **COMPLETE**
**Goal:** Eliminate security vulnerabilities

1. ✅ `GetFilePathFromUriTest.kt` (27 tests) - Path traversal prevention, malicious URI handling
2. ✅ `GetUriForFileTest.kt` (25 tests) - FileProvider security, fallback mechanisms
3. ✅ Expand `BitmapUtilsTest.kt` (+17 tests) - URI validation, network/executable blocking

**Deliverable:** ✅ All URI/file handling has security tests (69 total tests)

---

### Phase 2: Configuration & Options ✅ **COMPLETE**
**Goal:** Ensure configuration validation is bulletproof

4. ✅ `CropImageOptionsTest.kt` (33 tests) - All validation rules, boundary values, Parcelable
5. ✅ `CropExceptionTest.kt` (8 tests) - Exception hierarchy, messages, serialization
6. ✅ `ParcelableUtilsTest.kt` (24 tests) - Bundle/Intent extraction, type safety

**Deliverable:** ✅ Configuration cannot be misconfigured, exceptions well-tested (65 total tests)

---

### Phase 3: Async Operations ✅ **COMPLETE**
**Goal:** Async jobs handle errors gracefully

7. ✅ `kotlinx-coroutines-test` dependency (already added in Phase 1)
8. ✅ `TestCoroutineExtensions.kt` helper - CoroutineTestRule for dispatcher setup
9. ✅ `BitmapLoadingWorkerJobTest.kt` (19 tests) - Loading, EXIF, density, lifecycle, errors
10. ✅ `BitmapCroppingWorkerJobTest.kt` (13 tests) - URI/Bitmap cropping, memory, lifecycle, errors
    - Note: writeBitmapToUri exception scenario cannot be tested due to nested launch(Dispatchers.IO) in production code

**Deliverable:** ✅ All async operations tested with cancellation, errors, lifecycle (32 total tests)

---

### Phase 4: Core Bitmap Operations (Week 2-3)
**Goal:** Image processing is reliable

11. Create `BitmapTestUtils.kt` helper
12. Expand `BitmapUtilsTest.kt` - decoding, cropping, resizing (5-6 days)

**Deliverable:** BitmapUtils has comprehensive coverage

---

### Phase 5: Crop Window Logic (Week 3)
**Goal:** Crop window geometry is correct

13. `CropWindowHandlerTest.kt` (3-4 days)
14. `CropWindowMoveHandlerTest.kt` (5-6 days)

**Deliverable:** All crop window calculations tested

---

### Phase 6: UI Components (Week 3-4)
**Goal:** UI renders correctly and handles interaction

15. Create `SnapshotTestHelpers.kt`
16. `CropOverlayViewTest.kt` - unit tests (3-4 days)
17. Expand `CropImageViewTest.kt` - snapshots (2-3 days)
18. `CropImageActivityTest.kt` (4-5 days)

**Deliverable:** UI components tested with Paparazzi snapshots

---

### Phase 7: Public API (Week 4)
**Goal:** Main API is stable and tested

19. Expand `CropImageViewTest.kt` - unit tests (7-8 days)
20. Expand `CropImageContractTest.kt` - edge cases (1-2 days)

**Deliverable:** Public API has high coverage

---

### Phase 8: Supporting Features (Week 4-5)
**Goal:** Complete coverage of auxiliary features

21. `CropImageAnimationTest.kt` (2-3 days)
22. `CropImageTest.kt` (2 days)
23. `CropImageIntentChooserTest.kt` (3 days)

**Deliverable:** 100% file coverage achieved

---

## 7. Success Criteria

### Coverage Metrics

**Target:** 80% line coverage, 90% branch coverage for critical files

**Critical Files (Must achieve 90%+ coverage):**
- GetFilePathFromUri.kt
- GetUriForFile.kt
- BitmapUtils.kt (security functions)
- CropImageOptions.kt
- BitmapCroppingWorkerJob.kt
- BitmapLoadingWorkerJob.kt

**High Priority Files (Must achieve 75%+ coverage):**
- CropImageView.kt
- CropWindowHandler.kt
- CropWindowMoveHandler.kt
- CropOverlayView.kt

**Overall Target:** 70% line coverage across all production code

### Quality Gates

**Before Merge:**
1. All tests pass: `./gradlew testDebug`
2. No Paparazzi snapshot failures: `./gradlew verifyPaparazziDebug`
3. Ktlint passes: `./gradlew ktlint`
4. Coverage report generated: use JaCoCo plugin (add to build.gradle.kts)
5. No new warnings/errors

**CI Integration:**
- Add coverage reporting to GitHub Actions (build.yml)
- Fail build if coverage drops below threshold
- Generate coverage badge for README

### Documentation

**Each test file must include:**
1. KDoc header explaining what's being tested
2. `@Test` annotations with descriptive names
3. Given/When/Then structure in complex tests
4. Comments explaining non-obvious mocking setup

---

## 8. Risk Areas & Mitigation

### Hardest-to-Test Components

#### 1. CropImageView.kt (1,890 LOC)
**Challenge:** Complex view with touch events, lifecycle, async callbacks

**Approach:**
- Split into unit tests (logic) and integration tests (view lifecycle)
- Mock async jobs to control timing
- Use Robolectric for view inflation
- Use Paparazzi for visual regression
- Test callbacks with captured arguments (MockK `slot()`)

**Fallback:** If full coverage too difficult, focus on public API methods (80% coverage of public surface)

---

#### 2. Touch Event Handling (CropOverlayView, CropWindowMoveHandler)
**Challenge:** Simulating complex touch gestures

**Approach:**
- Create `MotionEvent` mocks with `obtain()` factory
- Test move handler logic separately from view
- Use geometry-only tests (no actual view rendering)
- Paparazzi snapshots for visual confirmation

**Fallback:** Test calculations independently, rely on sample app for manual gesture testing

---

#### 3. Async Coroutine Jobs
**Challenge:** Race conditions, timing issues, dispatcher switching

**Approach:**
- Use `kotlinx-coroutines-test` with `runTest`
- Use `TestCoroutineDispatcher` for deterministic execution
- Test cancellation explicitly
- Test weak reference cleanup

**Fallback:** Integration tests may be slower but more reliable than pure unit tests

---

#### 4. File System Operations
**Challenge:** Platform-specific behavior, permissions, SDK differences

**Approach:**
- Use Robolectric for file system
- Mock `Context` and `ContentResolver`
- Test multiple SDK versions (21, 26, 29, 34)
- Create actual temp files (cleanup in tearDown)

**Fallback:** More integration-style tests with real file operations

---

#### 5. BitmapUtils.kt - OOM Handling
**Challenge:** Can't easily trigger real OutOfMemoryError in tests

**Approach:**
- Mock `BitmapFactory` to throw `OutOfMemoryError`
- Test retry logic with scaling
- Test that bitmaps are recycled after OOM
- Unit test scale calculation separately

**Fallback:** Document OOM scenarios as manual test cases

---

#### 6. Paparazzi Snapshot Tests
**Challenge:** Flakiness, platform differences, CI environment

**Approach:**
- Use fixed device config (Pixel 5)
- Disable animations in tests
- Use deterministic test data (specific bitmaps)
- Record snapshots on macOS (CI uses macOS-latest)

**Fallback:** If snapshots too flaky, reduce number and focus on critical UI states

---

### Test Maintenance Considerations

**Anti-Patterns to Avoid:**
1. Over-mocking - Don't mock everything, use real objects when possible
2. Brittle tests - Don't test implementation details, test behavior
3. Slow tests - Keep unit tests fast (<100ms each)
4. Flaky tests - Ensure deterministic execution

**Best Practices:**
1. One assertion concept per test (not one `assert` call)
2. Clear test names describing scenario
3. Use test helpers for repeated setup
4. Group related tests with nested classes
5. Use `@Before` and `@After` for setup/cleanup

---

## 9. Next Steps

1. **Review this plan** - Adjust priorities based on team needs
2. **Set up JaCoCo** - Add coverage reporting to build
3. **Phase 1 Start** - Begin with security tests (GetFilePathFromUri, GetUriForFile)
4. **Track progress** - Update this document as tests are completed
5. **Continuous improvement** - Add more tests as bugs are discovered

---

**Plan Status:** Ready for implementation  
**Last Updated:** 2026-04-23
