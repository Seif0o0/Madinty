package com.madinaty.app.presentation.fragment

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.R
import com.madinaty.app.databinding.RegionsLayoutBinding
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.ListsLoadStateAdapter
import com.madinaty.app.presentation.adapter.RegionsAdapter
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.RegionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegionsDialogFragment : DialogFragment() {
    private val viewModel: RegionsViewModel by viewModels()
    private lateinit var binding: RegionsLayoutBinding

    private lateinit var regionsAdapter: RegionsAdapter
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_Material_Light_Dialog_NoMinWidth)
    }

    override fun onResume() {
        super.onResume()
        val displayMetrics = Resources.getSystem().displayMetrics
        val rect = displayMetrics.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentageWidth = rect.width() * (80f / 100)
        val percentageHeight = rect.height() * (70f / 100)
        dialog?.window?.setLayout(percentageWidth.toInt(), percentageHeight.toInt())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegionsLayoutBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        binding.lifecycleOwner = requireActivity()

        regionsAdapter = RegionsAdapter(clickListener = ListItemClickListener {
            setFragmentResult(
                requireArguments().getString("requestKey", ""),
                bundleOf("region" to it)
            )
            dismiss()
        })

        regionsAdapter.addLoadStateListener { combinedLoadStates ->
            if (combinedLoadStates.refresh is LoadState.NotLoading) {
                binding.emptyListText.visibility = if (regionsAdapter.itemCount == 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            binding.loadingState = combinedLoadStates.refresh is LoadState.Loading
            if (combinedLoadStates.refresh is LoadState.Error) {
                var error = (combinedLoadStates.refresh as LoadState.Error).error.localizedMessage
                binding.errorState = true
                binding.errorMessage = error
                binding.retryListener = RetryClickListener {
                    regionsAdapter.retry()
                }
            } else {
                binding.errorState = false
                binding.retryListener = null
            }
        }

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = regionsAdapter.withLoadStateFooter(
                footer = ListsLoadStateAdapter(RetryClickListener {
                    regionsAdapter.retry()
                })
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.regions.collectLatest {
                regionsAdapter.submitData(it)
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(requestKey: String) = RegionsDialogFragment().apply {
            arguments = Bundle().apply {
                putString("requestKey", requestKey)
            }
        }
    }
}