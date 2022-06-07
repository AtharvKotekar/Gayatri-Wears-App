package com.gayatriladieswears.app.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.ActivityHomeBinding
import com.gayatriladieswears.app.getData
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    lateinit var binding:ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        auth= FirebaseAuth.getInstance()






        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment2) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNav,navController)


        var currentUser = auth.currentUser

        if(currentUser==null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.i(TAG, "onDestinationChanged: "+destination.label);
            if(destination.label == "fragment_home"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.VISIBLE
                    },100
                )

            }
            else if(destination.label == "fragment_shoping"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.GONE
                    },100
                )

            }
            else if(destination.label == "fragment_product_detail"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.GONE
                    },100
                )

            }
            else if(destination.label == "fragment_cart"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.GONE
                    },100
                )

            }
        }

    }




}