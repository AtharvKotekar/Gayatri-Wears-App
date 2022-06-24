package com.gayatriladieswears.app.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.gayatriladieswears.app.*
import com.gayatriladieswears.app.Adaptors.*
import com.gayatriladieswears.app.Model.Deal
import com.gayatriladieswears.app.Model.Info
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.Model.Sizes
import com.gayatriladieswears.app.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    lateinit var dialog:Dialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding.scrollView2.smoothScrollBy(0,0)






        binding.shimmerViewContainerTopCategory.visibility = View.VISIBLE
        binding.shimmerViewContainerTopCategory.startShimmer()
        binding.recyclerView.visibility = View.INVISIBLE


        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.shimmerViewContainer.startShimmer()
        binding.recyclerView2.visibility = View.INVISIBLE

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        getData(this)
        FirestoreClass().mFirestore.collection("users").whereEqualTo("id",FirebaseAuth.getInstance().currentUser?.uid).get()
            .addOnSuccessListener {
                for (i in it.documents){
                    binding.navDrawer.getHeaderView(0).findViewById<TextView>(R.id.name_text).text = (i.getString("firstName")+" "+i.getString("lastName")).toString()
                }
            }

        binding.navDrawer.setNavigationItemSelectedListener { menuIteam ->
            when(menuIteam.itemId){
                 R.id.home -> {
                     binding.drawerLayout.close()
                 }
                R.id.cart -> {
                    binding.drawerLayout.close()
                    findNavController().navigate(R.id.cartFragment)
                }
                R.id.orders -> {
                    binding.drawerLayout.close()
                    findNavController().navigate(R.id.orderFragment)
                }
                else -> {
                    Toast.makeText(requireContext(), "Tanda ni n nane nare n nanu nare narne no", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }



        binding.menu.setOnClickListener {
            binding.drawerLayout?.open()
        }

        binding.editTextTextPersonName.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFrsgment)

        }

        binding.button3.setOnClickListener {
            binding.scrollView2.smoothScrollTo(0,0)
        }





        binding.cartBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }






        return binding.root
    }



    fun infoGet(iteamList: ArrayList<Info>){
        val adaptor = TopCategoriesAdaptor(requireContext(),iteamList)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }

    fun infoGetFabric(iteamList: ArrayList<Info>){
        val adaptor = FabricAdaptor(requireContext(),iteamList,false)
        binding.recyclerView5.adapter = adaptor
        binding.recyclerView5.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }

    fun infoGetColor(iteamList: ArrayList<Info>){
        val adaptor = FabricAdaptor(requireContext(),iteamList,true)
        binding.recyclerView7.adapter = adaptor
        binding.recyclerView7.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }

    fun spotlightOn(iteamList: ArrayList<Product>){
        val adaptor = SpotlightOnAdaptor(requireContext(),iteamList)
        binding.recyclerView2.adapter = adaptor
        binding.recyclerView2.layoutManager = StaggeredGridLayoutManager(2,1)
    }

    fun getSizes(iteamList: ArrayList<Sizes>){
        val adaptor = ShopBySizeAdaptor(requireContext(),iteamList)
        binding.recyclerView3.adapter = adaptor
        binding.recyclerView3.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false) }

    fun getTraditional(iteamList: ArrayList<Product>){
        val adaptor = TraditonalAdaptor(requireContext(),iteamList)
        binding.recyclerView4.adapter = adaptor
        binding.recyclerView4.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }

    fun getNewCollections(iteamList: ArrayList<Product>){
        val adaptor = TraditonalAdaptor(requireContext(),iteamList)
        binding.recyclerView6.adapter = adaptor
        binding.recyclerView6.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }

    fun getDeals(iteamList: ArrayList<Deal>) {
        val adaptor = DealAdaptor(requireContext(),iteamList)
        binding.recyclerView8.adapter = adaptor
        binding.recyclerView8.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        val handler = Handler()
        handler.postDelayed(
            Runnable {
                binding.shimmerViewContainerTopCategory.visibility = View.INVISIBLE
                binding.shimmerViewContainerTopCategory.stopShimmer()
                binding.recyclerView.visibility = View.VISIBLE

                binding.shimmerViewContainer.visibility = View.GONE
                binding.shimmerViewContainer.stopShimmer()
                binding.recyclerView2.visibility = View.VISIBLE
                dialog.dismiss()
            },800
        )






    }















}