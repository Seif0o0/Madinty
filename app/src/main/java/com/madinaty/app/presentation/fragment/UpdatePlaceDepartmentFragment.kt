package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentUpdatePlaceDepartmentBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.AddPlaceDepartmentsAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.ListsLoadStateAdapter
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.HomeViewModel
import com.madinaty.app.presentation.viewmodel.UpdatePlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UpdatePlaceDepartmentFragment : Fragment() {
    private val viewModel: UpdatePlaceViewModel by activityViewModels()

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentUpdatePlaceDepartmentBinding

    private lateinit var departmentsAdapter: AddPlaceDepartmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdatePlaceDepartmentBinding.inflate(layoutInflater, container, false)
        viewModel.setMyPlace(UpdatePlaceDepartmentFragmentArgs.fromBundle(requireArguments()).myPlaceInfo)
        binding.viewModel = viewModel
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        binding.lifecycleOwner = requireActivity()
        binding.categoryLabel.text = String.format(
            getString(R.string.your_selected_category_is),viewModel.place.department.name)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.skip.setOnClickListener {
            findNavController().navigate(
                UpdatePlaceDepartmentFragmentDirections.actionUpdatePlaceDepartmentFragmentToUpdatePlaceDetailsFragment(
                    departmentId = viewModel.place.departmentId
                )
            )
        }

        /* departments list section */
        departmentsAdapter =
            AddPlaceDepartmentsAdapter(clickListener = ListItemClickListener { department ->
                if (department.departmentChilds!!.isNotEmpty()) {
                    findNavController().navigate(
                        UpdatePlaceDepartmentFragmentDirections.actionUpdatePlaceDepartmentFragmentToUpdatePlaceDepartmentChildFragment(
                            department
                        )
                    )
                } else {
                    findNavController().navigate(
                        UpdatePlaceDepartmentFragmentDirections.actionUpdatePlaceDepartmentFragmentToUpdatePlaceDetailsFragment(
                            departmentId = department.id
                        )
                    )
                }
            })

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
                LinearLayoutManager(requireContext())
            adapter = departmentsAdapter.withLoadStateFooter(
                footer = ListsLoadStateAdapter(RetryClickListener { departmentsAdapter.retry() })
            )
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.departments.collectLatest {
                departmentsAdapter.submitData(it)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.deleteAllData()
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}