package com.madinaty.app.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.madinaty.app.R
import com.madinaty.app.presentation.activity.AuthActivity
import com.madinaty.app.presentation.adapter.IntroductionAdapter
import com.madinaty.app.databinding.FragmentIntroductionContainerBinding
import com.madinaty.app.kot_pref.UserInfo

class IntroductionContainerFragment : Fragment() {
    private lateinit var switchBtn: ViewPager2.OnPageChangeCallback
    private lateinit var binding: FragmentIntroductionContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionContainerBinding.inflate(inflater, container, false)

        val adapter = IntroductionAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPagerIndicator.setViewPager2(binding.viewPager)

        switchBtn = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                when (position) {
                    0 -> {
                        binding.prevBtn.visibility = View.INVISIBLE
                        binding.nextBtn.text = getString(R.string.next)
                        binding.prevBtn.text = getString(R.string.previous)
                    }
                    1 -> {
                        binding.prevBtn.visibility = View.VISIBLE
                        binding.nextBtn.text = getString(R.string.next)
                        binding.prevBtn.text = getString(R.string.previous)
                    }
                    2 -> {
                        binding.prevBtn.visibility = View.VISIBLE
                        binding.nextBtn.text = getString(R.string.login)
                        binding.prevBtn.text = getString(R.string.previous)

                    }
                }
            }
        }
        binding.viewPager.registerOnPageChangeCallback(switchBtn)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.viewPager.currentItem == 0) {
                        findNavController().popBackStack()
                    } else {
                        binding.viewPager.currentItem--
                    }
                }
            })

        binding.nextBtn.setOnClickListener {
            if (binding.viewPager.currentItem < 2) {
                binding.viewPager.currentItem++
            } else {
                // go to login screen
                UserInfo.isFirstTime = false
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
        }

        binding.prevBtn.setOnClickListener {
            binding.viewPager.currentItem--
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(switchBtn)
    }
}