package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madinaty.app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.contactUs.setOnClickListener {
            binding.hiddenFloatingButton.visibility = View.VISIBLE
            binding.contactUs.visibility = View.GONE
            binding.whatsFloatingButton.requestFocus()
        }

        binding.termsConditions.setOnClickListener {
            val termsConditionsDialog = TermsConditionsDialogFragment.newInstance()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val prevFragment =
                requireActivity().supportFragmentManager.findFragmentByTag("TermsConditionsDialog")
            if (prevFragment != null)
                fragmentTransaction.remove(prevFragment)
            fragmentTransaction.addToBackStack(null)
            termsConditionsDialog.show(fragmentTransaction, "TermsConditionsDialog")
        }

        binding.closeMenu.setOnClickListener {
            binding.hiddenFloatingButton.visibility = View.GONE
            binding.contactUs.visibility = View.VISIBLE
        }

        binding.phoneBackground.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToPhoneLoginFragment()
            )
        }
    }
}