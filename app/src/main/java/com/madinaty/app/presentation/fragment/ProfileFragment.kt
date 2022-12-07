package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentProfileBinding
import com.madinaty.app.domain.model.User
import com.madinaty.app.presentation.activity.AuthActivity
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.ProfileViewModel
import com.madinaty.app.utils.CustomDialog
import com.madinaty.app.utils.logUserOut
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var binding: FragmentProfileBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentProfileBinding.bind(view)
        binding.retryListener = RetryClickListener {
            viewModel.getUserInfo()
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editInfo.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(viewModel.userInfo.value!!)
            )
        }

        binding.logout.setOnClickListener {
            CustomDialog.showLogoutDialog(context = requireContext(), onYesBtnClicked = ::logout)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.userInfo.collectLatest {
                binding.userInfo = it
            }
        }
    }

    private fun logout(view: View?) {
        logUserOut()
        startActivity(Intent(requireActivity(), AuthActivity::class.java))
        requireActivity().finish()
    }
}