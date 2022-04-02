package com.canhub.cropper

import android.net.Uri

sealed class CropException(message: String) : Exception(message) {
    class Cancellation : CropException("$EXCEPTION_PREFIX cropping has been cancelled by the user")
    class FailedToLoadBitmap(uri: Uri, message: String?) :
        CropException("$EXCEPTION_PREFIX Failed to load sampled bitmap: $uri\r\n$message")

    class FailedToDecodeImage(uri: Uri) :
        CropException("$EXCEPTION_PREFIX Failed to decode image: $uri")

    companion object {

        const val EXCEPTION_PREFIX = "crop:"
    }
}
