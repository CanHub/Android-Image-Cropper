package com.canhub.cropper

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.canhub.cropper.CropImage.getPickImageResultUriContent

class OpenChooser: ActivityResultContract<Boolean, Uri?>() {

    private var context: Context? = null

    override fun createIntent(context: Context, input: Boolean): Intent {

        this.context = context

        return CropImage.getPickImageChooserIntent(
            context = context,
            title = context.getString(R.string.pick_image_intent_chooser_title),
            includeDocuments = false,
            includeCamera = input
        )
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): Uri? {
        if (intent != null) {
            context?.let {
                context = null
                return getPickImageResultUriContent(it, intent)
            }
        }
        context = null
        return null
    }
}
