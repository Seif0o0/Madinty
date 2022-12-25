package com.madinaty.app.presentation.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
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
import com.madinaty.app.databinding.FragmentRegisterBinding
import com.madinaty.app.databinding.LoginSuccessDialogLayoutBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.viewmodel.RegisterViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@AndroidEntryPoint
class RegisterFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

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
            viewModel.onRegisterBtnClicked()
        }

        binding.birthdateEdittext.setOnClickListener {
            showDatePicker()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.startRegister.collectLatest {
                if (it) {
                    viewModel.registerUser()
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
            viewModel.registerState.collectLatest {
                if (it) {
                    showSuccessDialog()
                    viewModel.registerState(false)
                }
            }
        }
        return binding.root
    }

    private val calendar = Calendar.getInstance()
    private fun showDatePicker() {
        val pickedDate = binding.birthdateEdittext.text.toString()

        val dateTriple = if (pickedDate.isEmpty()) {
            Triple(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            val splitDate = pickedDate.split("-")
            Triple(splitDate[0].toInt(), splitDate[1].toInt(), splitDate[2].toInt())
        }

        val datePicker = DatePickerDialog(
            requireContext(),
            this,
            dateTriple.first,
            dateTriple.second,
            dateTriple.third
        )

        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -18)
        datePicker.datePicker.maxDate = maxDate.timeInMillis
        datePicker.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        binding.birthdateEdittext.setText("$year-${month + 1}-$dayOfMonth")
        viewModel.dobErrorState.value = ""
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