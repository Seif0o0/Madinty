package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentMyPlacesBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.MyPlacesAdapter
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.MyPlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MyPlacesFragment : Fragment(R.layout.fragment_my_places) {
    private val viewModel: MyPlacesViewModel by viewModels()
    private lateinit var binding: FragmentMyPlacesBinding

    private lateinit var myPlacesAdapter: MyPlacesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentMyPlacesBinding.bind(view)
        binding.viewModel = viewModel
        binding.retryListener = RetryClickListener {
            viewModel.getMyPlaces()
        }
        binding.lifecycleOwner = requireActivity()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        myPlacesAdapter = MyPlacesAdapter(clickListener = ListItemClickListener {
            findNavController().navigate(
                MyPlacesFragmentDirections.actionMyPlacesFragmentToMyPlaceDetailsFragment(it)
            )
        })

        binding.swipeRefresh.setColorSchemeResources(
            R.color.auth_screens_main_color
        )

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getMyPlaces()
        }

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myPlacesAdapter
        }
        lifecycleScope.launchWhenStarted {
            viewModel.myPlaces.collectLatest {
                if (binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isRefreshing = false
                }
                myPlacesAdapter.submitList(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}