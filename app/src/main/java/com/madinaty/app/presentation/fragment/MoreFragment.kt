package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentMoreBinding
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.presentation.activity.AuthActivity
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.utils.CustomDialog
import com.madinaty.app.utils.logUserOut
import dev.b3nedikt.app_locale.AppLocale
import java.util.*

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = requireActivity()
        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToProfileFragment())
        }

        binding.addPlace.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToAddPlaceDepartmentFragment())
        }

        binding.myPlace.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToMyPlacesFragment())
        }
        binding.appLanguageValue.text =
            if (UserInfo.appLanguage == getString(R.string.arabic_value)) getString(R.string.arabic_label) else getString(
                R.string.english_label
            )
        binding.helpCenter.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToHelpCenterFragment())
        }

        binding.privacyPolicy.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToPrivacyPolicyFragment())
        }

        binding.aboutApp.setOnClickListener {
            findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToAboutAppFragment())
        }

        binding.appLanguage.setOnClickListener {
            if (UserInfo.appLanguage == getString(R.string.arabic_value)) {
                UserInfo.appLanguage = getString(R.string.english_value)
                AppLocale.desiredLocale = Locale(getString(R.string.english_value))
            } else {
                UserInfo.appLanguage = getString(R.string.arabic_value)
                AppLocale.desiredLocale = Locale(getString(R.string.arabic_value))
            }
            (requireActivity() as MainActivity).recreate()
        }

        binding.logout.setOnClickListener {
            CustomDialog.showLogoutDialog(context = requireContext()) {
                logUserOut()
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}