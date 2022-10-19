package com.canhub.cropper

import android.net.Uri

sealed class CropException(message: String) : Exception(message) {
  class Cancellation : CropException("$EXCEPTION_PREFIX cropping has been cancelled by the user") {
    companion object {
      private const val serialVersionUID: Long = -6896269134508601990L
    }
  }

  class FailedToLoadBitmap(uri: Uri, message: String?) : CropException("$EXCEPTION_PREFIX Failed to load sampled bitmap: $uri\r\n$message") {
    companion object {
      private const val serialVersionUID: Long = 7791142932960927332L
    }
  }

  class FailedToDecodeImage(uri: Uri) : CropException("$EXCEPTION_PREFIX Failed to decode image: $uri") {
    companion object {
      private const val serialVersionUID: Long = 3516154387706407275L
    }
  }

  companion object {

    private const val serialVersionUID: Long = 4933890872862969613L
    const val EXCEPTION_PREFIX = "crop:"
  }
}
