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
import com.madinaty.app.databinding.FragmentDepartmentChildsBinding
import com.madinaty.app.domain.model.Department
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.DepartmentChildsAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.PinOffersAdapter
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.PinOffersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DepartmentChildsFragment : Fragment() {
    private val pinOffersViewModel: PinOffersViewModel by viewModels()
    private lateinit var binding: FragmentDepartmentChildsBinding
    private lateinit var department: Department
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDepartmentChildsBinding.inflate(inflater, container, false)
        val args = DepartmentChildsFragmentArgs.fromBundle(requireArguments())
        department = args.department
        binding.titleString = department.name
        binding.offersRetryListener = RetryClickListener {
            pinOffersViewModel.getOffers()
        }
        binding.viewModel = pinOffersViewModel
        binding.lifecycleOwner = requireActivity()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val childsAdapter =
            DepartmentChildsAdapter(clickListener = ListItemClickListener { departmentChild ->
                if (departmentChild.childs.isNullOrEmpty()) {
                    findNavController().navigate(
                        DepartmentChildsFragmentDirections.actionDepartmentChildsFragmentToPlacesFragment(
                            id = departmentChild.id,
                            title = departmentChild.name,
                            places = departmentChild.places?.toTypedArray()
                        )
                    )
                } else {
                    findNavController().navigate(
                        DepartmentChildsFragmentDirections.actionDepartmentChildsFragmentSelf(
                            Department(
                                id = departmentChild.id,
                                name = departmentChild.name,
                                departmentChilds = departmentChild.childs,
                                places = departmentChild.places
                            )
                        )
                    )
                }
            })

        childsAdapter.submitList(department.departmentChilds!!.toMutableList())
        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = childsAdapter
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
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}