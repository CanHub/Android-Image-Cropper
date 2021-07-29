package com.canhub.cropper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.canhub.cropper.CropImage.getPickImageResultUriContent

/**
 * An ActivityResultContract to prompt the user to pick an image, receiving
 * a Uri for that image that allows you to use
 *
 * android.content.ContentResolver#openInputStream(Uri) to access the raw data.
 *
 * Set the boolean input flag to true to include the camera in the options presented to the user.
 *
 * If you want to customize how the result is parsed, extend this class and override parseResult
*/

open class PickImageContract : ActivityResultContract<Boolean, Uri?>() {

    protected var context: Context? = null

    override fun createIntent(context: Context, input: Boolean): Intent {
        this.context = context

        return CropImage.getPickImageChooserIntent(
            context = context,
            title = context.getString(R.string.pick_image_intent_chooser_title),
            includeDocuments = false,
            includeCamera = input
        )
    }

    open override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): Uri? =
        when (resultCode) {
            Activity.RESULT_CANCELED -> {
                context = null
                null
            }
            else -> {
                context?.let {
                    context = null
                    getPickImageResultUriContent(it, intent)
                }
            }
        }
}
