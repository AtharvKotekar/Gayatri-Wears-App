package com.gayatriladieswears.app.Fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.Adaptors.TraditonalAdaptor
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentProductDetailBinding
import com.gayatriladieswears.app.databinding.FragmentShopingBinding

class ProductDetailFragment : Fragment() {

    var name:String = ""
    var price:String = ""
    var dis:String = ""
    var fabric:String = ""
    var image:String = ""
    var category:String = ""
    var color:String = ""
    var pattern:String = ""
    var occasion:String = ""
    var mrp:String = ""
    var deal:String = ""
    var tag:ArrayList<String> = ArrayList()
    var size:ArrayList<String> = ArrayList()

    private lateinit var binding: FragmentProductDetailBinding


    @SuppressLint("NewApi", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductDetailBinding.inflate(inflater,container,false)



        name = arguments?.getString("name").toString()
        price = arguments?.getString("price").toString()
        dis = arguments?.getString("dis").toString()
        fabric = arguments?.getString("fabric").toString()
        image = arguments?.getString("image").toString()
        category = arguments?.getString("category").toString()
        color = arguments?.getString("color").toString()
        pattern = arguments?.getString("pattern").toString()
        occasion = arguments?.getString("occasion").toString()
        mrp = arguments?.getString("mrp").toString()
        tag = arguments?.getStringArrayList("tag") as ArrayList<String>
        size = arguments?.getStringArrayList("sizes") as ArrayList<String>
        FirestoreClass().getCategorizeProduct(this,"category",category,name)

        Glide
            .with(requireContext())
            .load(image)
            .placeholder(R.drawable.baseline_shopping_bag_24)
            .centerCrop()
            .into(binding.imageView8)


        binding.productMrpText.text = mrp
        binding.productMrpText.setPaintFlags(binding.productMrpText.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        binding.productNameTextTop.text = name.toString()
        binding.produxtPriceTextTop.text = price.toString()
        binding.productName.text = name
        binding.productDisText.text = dis
        binding.productPriceText.text = price
        binding.productCategoryText.text = category
        binding.productColorText.text = color
        binding.productFabricText.text = fabric
        binding.productOccasionText.text = occasion
        binding.productPatternText.text = pattern






        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.productDetailFragment) {
                activity?.onBackPressed()
            }
        }

        binding.cartBtn.setOnClickListener {
            findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
        }

        binding.addToBagBtn.setOnClickListener{
            findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
        }


        return binding.root
    }

    fun getCategorizedProduct(iteamList: ArrayList<Product>){
        binding.nestedSrollView.smoothScrollBy(0,0)
        val adaptor = TraditonalAdaptor(requireContext(),iteamList)
        binding.recylerView.adapter = adaptor
        binding.recylerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }






}