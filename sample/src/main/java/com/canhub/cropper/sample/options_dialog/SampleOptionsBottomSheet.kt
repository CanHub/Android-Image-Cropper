package com.canhub.cropper.sample.options_dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.canhub.cropper.CropImageView
import com.example.croppersample.databinding.FragmentOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class SampleOptionsBottomSheet : BottomSheetDialogFragment() {

    fun interface Listener {

        fun onOptionsApplySelected(options: SampleOptionsEntity)
    }

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            options: SampleOptionsEntity?,
            listener: Listener
        ) {
            Companion.listener = listener
            SampleOptionsBottomSheet().apply {
                arguments = Bundle().apply { putParcelable(OPTIONS_KEY, options) }
                show(fragmentManager, null)
            }
        }

        private const val DIRECTION_UPWARDS = -1
        private lateinit var listener: Listener
        private const val OPTIONS_KEY = "OPTIONS_KEY"
    }

    private lateinit var binding: FragmentOptionsBinding
    private lateinit var options: SampleOptionsEntity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        options = arguments?.getParcelable(OPTIONS_KEY) ?: defaultOptions
        updateOptions(options)

        bindingActions()
    }

    private fun updateOptions(options: SampleOptionsEntity) {
        when (options.scaleType) {
            CropImageView.ScaleType.CENTER -> binding.scaleType.chipCenter.isChecked = true
            CropImageView.ScaleType.FIT_CENTER -> binding.scaleType.chipFitCenter.isChecked = true
            CropImageView.ScaleType.CENTER_INSIDE ->
                binding.scaleType.chipCenterInside.isChecked = true
            CropImageView.ScaleType.CENTER_CROP -> binding.scaleType.chipCenterCrop.isChecked = true
        }
        when (options.cornerShape) {
            CropImageView.CropCornerShape.RECTANGLE ->
                binding.cornerShape.chipRectangle.isChecked =
                    true
            CropImageView.CropCornerShape.OVAL -> binding.cornerShape.chipOval.isChecked = true
        }

        when (options.cropShape) {
            CropImageView.CropShape.RECTANGLE -> {
                binding.cropShape.chipRectangle.isChecked = true
                // Enabling the corner shape selection functionality only for Rectangle shape cropper for now
                // To expose to other crop shape, we will need to for necessary changes in CropOverlay class
                binding.cornerShape.root.visibility = View.VISIBLE
            }
            CropImageView.CropShape.OVAL -> {
                binding.cropShape.chipOval.isChecked = true
            }
            CropImageView.CropShape.RECTANGLE_VERTICAL_ONLY ->
                binding.cropShape.chipRectangleVerticalOnly.isChecked = true
            CropImageView.CropShape.RECTANGLE_HORIZONTAL_ONLY ->
                binding.cropShape.chipRectangleHorizontalOnly.isChecked = true
        }

        when (options.guidelines) {
            CropImageView.Guidelines.OFF -> binding.guidelines.chipOff.isChecked = true
            CropImageView.Guidelines.ON -> binding.guidelines.chipOn.isChecked = true
            CropImageView.Guidelines.ON_TOUCH -> binding.guidelines.chipOnTouch.isChecked = true
        }

        when (options.ratio) {
            Pair(1, 1) -> binding.ratio.chipOneOne.isChecked = true
            Pair(4, 3) -> binding.ratio.chipFourThree.isChecked = true
            Pair(2, 1) -> binding.ratio.chipTwoOne.isChecked = true
            Pair(16, 9) -> binding.ratio.chipSixteenNine.isChecked = true
            else -> binding.ratio.chipFree.isChecked = true
        }

        when (options.maxZoomLvl) {
            4 -> binding.maxZoom.chipFour.isChecked = true
            8 -> binding.maxZoom.chipEight.isChecked = true
            else -> binding.maxZoom.chipTwo.isChecked = true
        }

        binding.autoZoom.toggle.isChecked = options.autoZoom
        binding.multiTouch.toggle.isChecked = options.multiTouch
        binding.centerMoveEnabled.toggle.isChecked = options.centerMove
        binding.cropOverlay.toggle.isChecked = options.showCropOverlay
        binding.progressBar.toggle.isChecked = options.showProgressBar
        binding.flipHorizontal.toggle.isChecked = options.flipHorizontally
        binding.flipVertical.toggle.isChecked = options.flipVertically
        binding.cropLabelText.toggle.isChecked = options.showCropLabel
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onOptionsApplySelected(options)
        super.onDismiss(dialog)
    }

    private val defaultOptions = SampleOptionsEntity(
        scaleType = CropImageView.ScaleType.FIT_CENTER,
        cropShape = CropImageView.CropShape.RECTANGLE,
        cornerShape = CropImageView.CropCornerShape.RECTANGLE,
        guidelines = CropImageView.Guidelines.ON,
        ratio = Pair(1, 1),
        maxZoomLvl = 2,
        autoZoom = true,
        multiTouch = true,
        centerMove = true,
        showCropOverlay = true,
        showProgressBar = true,
        flipHorizontally = false,
        flipVertically = false,
        showCropLabel = true
    )

    private fun bindingActions() {
        binding.optionsHeader.isSelected = false
        binding.optionsItemsScroll.setOnScrollChangeListener { _, _, _, _, _ ->
            binding.optionsHeader.isSelected =
                binding.optionsItemsScroll.canScrollVertically(DIRECTION_UPWARDS)
        }

        binding.scaleType.chipCenter.setOnClickListener {
            options = options.copy(scaleType = CropImageView.ScaleType.CENTER)
        }

        binding.scaleType.chipCenterCrop.setOnClickListener {
            options = options.copy(scaleType = CropImageView.ScaleType.CENTER_CROP)
        }

        binding.scaleType.chipCenterInside.setOnClickListener {
            options = options.copy(scaleType = CropImageView.ScaleType.CENTER_INSIDE)
        }

        binding.scaleType.chipFitCenter.setOnClickListener {
            options = options.copy(scaleType = CropImageView.ScaleType.FIT_CENTER)
        }

        binding.cropShape.chipRectangle.setOnClickListener {
            options = options.copy(cropShape = CropImageView.CropShape.RECTANGLE)
            binding.cornerShape.root.visibility = View.VISIBLE
        }

        binding.cropShape.chipOval.setOnClickListener {
            options = options.copy(cropShape = CropImageView.CropShape.OVAL)
            binding.cornerShape.root.visibility = View.GONE
        }

        binding.cropShape.chipRectangleVerticalOnly.setOnClickListener {
            options = options.copy(cropShape = CropImageView.CropShape.RECTANGLE_VERTICAL_ONLY)
            binding.cornerShape.root.visibility = View.GONE
        }

        binding.cropShape.chipRectangleHorizontalOnly.setOnClickListener {
            options = options.copy(cropShape = CropImageView.CropShape.RECTANGLE_HORIZONTAL_ONLY)
            binding.cornerShape.root.visibility = View.GONE
        }
        binding.cornerShape.chipRectangle.setOnClickListener {
            options = options.copy(cornerShape = CropImageView.CropCornerShape.RECTANGLE)
        }
        binding.cornerShape.chipOval.setOnClickListener {
            options = options.copy(cornerShape = CropImageView.CropCornerShape.OVAL)
        }

        binding.guidelines.chipOff.setOnClickListener {
            options = options.copy(guidelines = CropImageView.Guidelines.OFF)
        }

        binding.guidelines.chipOn.setOnClickListener {
            options = options.copy(guidelines = CropImageView.Guidelines.ON)
        }

        binding.guidelines.chipOnTouch.setOnClickListener {
            options = options.copy(guidelines = CropImageView.Guidelines.ON_TOUCH)
        }

        binding.ratio.chipFree.setOnClickListener {
            options = options.copy(ratio = null)
        }

        binding.ratio.chipOneOne.setOnClickListener {
            options = options.copy(ratio = Pair(1, 1))
        }

        binding.ratio.chipTwoOne.setOnClickListener {
            options = options.copy(ratio = Pair(2, 1))
        }

        binding.ratio.chipFourThree.setOnClickListener {
            options = options.copy(ratio = Pair(4, 3))
        }

        binding.ratio.chipSixteenNine.setOnClickListener {
            options = options.copy(ratio = Pair(16, 9))
        }

        binding.maxZoom.chipTwo.setOnClickListener {
            options = options.copy(maxZoomLvl = 2)
        }

        binding.maxZoom.chipFour.setOnClickListener {
            options = options.copy(maxZoomLvl = 4)
        }

        binding.maxZoom.chipEight.setOnClickListener {
            options = options.copy(maxZoomLvl = 8)
        }

        binding.autoZoom.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(autoZoom = isChecked)
        }

        binding.cropOverlay.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(showCropOverlay = isChecked)
        }

        binding.flipHorizontal.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(flipHorizontally = isChecked)
        }

        binding.flipVertical.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(flipVertically = isChecked)
        }

        binding.multiTouch.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(multiTouch = isChecked)
        }

        binding.centerMoveEnabled.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(centerMove = isChecked)
        }

        binding.progressBar.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(showProgressBar = isChecked)
        }

        binding.cropLabelText.toggle.setOnCheckedChangeListener { _, isChecked ->
            options = options.copy(showCropLabel = isChecked)
        }
    }
}
