package com.madinaty.app.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.madinaty.app.presentation.fragment.IntroductionFragment

class IntroductionAdapter(fragment:Fragment):FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int)= IntroductionFragment.newInstance(position)
}