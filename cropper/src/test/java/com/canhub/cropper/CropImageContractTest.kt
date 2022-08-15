package com.canhub.cropper

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CropImageContractTest {

    @Test
    fun `WHEN providing invalid options THEN cropping should crash`() {
        // GIVEN
        var result: Exception? = null
        val expected: IllegalArgumentException = IllegalArgumentException()
        var fragment: ContractTestFragment? = null
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, Activity.RESULT_CANCELED, Intent())
            }
        }
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment = it }
        }
        // WHEN
        try {
            fragment?.cropImageIntent(options { setMaxZoom(-10) })
        } catch (e: Exception) {
            result = e
        }
        // THEN
        assertEquals(expected.javaClass, result?.javaClass)
    }

    @Test
    fun `WHEN cropping is cancelled by user, THEN result should be cancelled`() {
        // GIVEN
        val expected = CropImage.CancelledResult
        var fragment: ContractTestFragment? = null
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, Activity.RESULT_CANCELED, Intent())
            }
        }
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment = it }
        }
        // WHEN
        fragment?.cropImage(options())
        // THEN
        assertEquals(expected, fragment?.cropResult)
    }

    @Test
    fun `WHEN cropping succeeds, THEN result should be successful`() {
        // GIVEN
        var fragment: ContractTestFragment? = null
        val expected = CropImage.ActivityResult(
            originalUri = "content://original".toUri(),
            uriContent = "content://content".toUri(),
            error = null,
            cropPoints = floatArrayOf(),
            cropRect = Rect(1, 2, 3, 4),
            rotation = 45,
            wholeImageRect = Rect(10, 20, 0, 0),
            sampleSize = 0
        )
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                val intent = Intent()
                intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, expected)

                dispatchResult(requestCode, CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE, intent)
            }
        }
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment = it }
        }
        // WHEN
        fragment?.cropImage(options())
        // THEN
        assertEquals(expected, fragment?.cropResult)
    }

    @Test
    fun `WHEN starting crop with all options, THEN intent should contain these options`() {
        // GIVEN
        var cropImageIntent: Intent? = null
        val expectedClassName = CropImageActivity::class.java.name
        val expectedSource = "file://testInput".toUri()
        val options = options(expectedSource) {
            setCropShape(CropImageView.CropShape.OVAL)
            setSnapRadius(1f)
            setTouchRadius(2f)
            setGuidelines(CropImageView.Guidelines.ON_TOUCH)
            setScaleType(CropImageView.ScaleType.CENTER)
            setShowCropOverlay(true)
            setAutoZoomEnabled(false)
            setMultiTouchEnabled(true)
            setCenterMoveEnabled(false)
            setMaxZoom(17)
            setInitialCropWindowPaddingRatio(0.2f)
            setFixAspectRatio(true)
            setAspectRatio(3, 4)
            setBorderLineThickness(3f)
            setBorderLineColor(Color.GREEN)
            setBorderCornerThickness(5f)
            setBorderCornerOffset(6f)
            setBorderCornerLength(7f)
            setBorderCornerColor(Color.MAGENTA)
            setGuidelinesThickness(8f)
            setGuidelinesColor(Color.RED)
            setBackgroundColor(Color.BLUE)
            setMinCropWindowSize(5, 5)
            setMinCropResultSize(10, 10)
            setMaxCropResultSize(5000, 5000)
            setActivityTitle("Test Activity Title")
            setActivityMenuIconColor(Color.BLACK)
            setOutputUri(expectedSource)
            setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            setOutputCompressQuality(85)
            setRequestedSize(25, 30, CropImageView.RequestSizeOptions.NONE)
            setNoOutputImage(false)
            setInitialCropWindowRectangle(Rect(4, 5, 6, 7))
            setInitialRotation(13)
            setAllowRotation(true)
            setAllowFlipping(false)
            setAllowCounterRotation(true)
            setRotationDegrees(4)
            setFlipHorizontally(true)
            setFlipVertically(false)
            setCropMenuCropButtonTitle("Test Button Title")
            setCropMenuCropButtonIcon(R.drawable.ic_rotate_left_24)
        }
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
            }
        }
        // WHEN
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment -> cropImageIntent = fragment.cropImageIntent(options) }
        }
        val bundle = cropImageIntent?.getBundleExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE)
        // THEN
        assertEquals(expectedClassName, cropImageIntent?.component?.className)
        assertEquals(expectedSource, bundle?.getParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE))
    }

    @Test
    fun `WHEN cropping fails, THEN result should be unsuccessful`() {
        // GIVEN
        var fragment: ContractTestFragment? = null
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                val result = CropImage.ActivityResult(
                    originalUri = null,
                    uriContent = null,
                    error = Exception("Error!"),
                    cropPoints = floatArrayOf(),
                    cropRect = null,
                    rotation = 0,
                    wholeImageRect = null,
                    sampleSize = 0
                )
                val intent = Intent()
                intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result)

                dispatchResult(requestCode, CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE, intent)
            }
        }
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment = it }
        }
        // WHEN
        fragment?.cropImage(options())
        // THEN
        assertEquals(false, fragment?.cropResult?.isSuccessful)
    }
}
