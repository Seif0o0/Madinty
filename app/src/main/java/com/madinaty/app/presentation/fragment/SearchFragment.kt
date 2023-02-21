package com.madinaty.app.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentSearchBinding
import com.madinaty.app.domain.model.Place
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.presentation.activity.AuthActivity
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.*
import com.madinaty.app.presentation.viewmodel.AddRemoveFavouriteViewModel
import com.madinaty.app.presentation.viewmodel.SearchViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private val addRemoveFavouriteViewModel: AddRemoveFavouriteViewModel by viewModels()

    private lateinit var placesAdapter: PagingPlacesAdapter

    private val placeDetailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                findNavController().navigate(
                    PlacesFragmentDirections.actionPlacesFragmentToPlaceDetailsFragment(
                        pickedPlace
                    )
                )
            }
        }
    lateinit var pickedPlace: Place

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        binding.lifecycleOwner = requireActivity()



        placesAdapter = PagingPlacesAdapter(clickListener = ListItemClickListener {
            pickedPlace = it
            launchActivity(
                launcher = placeDetailsLauncher,
                destination = SearchFragmentDirections.actionSearchFragmentToPlaceDetailsFragment3(
                    it
                )
            )
        }, favClickListener = ListItemClickListener {
            if (UserInfo.userId.isEmpty()) {
                CustomDialog.showErrorDialog(
                    context = requireContext(),
                    errorMessage = getString(R.string.login_required_message)
                )
            } else {
                addRemoveFavouriteViewModel.startAddRemoveFavouriteState(true, it)
            }

        })

        binding.list.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = placesAdapter.withLoadStateFooter(
                footer = ListsLoadStateAdapter(RetryClickListener { placesAdapter.retry() })
            )
        }
        binding.swipeRefresh.setColorSchemeResources(
            R.color.auth_screens_main_color
        )

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            placesAdapter.refresh()
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

        placesAdapter.withLoadStateFooter(
            footer = ListsLoadStateAdapter(RetryClickListener {
                placesAdapter.retry()
            })
        )

        lifecycleScope.launchWhenStarted {
            addRemoveFavouriteViewModel.startAddRemoveFavouriteState.collectLatest {
                if (it) {
                    addRemoveFavouriteViewModel.addRemoveFavourite()
                }
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
            viewModel.places.collectLatest {
                placesAdapter.submitData(it)
            }
        }
        return binding.root
    }

    private fun launchActivity(
        launcher: ActivityResultLauncher<Intent>,
        destination: NavDirections,
    ) {
        if (UserInfo.userId.isEmpty()) {
            launcher.launch(
                Intent(
                    requireContext(),
                    AuthActivity::class.java
                ).apply {
                    putExtra("requiredLogin", true)
                })
        } else {
            findNavController().navigate(destination)

        }
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(false)
    }
}