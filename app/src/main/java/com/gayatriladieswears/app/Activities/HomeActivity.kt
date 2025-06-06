package com.gayatriladieswears.app.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.gayatriladieswears.app.Fragments.CheckOutFragment
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.Fragments.OrderFragment
import com.gayatriladieswears.app.currentNavigationFragment
import com.gayatriladieswears.app.databinding.ActivityHomeBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.razorpay.PaymentResultListener


class HomeActivity : AppCompatActivity(),PaymentResultListener{

    lateinit var binding:ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.gayatriladieswears.app.R.layout.activity_home)
        auth= FirebaseAuth.getInstance()


        FirebaseAnalytics.getInstance(this).logEvent("app_launch", null)
        FirebaseInAppMessaging.getInstance().triggerEvent("app_launch")






        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val navHostFragment = supportFragmentManager.findFragmentById(com.gayatriladieswears.app.R.id.fragment2) as NavHostFragment
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


            else if(destination.label == "fragment_order_add_address"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.GONE
                    },100
                )

            }

            else if(destination.label == "fragment_order"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.GONE
                    },100
                )

            }

            else if(destination.label == "fragment_order_address"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.GONE
                    },100
                )

            }

            else if(destination.label == "fragment_category"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.VISIBLE
                    },100
                )

            }

            else if(destination.label == "fragment_search_fragment"){
                val handler = Handler()
                handler.postDelayed(
                    Runnable {
                        binding.bottomNav.visibility = View.GONE
                    },100
                )

            }
        }

    }




    override fun onPaymentSuccess(p0: String?) {
        try {
            val fragment: CheckOutFragment = supportFragmentManager.currentNavigationFragment as CheckOutFragment
            fragment.checkRazorResponse(p0,true)
            Log.e(TAG, "onPaymentSuccess: $p0")
        }catch (e:Exception){
            Log.e(TAG, "onPaymentSuccess: ${e.localizedMessage}")
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        try {
            val fragment: CheckOutFragment = supportFragmentManager.currentNavigationFragment as CheckOutFragment
            fragment.checkRazorResponse(p1,false)
            Log.e(TAG, "onPaymentSuccess: $p1")
        }catch (e:Exception){
            Log.e(TAG, "onPaymentSuccess: ${e.localizedMessage}")
        }
    }


}