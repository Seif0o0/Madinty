package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentHelpCenterBinding
import com.madinaty.app.databinding.FragmentMoreBinding

class HelpCenterFragment : Fragment(R.layout.fragment_help_center) {
    private lateinit var binding: FragmentHelpCenterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHelpCenterBinding.bind(view)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}