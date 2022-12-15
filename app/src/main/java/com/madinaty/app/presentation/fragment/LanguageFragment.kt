package com.madinaty.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.FragmentLanguageBinding
import com.madinaty.app.kot_pref.UserInfo
import com.madinaty.app.utils.setLangButtonsColors
import dev.b3nedikt.app_locale.AppLocale
import java.util.*

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

        binding.arabicBtn.setOnClickListener {
            if (UserInfo.appLanguage == getString(R.string.english_value)) {
                UserInfo.appLanguage = getString(R.string.arabic_value)
                AppLocale.desiredLocale = Locale(getString(R.string.arabic_value))
                binding.arabicBtn.setLangButtonsColors(true)
                binding.englishBtn.setLangButtonsColors(false)
            }
        }

        binding.englishBtn.setOnClickListener {
            if (UserInfo.appLanguage == getString(R.string.arabic_value)) {
                UserInfo.appLanguage = getString(R.string.english_value)
                AppLocale.desiredLocale = Locale(getString(R.string.english_value))
                binding.arabicBtn.setLangButtonsColors(true)
                binding.englishBtn.setLangButtonsColors(false)
            }
        }
        binding.startBtn.setOnClickListener {
            findNavController().navigate(
                LanguageFragmentDirections.actionLanguageFragmentToIntroductionContainerFragment()
            )
        }
    }
}