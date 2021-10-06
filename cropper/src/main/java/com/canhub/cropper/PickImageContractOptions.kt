package com.canhub.cropper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A data class to specify where the user can
 * select images from, there are 2 options
 * @param includeGallery,
 * @param includeCamera,
 * to include from a particular source just set its
 * attribute to true, by default Gallery and Camera are
 * always included, to exclude one of them, set it to
 * false*/
@Parcelize
data class PickImageContractOptions @JvmOverloads constructor(
    var includeGallery: Boolean = true,
    var includeCamera: Boolean = true
) : Parcelable
