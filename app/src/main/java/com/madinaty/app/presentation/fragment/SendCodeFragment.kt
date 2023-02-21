package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madinaty.app.databinding.FragmentSendCodeBinding
import com.madinaty.app.presentation.viewmodel.SendCodeViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SendCodeFragment : Fragment() {
    private val viewModel: SendCodeViewModel by viewModels()

    private lateinit var binding: FragmentSendCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendCodeBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.onSendBtnClicked()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.startSendCode.collectLatest {
                if (it) {
                    viewModel.sendCode()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.errorState.collectLatest {
                if (it.isNotEmpty()) {
                    CustomDialog.showErrorDialog(
                        context = requireContext(), errorMessage = it
                    )
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.sendCodeState.collectLatest {
                if (it) {
                    findNavController().navigate(
                        SendCodeFragmentDirections.actionSendCodeFragmentToVerificationFragment(
                            userId = viewModel.userId,
                            phoneNumber = viewModel.phoneNumberState.value!!
                        )
                    )
                    viewModel.sendCodeState(false)
                }
            }
        }

        return binding.root
    }
}