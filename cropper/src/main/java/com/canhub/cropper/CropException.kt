package com.canhub.cropper

sealed class CropException(message: String) : Exception(message) {
    class CancellationException : CropException("cropping has been cancelled by the user")
}
