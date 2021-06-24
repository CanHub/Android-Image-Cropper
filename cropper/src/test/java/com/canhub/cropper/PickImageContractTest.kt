package com.canhub.cropper

import android.app.Activity
import android.content.Intent
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
class PickImageContractTest {

    private val testRegistry = object : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?
        ) { }
    }

    @Test
    fun testImagePicking() {
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment ->
                val pickImageIntent = fragment.pickImageIntent(true)

                assertEquals(pickImageIntent.action, Intent.ACTION_CHOOSER)
            }
        }
    }

    @Test
    fun testParsePickResult() {
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment ->
                fragment.pickImageIntent(true)
                val resultIntent = Intent().apply {
                    data = "content://testResult".toUri()
                }
                val result = fragment.pickImage.contract.parseResult(Activity.RESULT_OK, resultIntent)
                assertEquals("content://testResult".toUri(), result)
            }
        }
    }
}
