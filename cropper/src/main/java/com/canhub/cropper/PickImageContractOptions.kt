package com.canhub.cropper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A data class to specify where the user can
 * select images from, there are 3 options
 * @param includeGallery,
 * @param includeCamera, and
 * @param includeDocuments,
 * to include from a particular source just set its
 * attribute to true, by default Gallery is always
 * included and documents excluded, so all the user must
 * always specify is whether to include camera or not*/
@Parcelize
data class PickImageContractOptions @JvmOverloads constructor(
    val includeGallery: Boolean = true,
    val includeCamera: Boolean,
    val includeDocuments: Boolean = false
) : Parcelable