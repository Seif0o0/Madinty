package com.madinaty.app.presentation.fragment

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madinaty.app.databinding.FragmentUpdatePlaceDetailsBinding
import com.madinaty.app.domain.model.Region
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.presentation.viewmodel.UpdatePlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UpdatePlaceDetailsFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    private val requestKey = "UpdatePlaceDetailsFragment"
    private val viewModel: UpdatePlaceViewModel by activityViewModels()

    private lateinit var binding: FragmentUpdatePlaceDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdatePlaceDetailsBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.cityEdittext.setText(viewModel.region.name)
        binding.lifecycleOwner = requireActivity()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.pickLocationIconBackground.setOnClickListener {
            setFragmentResultListener("PickLatLng") { _, bundle ->
                val latLng = bundle.getString("pickedLatLng")
                latLng?.let {
                    binding.placeLocationEdittext.setText(it)
                }
            }
            findNavController().navigate(
                UpdatePlaceDetailsFragmentDirections.actionUpdatePlaceDetailsFragmentToPickLocationFragment(
                    latLng = binding.placeLocationEdittext.text.toString()
                )
            )
        }

        binding.cityEdittext.setOnClickListener {
            val regionsDialog = RegionsDialogFragment.newInstance(requestKey)
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val prevFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("RegionsDialog")
            if (prevFragment != null) fragmentTransaction.remove(prevFragment)
            fragmentTransaction.addToBackStack(null)
            requireActivity().supportFragmentManager.setFragmentResultListener(
                requestKey, viewLifecycleOwner
            ) { _, bundle ->
                val region = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable("region", Region::class.java)
                } else {
                    bundle.getParcelable("region") as? Region
                }

                if (region != null) {
                    binding.cityEdittext.setText(region.name)
                    viewModel.region = region
                }
            }

            regionsDialog.show(fragmentTransaction, "RegionsDialog")
        }

        binding.nextBtn.setOnClickListener {
            viewModel.onNextBtnClicked()
        }

        viewModel.locationState.observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && viewModel.locationErrorState.value == View.VISIBLE) {
                viewModel.locationErrorState.value = View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.goToNext.collectLatest {
                if (it) {
                    findNavController().navigate(
                        UpdatePlaceDetailsFragmentDirections.actionUpdatePlaceDetailsFragmentToUpdatePlacePhotosFragment(
                            departmentId = UpdatePlaceDetailsFragmentArgs.fromBundle(
                                requireArguments()
                            ).departmentId
                        )
                    )
                    viewModel.goToNext(false)
                }
            }
        }

        binding.workingHoursEdittext.setOnClickListener {
            val hour: Int
            val minute: Int
            if (binding.workingHoursEdittext.text.toString().isEmpty()) {
                hour = calendar.get(Calendar.HOUR_OF_DAY)
                minute = calendar.get(Calendar.MINUTE)
            } else {
                val splitStartTime =
                    binding.workingHoursEdittext.text.toString().split("-")[0].trim().split(":")
                hour = splitStartTime[0].toInt()
                minute = splitStartTime[1].toInt()
            }

            TimePickerDialog(
                requireContext(),
                this,
                hour,
                minute,
                true
            ).show()
        }

        return binding.root
    }

    private val calendar = Calendar.getInstance()
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val startTime =
            "${if (hourOfDay < 10) "0$hourOfDay" else hourOfDay}:${if (minute < 10) "0$minute" else minute} - "
        val pickedHour: Int
        val pickedMinute: Int
        if (binding.workingHoursEdittext.text.toString().isEmpty()) {
            pickedHour = calendar.get(Calendar.HOUR_OF_DAY)
            pickedMinute = calendar.get(Calendar.MINUTE)
        } else {
            val splitStartTime =
                binding.workingHoursEdittext.text.toString().split("-")[1].trim().split(":")
            pickedHour = splitStartTime[0].toInt()
            pickedMinute = splitStartTime[1].toInt()
        }
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay1, minute1 ->
                val endTime =
                    "${if (hourOfDay1 < 10) "0$hourOfDay1" else hourOfDay1}:${if (minute1 < 10) "0$minute1" else minute1}"
                binding.workingHoursEdittext.setText("$startTime$endTime")
                viewModel.workingHoursState.value = "$startTime$endTime"
                if (binding.workingHoursError.visibility == View.VISIBLE)
                    binding.workingHoursError.visibility = View.GONE
            },
            pickedHour,
            pickedMinute,
            true
        ).show()
    }


    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}