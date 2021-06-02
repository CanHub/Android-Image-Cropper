package com.canhub.cropper.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

internal fun getFragmentActivityFrom(context: Context): FragmentActivity {
    var unpackContext = context
    while (unpackContext !is FragmentActivity && unpackContext is ContextWrapper) {
        unpackContext = unpackContext.baseContext
    }

    return unpackContext as FragmentActivity
}
