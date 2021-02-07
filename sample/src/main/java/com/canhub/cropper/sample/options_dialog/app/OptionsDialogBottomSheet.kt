package com.canhub.cropper.sample.options_dialog.app

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.canhub.cropper.CropImageView
import com.canhub.cropper.sample.options_dialog.domain.OptionsActivityEnum
import com.canhub.cropper.sample.options_dialog.domain.OptionsContract
import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain
import com.example.croppersample.databinding.FragmentOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class OptionsDialogBottomSheet : BottomSheetDialogFragment(), OptionsContract.View {

    interface Listener {
        fun onOptionsApplySelected(options: OptionsDomain)
    }

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            options: OptionsDomain?,
            listener: Listener
        ) {
            this.listener = listener
            OptionsDialogBottomSheet().apply {
                arguments = Bundle().apply { putParcelable(OPTIONS_KEY, options) }
                show(fragmentManager, null)
            }
        }

        private const val DIRECTION_UPWARDS = -1
        private lateinit var listener: Listener
        private const val OPTIONS_KEY = "OPTIONS_KEY"
    }

    private lateinit var presenter: OptionsContract.Presenter
    private lateinit var binding: FragmentOptionsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val serviceLocator = OptionsServiceLocator(context)
        presenter = serviceLocator.getPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        presenter.onDismiss()
        super.onDismiss(dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bind(this)
        val options = arguments?.getParcelable<OptionsDomain>(OPTIONS_KEY)

        presenter.onViewCreated(options)

        binding.optionsHeader.isSelected = false
        binding.optionsItemsScroll.setOnScrollChangeListener { _, _, _, _, _ ->
            binding.optionsHeader.isSelected =
                binding.optionsItemsScroll.canScrollVertically(DIRECTION_UPWARDS)
        }

        binding.activityType.chipDefault.setOnClickListener {
            presenter.onActivityTypeSelect(OptionsActivityEnum.DEFAULT)
        }

        binding.activityType.chipCustom.setOnClickListener {
            presenter.onActivityTypeSelect(OptionsActivityEnum.CUSTOM)
        }

        binding.scaleType.chipCenter.setOnClickListener {
            presenter.onScaleTypeSelect(CropImageView.ScaleType.CENTER)
        }

        binding.scaleType.chipCenterCrop.setOnClickListener {
            presenter.onScaleTypeSelect(CropImageView.ScaleType.CENTER_CROP)
        }

        binding.scaleType.chipCenterInside.setOnClickListener {
            presenter.onScaleTypeSelect(CropImageView.ScaleType.CENTER_INSIDE)
        }

        binding.scaleType.chipFitCenter.setOnClickListener {
            presenter.onScaleTypeSelect(CropImageView.ScaleType.FIT_CENTER)
        }

        binding.cropShape.chipRectangle.setOnClickListener {
            presenter.onCropShapeSelect(CropImageView.CropShape.RECTANGLE)
        }

        binding.cropShape.chipOval.setOnClickListener {
            presenter.onCropShapeSelect(CropImageView.CropShape.OVAL)
        }

        binding.guidelines.chipOff.setOnClickListener {
            presenter.onGuidelinesSelect(CropImageView.Guidelines.OFF)
        }

        binding.guidelines.chipOn.setOnClickListener {
            presenter.onGuidelinesSelect(CropImageView.Guidelines.ON)
        }

        binding.guidelines.chipOnTouch.setOnClickListener {
            presenter.onGuidelinesSelect(CropImageView.Guidelines.ON_TOUCH)
        }

        binding.ratio.chipFree.setOnClickListener {
            presenter.onRatioSelect(null)
        }

        binding.ratio.chipOneOne.setOnClickListener {
            presenter.onRatioSelect(Pair(1, 1))
        }

        binding.ratio.chipFourThree.setOnClickListener {
            presenter.onRatioSelect(Pair(16, 9))
        }

        binding.ratio.chipSixteenNine.setOnClickListener {
            presenter.onRatioSelect(Pair(9, 16))
        }

        binding.maxZoom.chipTwo.setOnClickListener {
            presenter.onMaxZoomLvlSelect(2)
        }

        binding.maxZoom.chipFour.setOnClickListener {
            presenter.onMaxZoomLvlSelect(4)
        }

        binding.maxZoom.chipEight.setOnClickListener {
            presenter.onMaxZoomLvlSelect(8)
        }

        binding.autoZoom.toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onAutoZoomSelect(isChecked)
        }

        binding.cropOverlay.toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onCropOverlaySelect(isChecked)
        }

        binding.flipHorizontal.toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onFlipHorizontalSelect(isChecked)
        }

        binding.flipVertical.toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onFlipVerticallySelect(isChecked)
        }

        binding.multiTouch.toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onMultiTouchSelect(isChecked)
        }

        binding.progressBar.toggle.setOnCheckedChangeListener { _, isChecked ->
            presenter.onProgressBarSelect(isChecked)
        }
    }

    override fun updateOptions(options: OptionsDomain) {
        // todo #46
        binding.activityType.chipDefault.isChecked = true
//        when (options.activityType) {
//            OptionsActivityEnum.DEFAULT -> binding.activityType.chipDefault.isChecked = true
//            OptionsActivityEnum.CUSTOM -> binding.activityType.chipCustom.isChecked = true
//        }
        when (options.scaleType) {
            CropImageView.ScaleType.CENTER -> binding.scaleType.chipCenter.isChecked = true
            CropImageView.ScaleType.FIT_CENTER -> binding.scaleType.chipFitCenter.isChecked = true
            CropImageView.ScaleType.CENTER_INSIDE ->
                binding.scaleType.chipCenterInside.isChecked = true
            CropImageView.ScaleType.CENTER_CROP -> binding.scaleType.chipCenterCrop.isChecked = true
        }

        when (options.cropShape) {
            CropImageView.CropShape.RECTANGLE -> binding.cropShape.chipRectangle.isChecked = true
            CropImageView.CropShape.OVAL -> binding.cropShape.chipOval.isChecked = true
        }

        when (options.guidelines) {
            CropImageView.Guidelines.OFF -> binding.guidelines.chipOff.isChecked = true
            CropImageView.Guidelines.ON -> binding.guidelines.chipOn.isChecked = true
            CropImageView.Guidelines.ON_TOUCH -> binding.guidelines.chipOnTouch.isChecked = true
        }

        when (options.ratio) {
            Pair(1, 1) -> binding.ratio.chipOneOne.isChecked = true
            Pair(4, 3) -> binding.ratio.chipFourThree.isChecked = true
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
        binding.cropOverlay.toggle.isChecked = options.showCropOverlay
        binding.progressBar.toggle.isChecked = options.showProgressBar
        binding.flipHorizontal.toggle.isChecked = options.flipHorizontal
        binding.flipVertical.toggle.isChecked = options.flipVertically
    }

    override fun closeWithResult(options: OptionsDomain) {
        listener.onOptionsApplySelected(options)
    }

    override fun onDestroy() {
        presenter.unbind()
        super.onDestroy()
    }

    override fun activityCustomNotImplementedMessage() {
        binding.activityType.chipDefault.isChecked = true
        Toast.makeText(context, "Not implemented yet, check Issue #46", Toast.LENGTH_LONG).show()
    }
}