package com.madinaty.app.presentation.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.madinaty.app.R
import com.madinaty.app.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var binding : ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
    }
    
}