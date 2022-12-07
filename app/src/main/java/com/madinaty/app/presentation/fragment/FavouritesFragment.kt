package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.madinaty.app.R
import com.madinaty.app.databinding.DeleteFavouritesDialogLayoutBinding
import com.madinaty.app.databinding.FragmentFavouritesBinding

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {
    private lateinit var binding: FragmentFavouritesBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentFavouritesBinding.bind(view)

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