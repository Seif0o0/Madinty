package com.madinaty.app.presentation.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.madinaty.app.databinding.FragmentPickCityBinding
import com.madinaty.app.domain.model.Region
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.presentation.activity.MainActivity

@Suppress("DEPRECATION")
class PickCityFragment : Fragment() {
    private val requestKey = "pickCityFragment"
    private lateinit var binding: FragmentPickCityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPickCityBinding.inflate(layoutInflater, container, false)

        binding.cityEdittext.setText(UserInfo.city)

        binding.cityEdittext.setOnClickListener {
            val regionsDialog = RegionsDialogFragment.newInstance(requestKey)
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val prevFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("RegionsDialog")
            if (prevFragment != null) fragmentTransaction.remove(prevFragment)
            fragmentTransaction.addToBackStack(null)
            requireActivity().supportFragmentManager.setFragmentResultListener(
                requestKey, viewLifecycleOwner
            ) { requestKey, bundle ->

                val region = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable("region", Region::class.java)
                } else {
                    bundle.getParcelable("region") as? Region
                }

                if (region != null) {
                    UserInfo.city = region.name
                    binding.cityEdittext.setText(UserInfo.city)
                }
            }
            regionsDialog.show(fragmentTransaction, "RegionsDialog")
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(true)
    }
}