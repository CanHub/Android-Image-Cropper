package com.canhub.cropper.common

import android.os.Build
import android.os.Build.VERSION.SDK_INT

object CommonVersionCheck {

    fun isAtLeastM23() = SDK_INT >= Build.VERSION_CODES.M
    fun isAtLeastO26() = SDK_INT >= Build.VERSION_CODES.O
    fun isAtLeastQ29() = SDK_INT >= Build.VERSION_CODES.Q
}
