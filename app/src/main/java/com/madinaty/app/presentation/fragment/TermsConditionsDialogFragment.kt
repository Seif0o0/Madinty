package com.madinaty.app.presentation.fragment

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.madinaty.app.R
import com.madinaty.app.databinding.TermsConditionsLayoutBinding
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.CommonInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TermsConditionsDialogFragment : DialogFragment() {
    private val viewModel: CommonInfoViewModel by viewModels()
    private lateinit var binding: TermsConditionsLayoutBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_Material_Light_Dialog_NoMinWidth)
    }

    override fun onResume() {
        super.onResume()
        val displayMetrics = Resources.getSystem().displayMetrics
        val rect = displayMetrics.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentageWidth = rect.width() * (80f / 100)
        val percentageHeight = rect.height() * (60f / 100)
        dialog?.window?.setLayout(percentageWidth.toInt(), percentageHeight.toInt())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TermsConditionsLayoutBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.retryListener = RetryClickListener { viewModel.getCommonInfo() }
        binding.lifecycleOwner = requireActivity()

        binding.dismissIcon.setOnClickListener {
            dismiss()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.commonInfo.collectLatest {
                binding.termsConditionsString = if (it == null) {
                    ""
                } else {
                    it.termsAndConditions.ifEmpty { getString(R.string.empty_list_text) }
                }
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = TermsConditionsDialogFragment()
    }

}