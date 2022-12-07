package com.madinaty.app.presentation.fragment

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
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentPhoneLoginBinding
import com.madinaty.app.databinding.LoginSuccessDialogLayoutBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.viewmodel.PhoneLoginViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PhoneLoginFragment : Fragment() {
    private val viewModel: PhoneLoginViewModel by viewModels()

    private lateinit var binding: FragmentPhoneLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneLoginBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.onLoginBtnClicked()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.startLogging.collectLatest {
                if (it) {
                    viewModel.login()
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
            viewModel.loginState.collectLatest {
                if (it) {
                    showSuccessDialog()
                    viewModel.loginState(false)
                }
            }
        }
    }


    private fun showSuccessDialog(
        dialogBehavior: DialogBehavior = ModalDialog,
    ) {
        val dialog = MaterialDialog(requireContext(), dialogBehavior).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelable(false)
            customView(
                R.layout.login_success_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = LoginSuccessDialogLayoutBinding.bind(dialog.getCustomView())
        dialogBinding.successAnimation.setAnimation("success.json")
        dialogBinding.successAnimation.playAnimation()

        dialogBinding.thanksBtn.setOnClickListener {
            dialogBinding.successAnimation.cancelAnimation()
            dialog.dismiss()
            /* comment till wessam setup otp */
//                    findNavController().navigate(
//                        PhoneLoginFragmentDirections.actionPhoneLoginFragmentToVerificationFragment()
//                    )
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }


}