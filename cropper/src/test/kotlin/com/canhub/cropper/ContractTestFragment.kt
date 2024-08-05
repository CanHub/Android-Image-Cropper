@file:Suppress("DEPRECATION")

package com.canhub.cropper

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.fragment.app.Fragment

class ContractTestFragment(
  registry: ActivityResultRegistry,
) : Fragment() {

  var cropResult: CropImageView.CropResult? = null

  val cropImage: ActivityResultLauncher<CropImageContractOptions> = registerForActivityResult(CropImageContract(), registry) { result ->
    this.cropResult = result
  }

  fun cropImage(input: CropImageContractOptions) {
    cropImage.launch(input)
  }

  fun cropImageIntent(input: CropImageContractOptions): Intent = cropImage.contract.createIntent(requireContext(), input)
}
