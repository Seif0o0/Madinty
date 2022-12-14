package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentMyPlaceDetailsBinding
import com.madinaty.app.presentation.adapter.AttachmentsAdapter
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.utils.CustomDialog

class MyPlaceDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMyPlaceDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPlaceDetailsBinding.inflate(inflater, container, false)
        val placeDetails = MyPlaceDetailsFragmentArgs.fromBundle(requireArguments()).myPlace
        binding.place = placeDetails
        binding.lifecycleOwner = requireActivity()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.address.setOnClickListener {
            val latlngUri = Uri.parse("geo:${placeDetails.latitude},${placeDetails.longitude}?z=15")
            val mapIntent = Intent(Intent.ACTION_VIEW, latlngUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
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
}