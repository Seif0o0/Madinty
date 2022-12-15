package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentHelpCenterBinding
import com.madinaty.app.databinding.FragmentMoreBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.viewmodel.HelpCenterViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HelpCenterFragment : Fragment(R.layout.fragment_help_center) {
    private val viewModel: HelpCenterViewModel by viewModels()
    private lateinit var binding: FragmentHelpCenterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHelpCenterBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        binding.sendBtn.setOnClickListener {
            viewModel.onSendBtnClicked()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.startSendMessage.collectLatest {
                if (it) {
                    viewModel.sendMessage()
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
            viewModel.sendMessageState.collectLatest {
                if (it) {
                    CustomDialog.showSuccessDialog(
                        context =  requireContext(),
                        successMessage = getString(R.string.send_message_successful_message)
                    )
                    viewModel.sendMessageState(false)
                }
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}