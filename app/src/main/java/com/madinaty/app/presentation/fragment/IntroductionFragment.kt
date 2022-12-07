package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment() {
    private lateinit var binding: FragmentIntroductionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false)

        when (arguments?.getInt("indexParam")) {
            0 -> {
                binding.header.setImageResource(R.mipmap.intro_1)
                binding.title.text = getString(R.string.intro_title_1)
            }
            1 -> {
                binding.header.setImageResource(R.mipmap.intro_2)
                binding.title.text = getString(R.string.intro_title_2)
            }
            else -> {
                binding.header.setImageResource(R.mipmap.intro_3)
                binding.title.text = getString(R.string.intro_title_3)
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(index: Int) = IntroductionFragment().apply {
            arguments = Bundle().apply {
                putInt("indexParam", index)
            }
        }
    }
}