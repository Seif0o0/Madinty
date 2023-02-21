package com.madinaty.app.presentation.fragment

import `in`.aabhasjindal.otptextview.OTPListener
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentOtpVerificationBinding
import com.madinaty.app.presentation.activity.MainActivity

import com.madinaty.app.databinding.LoginSuccessDialogLayoutBinding
import com.madinaty.app.presentation.viewmodel.OtpVerificationViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OtpVerificationFragment : Fragment() {
    private val viewModel: OtpVerificationViewModel by viewModels()

    private lateinit var binding: FragmentOtpVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)
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


        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                viewModel.codeErrorState.value = ""
            }

            override fun onOTPComplete(otp: String) {
            }
        }
        binding.confirmBtn.setOnClickListener {
            viewModel.onVerifyBtnClicked(
                code = binding.otpView.otp ?: ""
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.startVerifyCode.collectLatest {
                if (it) {
                    viewModel.verifyCode()
                }
            }
        }

        binding.resendCode.setOnClickListener {
            viewModel.startReSendCode(true)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.startReSendCode.collectLatest {
                if (it) {
                    viewModel.reSendCode()
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
            viewModel.verifyCodeState.collectLatest {
                if (it) {
                    viewModel.verifyCodeState(false)
                    findNavController().navigate(
                        OtpVerificationFragmentDirections.actionVerificationFragmentToRegisterFragment(
                            userId = OtpVerificationFragmentArgs.fromBundle(requireArguments()).userId
                        )
                    )
                }
            }
        }
        return binding.root
    }


}