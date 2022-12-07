package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.madinaty.app.R
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.databinding.FragmentPhoneVerificationBinding
import com.madinaty.app.databinding.LoginSuccessDialogLayoutBinding

class VerificationFragment:Fragment() {
    private lateinit var binding:FragmentPhoneVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(!::binding.isInitialized)
            binding = FragmentPhoneVerificationBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.confirmBtn.setOnClickListener {  showSuccessDialog()}
    }

    private fun showSuccessDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            cancelable(false)
            customView(
                R.layout.login_success_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = LoginSuccessDialogLayoutBinding.bind(dialog.getCustomView())

        dialogBinding.thanksBtn.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }


    }
}