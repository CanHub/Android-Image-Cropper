package com.canhub.cropper

import androidx.core.content.FileProvider

/**
 * Providing a custom {@code FileProvider} prevents manifest {@code <provider>} name collisions.
 *
 * See https://developer.android.com/guide/topics/manifest/provider-element.html for details.
 */
class CropFileProvider : FileProvider() {
    // This class intentionally left blank.
}
