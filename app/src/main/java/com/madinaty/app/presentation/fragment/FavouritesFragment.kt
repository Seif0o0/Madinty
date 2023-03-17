package com.madinaty.app.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.madinaty.app.R
import com.madinaty.app.data.mapper.toPlace
import com.madinaty.app.databinding.DeleteFavouritesDialogLayoutBinding
import com.madinaty.app.databinding.FragmentFavouritesBinding
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.presentation.activity.AuthActivity
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.FavouritesAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.RetryClickListener
import com.madinaty.app.presentation.viewmodel.AddRemoveFavouriteViewModel
import com.madinaty.app.presentation.viewmodel.FavouritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourites) {
    private val viewModel: FavouritesViewModel by viewModels()
    private val addRemoveFavouriteViewModel: AddRemoveFavouriteViewModel by viewModels()
    private lateinit var binding: FragmentFavouritesBinding

    private lateinit var favouritesAdapter: FavouritesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentFavouritesBinding.bind(view)
        binding.viewModel = viewModel
        binding.retryListener = RetryClickListener {
            viewModel.getFavourites()
        }
        binding.lifecycleOwner = requireActivity()

        if (UserInfo.userId.isEmpty()) {
            binding.whiteView.visibility = View.VISIBLE
            val loginLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        initViews()
                    } else {
                        (requireActivity() as MainActivity).navigateTo(R.id.bottom_nav_home)
                    }
                }
            loginLauncher.launch(
                Intent(
                    requireContext(),
                    AuthActivity::class.java
                ).apply {
                    putExtra("requiredLogin", true)
                })
        } else {
            initViews()
        }

    }

    private fun initViews() {
        binding.whiteView.visibility = View.GONE
        favouritesAdapter = FavouritesAdapter(clickListener = ListItemClickListener {
            setFragmentResultListener("changeFavourite") { _, bundle ->
                if (bundle.getBoolean("updated")) {
                    viewModel.getFavourites()
                }
            }
            findNavController().navigate(
                FavouritesFragmentDirections.actionFavouritesFragmentToPlaceDetailsFragment2(it.toPlace())
            )
        }, favClickListener = ListItemClickListener {
            addRemoveFavouriteViewModel.startAddRemoveFavouriteState(true, it)
        })

        binding.swipeRefresh.setColorSchemeResources(
            R.color.auth_screens_main_color
        )

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            viewModel.getFavourites()
        }

        lifecycleScope.launchWhenStarted {
            addRemoveFavouriteViewModel.addRemoveFavouriteState.collectLatest {
                if (it) {
                    addRemoveFavouriteViewModel.placeId?.let { it1 ->
                        favouritesAdapter.removeItem(
                            it1
                        )
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

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favouritesAdapter
        }

        lifecycleScope.launchWhenStarted {
            viewModel.favourites.collectLatest {
                favouritesAdapter.submitList(it)
            }
        }

        binding.deleteIcon.setOnClickListener {
            showDeleteFavouritesDialog()
        }
    }

    private fun showDeleteFavouritesDialog() {
        val dialog = MaterialDialog(requireContext(), ModalDialog).show {
            cornerRadius(res = com.intuit.sdp.R.dimen._8sdp)
            cancelOnTouchOutside(true)
            cancelable(false)
            customView(
                R.layout.delete_favourites_dialog_layout,
                noVerticalPadding = true,
                dialogWrapContent = true
            )
        }

        val dialogBinding = DeleteFavouritesDialogLayoutBinding.bind(dialog.getCustomView())

        dialogBinding.yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(false)
    }
}