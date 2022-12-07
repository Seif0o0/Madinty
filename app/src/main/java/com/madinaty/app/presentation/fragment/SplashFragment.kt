package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.madinaty.app.presentation.activity.MainActivity
import com.madinaty.app.R
import com.madinaty.app.presentation.activity.AuthActivity
import com.madinaty.app.databinding.FragmentSplashBinding
import com.madinaty.app.kot_pref.UserInfo

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.rootView)
            constraintSet.setVerticalBias(R.id.splash_logo, 0.2f)

            val transition = AutoTransition()
            transition.duration = 3000
            transition.interpolator = AccelerateDecelerateInterpolator()
            transition.doOnEnd {
                if (UserInfo.isFirstTime) {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLanguageFragment())
                } else {
                    if (UserInfo.userId.isNotEmpty()) {/* logged in user */
                        requireActivity().finish()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    } else {
                        requireActivity().finish()
                        startActivity(Intent(requireContext(), AuthActivity::class.java))
                    }
                }
            }

            TransitionManager.beginDelayedTransition(binding.rootView, transition)
            constraintSet.applyTo(binding.rootView)
        }
    }

    override fun onResume() {
        super.onResume()
    }

}