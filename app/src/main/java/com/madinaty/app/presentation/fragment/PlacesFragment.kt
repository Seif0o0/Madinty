package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.databinding.FragmentPlacesBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.*
import com.madinaty.app.presentation.viewmodel.PinOffersViewModel
import com.madinaty.app.presentation.viewmodel.PlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PlacesFragment : Fragment() {
    private val viewModel: PlacesViewModel by viewModels()
    private val pinOffersViewModel: PinOffersViewModel by viewModels()
    private lateinit var binding: FragmentPlacesBinding

    lateinit var placesAdapter: PlacesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlacesBinding.inflate(layoutInflater, container, false)
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        val args = PlacesFragmentArgs.fromBundle(requireArguments())
        binding.titleString = args.title
        binding.viewModel = pinOffersViewModel
        binding.offersRetryListener = RetryClickListener {
            pinOffersViewModel.getOffers()
        }
        binding.lifecycleOwner = requireActivity()

        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        placesAdapter = PlacesAdapter(clickListener = ListItemClickListener {
            findNavController().navigate(
                PlacesFragmentDirections.actionPlacesFragmentToPlaceDetailsFragment(
                    it
                )
            )
        })

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        placesAdapter.addLoadStateListener { combinedLoadStates ->
            if (combinedLoadStates.refresh is LoadState.NotLoading) {
                binding.emptyListText.visibility = if (placesAdapter.itemCount == 0) {
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
                    placesAdapter.retry()
                }
            } else {
                binding.errorState = false
                binding.retryListener = null
            }
        }

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placesAdapter.withLoadStateFooter(
                footer = ListsLoadStateAdapter(RetryClickListener {
                    placesAdapter.retry()
                })
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.places.collectLatest {
                placesAdapter.submitData(it)
            }
        }

        /* offers slider part */
        val offersAdapter = PinOffersAdapter(clickListener = ListItemClickListener {

        })
        binding.viewPager.adapter = offersAdapter


        lifecycleScope.launchWhenStarted {
            pinOffersViewModel.pinOffers.collectLatest {
                offersAdapter.submitList(it)
                binding.viewPagerIndicator.setViewPager2(binding.viewPager)
            }
        }


        return binding.root
    }

}