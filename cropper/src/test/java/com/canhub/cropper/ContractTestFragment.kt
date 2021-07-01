package com.canhub.cropper

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.fragment.app.Fragment

class ContractTestFragment(
    registry: ActivityResultRegistry
) : Fragment() {

    var cropResult: CropImageView.CropResult? = null
    var pickResult: Uri? = null

    val cropImage = registerForActivityResult(CropImageContract(), registry) { result ->
        this.cropResult = result
    }

    val pickImage = registerForActivityResult(PickImageContract(), registry) { result ->
        this.pickResult = result
    }

    fun cropImage(input: CropImageContractOptions) {
        cropImage.launch(input)
    }

    fun cropImageIntent(input: CropImageContractOptions): Intent {
        return cropImage.contract.createIntent(requireContext(), input)
    }

    fun pickImageIntent(input: Boolean): Intent {
        return pickImage.contract.createIntent(requireContext(), input)
    }
}
