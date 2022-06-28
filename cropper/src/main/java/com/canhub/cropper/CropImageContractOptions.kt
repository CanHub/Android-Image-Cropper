package com.canhub.cropper

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Options to customize the activity opened by CropImageContract.
 */
@Parcelize data class CropImageContractOptions(
    val uri: Uri?,
    val cropImageOptions: CropImageOptions,
) : Parcelable
