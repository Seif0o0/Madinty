package com.madinaty.app.presentation.fragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentAddPlacePhotosBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.PickedImagesAdapter
import com.madinaty.app.presentation.viewmodel.AddPlaceViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddPlacePhotosFragment : Fragment() {
    private val viewModel: AddPlaceViewModel by activityViewModels()

    private lateinit var binding: FragmentAddPlacePhotosBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPlacePhotosBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()
        viewModel.departmentId =
            AddPlacePhotosFragmentArgs.fromBundle(requireArguments()).departmentId
        viewModel.setErrorStateValue()

        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                viewModel.handlePickingImage(it)
            }

        val pickImagesAdapter = PickedImagesAdapter(clickListener = ListItemClickListener { uri ->
            val values = mutableListOf<Uri>()
            viewModel.images.value.mapIndexed { _, pickedUri ->
                values.add(pickedUri)
            }
            values.remove(uri)
            viewModel.images.value = values
        }, addImageClickListener = ListItemClickListener {
            if (viewModel.images.value.size < 12) {
                viewModel.setErrorStateValue("")
                ImagePicker.with(requireActivity()).crop(2f, 1f).cropSquare()
                    .setMultipleAllowed(true).maxResultSize(1080, 1080, true)
                    .provider(ImageProvider.BOTH)
                    .createIntentFromDialog { photoLauncher.launch(it) }
            } else {
                viewModel.setErrorStateValue(getString(R.string.photos_count_limit))
            }
        })

        binding.list.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = pickImagesAdapter
        }

        lifecycleScope.launchWhenStarted {
            viewModel.images.collectLatest {
                pickImagesAdapter.submitList(it)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.validateImages()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.startAddingPlace.collectLatest {
                if (it) {
                    viewModel.addPlace()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addPlaceState.collectLatest {
                if (it) {
                    CustomDialog.showAddPlaceSuccessDialog(
                        context = requireContext(),
                    ) {
                        findNavController().navigate(
                            AddPlacePhotosFragmentDirections.actionAddPlacePhotosFragmentToMoreFragment()
                        )
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.errorState.collectLatest {
                if (it.isNotEmpty()) CustomDialog.showErrorDialog(
                    context = requireContext(), errorMessage = it
                )
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }

}