package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.databinding.FragmentAddPlaceDepartmentChildBinding
import com.madinaty.app.domain.model.Department
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.*

class AddPlaceDepartmentChildFragment : Fragment() {
    private lateinit var binding: FragmentAddPlaceDepartmentChildBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPlaceDepartmentChildBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = requireActivity()
        val department =
            AddPlaceDepartmentChildFragmentArgs.fromBundle(requireArguments()).department

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        /* departments list section */
        val childsAdapter =
            AddPlaceDepartmentChildsAdapter(clickListener = ListItemClickListener { departmentChild ->
                if (departmentChild.childs!!.isEmpty()) {
                    findNavController().navigate(
                        AddPlaceDepartmentChildFragmentDirections.actionAddPlaceDepartmentChildFragmentToAddPlaceDetailsFragment(
                            departmentId = departmentChild.id
                        )
                    )
                } else {
                    findNavController().navigate(
                        AddPlaceDepartmentChildFragmentDirections.actionAddPlaceDepartmentChildFragmentSelf(
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
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = childsAdapter
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}