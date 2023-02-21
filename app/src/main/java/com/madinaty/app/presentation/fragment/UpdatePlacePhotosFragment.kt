package com.madinaty.app.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentUpdatePlacePhotosBinding
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.adapter.ListItemClickListener
import com.madinaty.app.presentation.adapter.PickedImagesAdapter
import com.madinaty.app.presentation.viewmodel.UpdatePlaceViewModel
import com.madinaty.app.utils.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UpdatePlacePhotosFragment : Fragment() {
    private val viewModel: UpdatePlaceViewModel by activityViewModels()

    private lateinit var binding: FragmentUpdatePlacePhotosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdatePlacePhotosBinding.inflate(layoutInflater, container, false)
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
            viewModel.startUpdatingPlace.collectLatest {
                if (it) {
                    viewModel.updatePlace()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updatePlaceState.collectLatest {
                if (it) {
                    viewModel.updatePlaceState(false)
                    CustomDialog.showAddPlaceSuccessDialog(
                        context = requireContext(),
                        successMessage = getString(R.string.update_place_success_message)
                    ) {
                        viewModel.deleteAllData()
                        findNavController().navigate(
                            UpdatePlacePhotosFragmentDirections.actionUpdatePlacePhotosFragmentToMyPlacesFragment()
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