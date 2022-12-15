package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentHomeBinding
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.*
import com.madinaty.app.presentation.viewmodel.HomeViewModel
import com.madinaty.app.presentation.viewmodel.PinOffersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private val pinOffersViewModel: PinOffersViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    lateinit var departmentsAdapter: DepartmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        binding.viewModel = pinOffersViewModel
        if (UserInfo.city.isEmpty() || UserInfo.city == "TODO::CITY".lowercase()) {//TODO dont forget to remove || condition
            binding.regionValue = getString(R.string.no_selected_region)
            binding.changeText.text = getString(R.string.select)
        } else {
            binding.regionValue = UserInfo.city
            binding.changeText.text = getString(R.string.change)
        }

        binding.offersRetryListener = RetryClickListener {
            pinOffersViewModel.getOffers()
        }
        binding.lifecycleOwner = requireActivity()

        binding.changeText.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPickCityFragment())
        }
        /* departments list section */
        departmentsAdapter =
            DepartmentsAdapter(clickListener = ListItemClickListener { department ->
                if (department.departmentChilds!!.isNotEmpty()) {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToDepartmentChildsFragment(
                            department = department
                        )
                    )
                } else {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToPlacesFragment(
                            id = department.id,
                            title = department.name,
                            places = department.places?.toTypedArray()
                        )
                    )
                }
            })

        binding.swipeRefresh.setColorSchemeResources(
            R.color.auth_screens_main_color
        )

        binding.swipeRefresh.setOnRefreshListener {
            departmentsAdapter.refresh()
        }

        departmentsAdapter.addLoadStateListener { combinedLoadStates ->
            if (combinedLoadStates.refresh is LoadState.NotLoading) {
                binding.emptyListText.visibility = if (departmentsAdapter.itemCount == 0) {
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
                    departmentsAdapter.retry()
                }
            } else {
                binding.errorState = false
                binding.retryListener = null
            }
        }

        binding.list.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = departmentsAdapter.withLoadStateFooter(
                footer = ListsLoadStateAdapter(RetryClickListener { departmentsAdapter.retry() })
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.departments.collectLatest {
                if (binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isRefreshing = false
                }
                departmentsAdapter.submitData(it)
            }
        }


        /* offers slider section */
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
        activity.hideBottomNav(false)
    }
}