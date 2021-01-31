package com.canhub.cropper.sample.options_dialog.app

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.canhub.cropper.sample.options_dialog.domain.OptionsContract
import com.canhub.cropper.sample.options_dialog.domain.OptionsDomain
import com.example.croppersample.R
import com.example.croppersample.databinding.FragmentOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal class OptionsDialogBottomSheet : BottomSheetDialogFragment(), OptionsContract.View {

    interface Listener {
        fun onOptionsApplySelected()
    }

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            options: OptionsDomain,
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

//    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
//        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

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
//        val myDrawerView = layoutInflater.inflate(R.layout.fragment_options, null)
//        binding = FragmentOptionsBinding.inflate(layoutInflater, myDrawerView as ViewGroup, false)
        binding = FragmentOptionsBinding.inflate(layoutInflater, container, false)
        return binding.root
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
    }

    override fun updateOptions(options: OptionsDomain) {
        binding.activityType.chipDefault.isChecked = true
    }

    override fun close() {
        dialog?.dismiss()
    }
}