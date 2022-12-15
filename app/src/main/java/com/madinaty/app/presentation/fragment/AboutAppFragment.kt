package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentAboutAppBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.CommonInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AboutAppFragment : Fragment(R.layout.fragment_about_app) {
    private val viewModel: CommonInfoViewModel by viewModels()
    private lateinit var binding: FragmentAboutAppBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentAboutAppBinding.bind(view)
        binding.viewModel = viewModel
        binding.retryListener = RetryClickListener {
            viewModel.getCommonInfo()
        }
        binding.lifecycleOwner = requireActivity()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.commonInfo.collectLatest {
                binding.aboutUs = if (it == null) {
                    ""
                } else {
                    it.aboutUs.ifEmpty { getString(R.string.empty_list_text) }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}