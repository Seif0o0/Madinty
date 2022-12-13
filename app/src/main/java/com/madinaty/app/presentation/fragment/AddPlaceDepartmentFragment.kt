package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.databinding.FragmentAddPlaceDepartmentBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.AddPlaceDepartmentsAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.ListsLoadStateAdapter
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.AddPlaceViewModel
import com.madinaty.app.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddPlaceDepartmentFragment : Fragment() {
    private val viewModel: AddPlaceViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentAddPlaceDepartmentBinding

    lateinit var departmentsAdapter: AddPlaceDepartmentsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPlaceDepartmentBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.loadingState = false
        binding.errorState = false
        binding.errorMessage = ""
        binding.retryListener = null
        binding.lifecycleOwner = requireActivity()

        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        /* departments list section */
        departmentsAdapter =
            AddPlaceDepartmentsAdapter(clickListener = ListItemClickListener { department ->
                if (department.departmentChilds!!.isNotEmpty()) {
                    findNavController().navigate(
                        AddPlaceDepartmentFragmentDirections.actionAddPlaceDepartmentFragmentToAddPlaceDepartmentChildFragment(
                            department
                        )
                    )
                } else {
                    findNavController().navigate(
                        AddPlaceDepartmentFragmentDirections.actionAddPlaceDepartmentFragmentToAddPlaceDetailsFragment(
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

}