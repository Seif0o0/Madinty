package com.madinaty.app.presentation.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.madinaty.app.R
import com.madinaty.app.databinding.ConfirmPhoneDialogLayoutBinding
import com.madinaty.app.databinding.FragmentEditProfileBinding
import com.madinaty.app.presentation.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.widget.DatePicker;
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.madinaty.app.utils.CustomDialog
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile),
    DatePickerDialog.OnDateSetListener {
    private val viewModel: EditProfileViewModel by viewModels()

    private lateinit var binding: FragmentEditProfileBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentEditProfileBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()


        binding.backBtn.setOnClickListener {
            navigateBack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })

        binding.birthadteEdittext.setOnClickListener {
            showDatePicker(binding.birthadteEdittext.text.toString())
        }

        binding.saveBtn.setOnClickListener {
            viewModel.onSaveBtnClicked()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.startUpdate.collectLatest {
                if (it) {
                    viewModel.updateUserInfo()
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
            viewModel.updateInfoState.collectLatest {
                if (it) {
                    CustomDialog.showSuccessDialog(
                        context = requireContext(),
                        successMessage = getString(R.string.edit_profile_successful_message)
                    )
                    viewModel.updateInfoState(false)
                }
            }
        }
    }

    private fun navigateBack() {
        setFragmentResult("EditProfileReqKey", bundleOf("updated" to true))
        findNavController().popBackStack()
    }

    private val calendar = Calendar.getInstance()
    private fun showDatePicker(dateOfBirth: String) {
        val day: Int
        val month: Int
        val year: Int
        if (dateOfBirth.isEmpty()) {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        } else {

            val splitDate = dateOfBirth.split("-")
            year = splitDate[0].toInt()
            month = splitDate[1].toInt() - 1
            day = splitDate[2].toInt()
        }

        val datePicker = DatePickerDialog(
            requireContext(),
            this,
            year,
            month,
            day,
        )
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -18)
        datePicker.datePicker.maxDate = maxDate.timeInMillis
        datePicker.show()

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date =
            "$year-${viewModel.convertToShortMonth(month)}-${if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth}"
        viewModel.dobState.value = date
    }
}