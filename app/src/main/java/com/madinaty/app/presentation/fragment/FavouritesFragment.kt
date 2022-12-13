package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.madinaty.app.R
import com.madinaty.app.databinding.DeleteFavouritesDialogLayoutBinding
import com.madinaty.app.databinding.FragmentFavouritesBinding
import com.madinaty.app.presentation.adapter.FavouritesAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.ListsLoadStateAdapter
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


        favouritesAdapter = FavouritesAdapter(clickListener = ListItemClickListener {
            addRemoveFavouriteViewModel.startAddRemoveFavouriteState(true, it)
        })

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
}