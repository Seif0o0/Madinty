package com.madinaty.app.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentHomeBinding
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.presentation.activity.AuthActivity
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.*
import com.madinaty.app.presentation.viewmodel.HomeViewModel
import com.madinaty.app.presentation.viewmodel.PinOffersViewModel
import com.madinaty.app.utils.CustomDialog
import com.madinaty.app.utils.logUserOut
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private val pinOffersViewModel: PinOffersViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    lateinit var departmentsAdapter: DepartmentsAdapter

    private val changeCityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPickCityFragment())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        binding.viewModel = pinOffersViewModel
        if (UserInfo.region.isEmpty()) {
            binding.regionValue = getString(R.string.no_selected_region)
            binding.changeText.text = getString(R.string.select)
        } else {
            binding.regionValue = UserInfo.region
            binding.changeText.text = getString(R.string.change)
        }

        binding.offersRetryListener = RetryClickListener {
            pinOffersViewModel.getOffers()
        }
        binding.lifecycleOwner = requireActivity()

        binding.changeText.setOnClickListener {
            launchActivity(
                launcher = changeCityLauncher,
                destination = HomeFragmentDirections.actionHomeFragmentToPickCityFragment()
            )
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
            binding.swipeRefresh.isRefreshing = false
            pinOffersViewModel.getOffers()
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

    private fun launchActivity(
        launcher: ActivityResultLauncher<Intent>,
        destination: NavDirections,
    ) {
        if (UserInfo.userId.isEmpty()) {
            launcher.launch(Intent(
                requireContext(), AuthActivity::class.java
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