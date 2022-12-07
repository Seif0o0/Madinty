package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madinaty.app.databinding.FragmentLanguageBinding

class LanguageFragment : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized)
            binding = FragmentLanguageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                LanguageFragmentDirections.actionLanguageFragmentToIntroductionContainerFragment()
            )
        }
    }
}