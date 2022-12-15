package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentOffersBinding
import com.madinaty.app.presentation.adapter.ListsLoadStateAdapter
import com.madinaty.app.presentation.adapter.OffersAdapter
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.OffersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class OffersFragment : Fragment() {

    private val viewModel: OffersViewModel by viewModels()

    private lateinit var binding: FragmentOffersBinding

    @Inject
    lateinit var offersAdapter: OffersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOffersBinding.inflate(layoutInflater, container, false)
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        binding.lifecycleOwner = requireActivity()

        binding.swipeRefresh.setColorSchemeResources(
            R.color.auth_screens_main_color)

        binding.swipeRefresh.setOnRefreshListener {
            offersAdapter.refresh()
        }

        offersAdapter.addLoadStateListener { combinedLoadStates ->
            if (combinedLoadStates.refresh is LoadState.NotLoading) {
                binding.emptyListText.visibility = if (offersAdapter.itemCount == 0) {
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
                    offersAdapter.retry()
                }
            } else {
                binding.errorState = false
                binding.retryListener = null
            }
        }

        binding.list.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = offersAdapter.withLoadStateFooter(
                footer = ListsLoadStateAdapter(RetryClickListener { offersAdapter.retry() })
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.offers.collectLatest {
                if(binding.swipeRefresh.isRefreshing){
                    binding.swipeRefresh.isRefreshing = false
                }
                offersAdapter.submitData(it)
            }
        }
        return binding.root
    }
}