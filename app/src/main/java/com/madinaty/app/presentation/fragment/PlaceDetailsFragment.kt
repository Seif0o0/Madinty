package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentPlaceDetailsBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.AttachmentsAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.viewmodel.AddRemoveFavouriteViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class PlaceDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPlaceDetailsBinding
    private val viewModel: AddRemoveFavouriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        val placeDetails = PlaceDetailsFragmentArgs.fromBundle(requireArguments()).place
        binding.place = placeDetails

        binding.backBtn.setOnClickListener {
            navigateBack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })


        binding.address.setOnClickListener {
            val latlngUri = Uri.parse("geo:${placeDetails.latitude},${placeDetails.longitude}?z=15")
            val mapIntent = Intent(Intent.ACTION_VIEW, latlngUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.favouriteIcon.setOnClickListener {
            viewModel.startAddRemoveFavouriteState(true, placeId = placeDetails.id)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveFavouriteState.collectLatest {
                if (it) {
                    placeDetails.isFavourite = !placeDetails.isFavourite
                    binding.place = placeDetails

                    viewModel.startAddRemoveFavouriteState(false)
                    viewModel.addRemoveFavouriteState(false)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.startAddRemoveFavouriteState.collectLatest {
                if (it) {
                    viewModel.addRemoveFavourite()
                }
            }
        }

        binding.call.setOnClickListener {
            if (placeDetails.whatsAppNumber != null) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${placeDetails.whatsAppNumber}")
                startActivity(intent)
            }
        }

        binding.whats.setOnClickListener {
            if (placeDetails.whatsAppNumber != null) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/${placeDetails.whatsAppNumber}")
                    )
                )
            }
        }

        binding.facebook.setOnClickListener {
            if (placeDetails.facebookUrl != null) {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("fb://facewebmodal/f?href=${placeDetails.facebookUrl}")
                        )
                    )
                } catch (e: Exception) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(placeDetails.facebookUrl)))
                }
            }
        }

        binding.email.setOnClickListener {
            if (placeDetails.email != null) {
                try {
                    val intent = Intent(Intent.ACTION_SEND)

                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(placeDetails.email))
                    intent.type = "message/rfc822"
                    startActivity(
                        Intent.createChooser(
                            intent,
                            getString(R.string.pick_email_client)
                        )
                    )
                } catch (e: android.content.ActivityNotFoundException) {
                    CustomDialog.showErrorDialog(
                        context = requireContext(),
                        errorMessage = getString(R.string.not_email_app_installed)
                    )
                }
            }

        }

        /* Attachments part */
        if (placeDetails.attachments.isEmpty()) {
            binding.attachmentsGroup.visibility = View.GONE
        } else {
            binding.attachmentsGroup.visibility = View.VISIBLE
            val attachmentsAdapter = AttachmentsAdapter(clickListener = ListItemClickListener {

            })
            attachmentsAdapter.submitList(placeDetails.attachments)
            binding.viewPager.adapter = attachmentsAdapter
            binding.viewPagerIndicator.setViewPager2(binding.viewPager)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }

    private fun navigateBack() {
        setFragmentResult("changeFavourite", bundleOf("updated" to viewModel.madeFavouriteChanges))
        findNavController().popBackStack()
    }
}