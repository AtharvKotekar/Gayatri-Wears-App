package com.gayatriladieswears.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.*
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.gayatriladieswears.app.R.layout.activity_home
import com.gayatriladieswears.app.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment2) as NavHostFragment
        navController = navHostFragment.navController

        setupWithNavController(binding.bottomNav,navController)




    }
}