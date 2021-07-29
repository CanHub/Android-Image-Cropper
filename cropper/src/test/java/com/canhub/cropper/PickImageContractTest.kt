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
    fun `GIVEN pick image contract, WHEN starting image pick, THEN intent action should be ACTION_CHOOSER`() {
        // GIVEN
        val expected = Intent.ACTION_CHOOSER
        var fragment: ContractTestFragment? = null
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment = it }
        }

        // WHEN
        val result = fragment?.pickImageIntent(true)?.action

        // THEN
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN pick image contract, WHEN parsing image pick, THEN result correct uri should be returned`() {
        // GIVEN
        var fragment: ContractTestFragment? = null
        val expected = "content://testResult".toUri()
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { fragment = it }
        }

        // WHEN
        fragment?.pickImageIntent(true)
        val resultIntent = Intent().apply { data = expected }
        val result = fragment?.pickImage?.contract?.parseResult(Activity.RESULT_OK, resultIntent)

        // THEN
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN pick image contract and previous image selection, WHEN cancelling image pick, THEN no uri should be returned`() {
        // GIVEN
        var firstFragment: ContractTestFragment? = null
        var secondFragment: ContractTestFragment? = null
        val firstSelection = "content://testResult".toUri()
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { firstFragment = it }
        }
        firstFragment?.pickImageIntent(true)
        firstFragment?.pickImage?.contract?.parseResult(Activity.RESULT_OK, Intent().apply { data = firstSelection })
        with(launchFragmentInContainer { ContractTestFragment(testRegistry) }) {
            onFragment { secondFragment = it }
        }

        // WHEN
        secondFragment?.pickImageIntent(true)
        val resultIntent = Intent()
        val result = secondFragment?.pickImage?.contract?.parseResult(Activity.RESULT_CANCELED, resultIntent)

        // THEN
        assertEquals(null, result)
    }
}
