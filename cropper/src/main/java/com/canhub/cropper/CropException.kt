package com.canhub.cropper

import android.net.Uri

sealed class CropException(message: String) : Exception(message) {
    class CancellationException : CropException("cropping has been cancelled by the user")
    class FailedToLoadBitmapException(uri: Uri, message: String?) :
        CropException("Failed to load sampled bitmap: $uri\r\n$message")
    class FailedToDecodeImageException(uri: Uri) : CropException("Failed to decode image: $uri")
}
