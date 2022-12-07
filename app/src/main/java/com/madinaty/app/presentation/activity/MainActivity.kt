package com.madinaty.app.presentation.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.madinaty.app.R
import com.madinaty.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        // setup the bottom navigation view with navController
        binding.bottomNav.setupWithNavController(navController)

    }

    fun hideBottomNav(hide: Boolean) {
        binding.bottomNav.visibility = if (hide) View.GONE else View.VISIBLE
    }

}