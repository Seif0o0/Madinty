package com.madinaty.app.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
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
        if (UserInfo.userId.isEmpty()) {
            binding.logout.text = getString(R.string.login)
        } else {
            binding.logout.text = getString(R.string.logout)
        }

        val activity = requireActivity() as MainActivity
        activity.hideBottomNav(false)

        return binding.root
    }

    private val profileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                navigateTo(MoreFragmentDirections.actionMoreFragmentToProfileFragment())
            }
        }

    private val addPlaceLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                navigateTo(MoreFragmentDirections.actionMoreFragmentToAddPlaceDepartmentFragment())
            }
        }

    private val myPlaceLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                navigateTo(MoreFragmentDirections.actionMoreFragmentToMyPlacesFragment())
            }
        }

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                (requireActivity() as MainActivity).navigateTo(R.id.bottom_nav_home)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.setOnClickListener {
            launchActivity(
                launcher = profileLauncher,
                destination = MoreFragmentDirections.actionMoreFragmentToProfileFragment()
            )
        }

        binding.addPlace.setOnClickListener {
            launchActivity(
                launcher = addPlaceLauncher,
                destination = MoreFragmentDirections.actionMoreFragmentToAddPlaceDepartmentFragment()
            )
        }

        binding.myPlace.setOnClickListener {
            launchActivity(
                launcher = myPlaceLauncher,
                destination = MoreFragmentDirections.actionMoreFragmentToMyPlacesFragment()
            )
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
            launchActivity(loginLauncher)
        }
    }

    private fun launchActivity(
        launcher: ActivityResultLauncher<Intent>,
        destination: NavDirections? = null,
    ) {
        if (UserInfo.userId.isEmpty()) {
            launcher.launch(
                Intent(
                    requireContext(),
                    AuthActivity::class.java
                ).apply {
                    putExtra("requiredLogin", true)
                })
        } else {
            if (destination == null) {
                CustomDialog.showLogoutDialog(context = requireContext()) {
                    logUserOut()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
            } else {
                navigateTo(destination)
            }
        }
    }

    private fun navigateTo(destination: NavDirections) {
        findNavController().navigate(destination)
    }
}