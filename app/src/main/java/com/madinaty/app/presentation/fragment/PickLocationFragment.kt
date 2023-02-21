package com.madinaty.app.presentation.fragment

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.birjuvachhani.locus.Locus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentPickLocationBinding
import com.madinaty.app.presentation.activity.MainActivity

class PickLocationFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentPickLocationBinding
    private var currentLocation: Location? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPickLocationBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Locus.getCurrentLocation(requireActivity()){
            it.location?.let {location ->
                currentLocation = location
            }
        }

        binding.pickBtn.setOnClickListener {
            if (pickedLatLng.isNotEmpty()) setFragmentResult(
                "PickLatLng",
                bundleOf("pickedLatLng" to pickedLatLng)
            )
            findNavController().popBackStack()
        }


        return binding.root
    }

    private var pickedLatLng = ""
    private lateinit var mMap: GoogleMap
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        val latLng = PickLocationFragmentArgs.fromBundle(requireArguments()).latLng
        val splitLatLng = latLng.split(",")
        if (latLng.isNotEmpty()) {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(splitLatLng[0].toDouble(), splitLatLng[1].toDouble()), 15f
                )
            )
        }

        mMap.setOnCameraMoveListener {
            val currentLatLng = mMap.cameraPosition.target
            pickedLatLng = "${currentLatLng.latitude},${currentLatLng.longitude}"
        }

        binding.currentLocationBtn.setOnClickListener {
            if(currentLocation == null){
                Locus.getCurrentLocation(requireActivity()){
                    it.location?.let {location ->
                        currentLocation = location
                        moveToCurrentLocation()
                    }
                }
            }else{
                moveToCurrentLocation()
            }

        }
    }

    private fun moveToCurrentLocation(){
        currentLocation?.let {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude, it.longitude), 15f
                )
            )
        }
    }
    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}