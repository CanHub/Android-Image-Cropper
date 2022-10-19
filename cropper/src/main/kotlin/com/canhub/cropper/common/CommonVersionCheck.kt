package com.canhub.cropper.common

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.annotation.ChecksSdkIntAtLeast

object CommonVersionCheck {
  @ChecksSdkIntAtLeast(api = VERSION_CODES.O) fun isAtLeastO26() = SDK_INT >= VERSION_CODES.O
  @ChecksSdkIntAtLeast(api = VERSION_CODES.Q) fun isAtLeastQ29() = SDK_INT >= VERSION_CODES.Q
}
