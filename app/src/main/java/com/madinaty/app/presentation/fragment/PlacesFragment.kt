package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.madinaty.app.databinding.FragmentPlacesBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.*
import com.madinaty.app.presentation.viewmodel.AddRemoveFavouriteViewModel
import com.madinaty.app.presentation.viewmodel.PinOffersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PlacesFragment : Fragment() {
    //    private val viewModel: PlacesViewModel by viewModels()
    private val pinOffersViewModel: PinOffersViewModel by viewModels()
    private val addRemoveFavouriteViewModel: AddRemoveFavouriteViewModel by viewModels()
    private lateinit var binding: FragmentPlacesBinding

//    lateinit var pagingPlacesAdapter: PagingPlacesAdapter

    private lateinit var placesAdapter: PlacesAdapter

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
//            pinOffersViewModel.getOffers()
        }
        binding.lifecycleOwner = requireActivity()

        val places = args.places?.toMutableList()
        if (places.isNullOrEmpty()) {
            binding.emptyListText.visibility = View.VISIBLE
        } else {
            binding.emptyListText.visibility = View.GONE
            placesAdapter = PlacesAdapter(clickListener = ListItemClickListener {
                findNavController().navigate(
                    PlacesFragmentDirections.actionPlacesFragmentToPlaceDetailsFragment(
                        it
                    )
                )
            }, favClickListener = ListItemClickListener {
                addRemoveFavouriteViewModel.startAddRemoveFavouriteState(true, it)
            })
            placesAdapter.submitList(places)

            binding.list.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = placesAdapter
            }
        }




        lifecycleScope.launchWhenStarted {
            addRemoveFavouriteViewModel.addRemoveFavouriteState.collectLatest {
                if (it) {
                    addRemoveFavouriteViewModel.placeId?.let { placeId ->
                        placesAdapter.updatePlace(placeId)
                    }
                    addRemoveFavouriteViewModel.startAddRemoveFavouriteState(false)
                    addRemoveFavouriteViewModel.addRemoveFavouriteState(false)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            addRemoveFavouriteViewModel.startAddRemoveFavouriteState.collectLatest {
                if (it) {
                    addRemoveFavouriteViewModel.addRemoveFavourite()
                }
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
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

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }

}

// initialize adapter
//        pagingPlacesAdapter = PagingPlacesAdapter(clickListener = ListItemClickListener {
//            findNavController().navigate(
//                PlacesFragmentDirections.actionPlacesFragmentToPlaceDetailsFragment(
//                    it
//                )
//            )
//        }, favClickListener = ListItemClickListener {
//            addRemoveFavouriteViewModel.startAddRemoveFavouriteState(true, it)
//        })

// set adapter loadState Listener
//        placesAdapter.addLoadStateListener { combinedLoadStates ->
//            if (combinedLoadStates.refresh is LoadState.NotLoading) {
//                binding.emptyListText.visibility = if (placesAdapter.itemCount == 0) {
//                    View.VISIBLE
//                } else {
//                    View.GONE
//                }
//            }
//
//            binding.loadingState = combinedLoadStates.refresh is LoadState.Loading
//            if (combinedLoadStates.refresh is LoadState.Error) {
//                var error = (combinedLoadStates.refresh as LoadState.Error).error.localizedMessage
//                binding.errorState = true
//                binding.errorMessage = error
//                binding.retryListener = RetryClickListener {
//                    placesAdapter.retry()
//                }
//            } else {
//                binding.errorState = false
//                binding.retryListener = null
//            }
//        }

// set Adapter to recyclerView
//                pagingPlacesAdapter.withLoadStateFooter(
//                footer = ListsLoadStateAdapter(RetryClickListener {
//                    pagingPlacesAdapter.retry()
//                })
//            )

// update list after remove favourites
//                        pagingPlacesAdapter.updatePlace(
//                            placeId
//                        )

// listen to places api
//        lifecycleScope.launchWhenStarted {
//            viewModel.places.collectLatest {
//                placesAdapter.submitData(it)
//            }
//        }