package com.gayatriladieswears.app.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gayatriladieswears.app.Adaptors.TraditonalAdaptor
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentProductDetailBinding
import com.gayatriladieswears.app.databinding.FragmentShopingBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

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
    var id:String = ""
    var stock:String = ""
    var tag:ArrayList<String> = ArrayList()
    var size:ArrayList<String> = ArrayList()


    lateinit var dialog:Dialog


    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var auth: FirebaseAuth


    @SuppressLint("NewApi", "ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductDetailBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        id = arguments?.getString("id").toString()
        checkSizes()


        auth = FirebaseAuth.getInstance()



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
        stock = arguments?.getString("stock").toString()
        tag = arguments?.getStringArrayList("tag") as ArrayList<String>
        size = arguments?.getStringArrayList("sizes") as ArrayList<String>


        if(stock.toInt() == 0){
            binding.stockText.visibility = View.VISIBLE
            binding.stockText.text = "Out of Stock"
        }else if(stock.toInt() < 5 ){
            binding.stockText.visibility = View.VISIBLE
            binding.stockText.text = "Limited Stock"
        }else{
            binding.stockText.visibility = View.GONE
        }

        FirestoreClass().getCategorizeProduct(this,"category",category,id)



        

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


        binding.imageView8.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("image",image)
            findNavController().navigate(R.id.action_productDetailFragment_to_fullScreenFragment,bundle)
        }





        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.productDetailFragment) {
                activity?.onBackPressed()
            }
        }

        binding.cartBtn.setOnClickListener {
            findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
        }

        binding.addToBagBtn.setOnClickListener{
            if(stock == "0"){
                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Sorry this product is out of stock.", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                snackBar.setTextColor(resources.getColor(R.color.white))
                snackBar.show()
                vibratePhone()
            }else{
                if(binding.chipGroup.checkedChipIds.isEmpty()){
                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Please select the size", Snackbar.LENGTH_LONG)
                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                    snackBar.setTextColor(resources.getColor(R.color.white))
                    snackBar.show()
                    vibratePhone()
                }else{
                    FirestoreClass().checkProductExistInCart(this,id,auth.currentUser!!.uid)
                }
            }


        }


        return binding.root
    }
    fun getCategorizedProduct(iteamList: ArrayList<Product>){
        binding.nestedSrollView.smoothScrollBy(0,0)
        val adaptor = TraditonalAdaptor(requireContext(),iteamList)
        binding.recylerView.adapter = adaptor
        binding.recylerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

    }

    fun addToCart(){
        if(binding.chipGroup.isEmpty()){
            val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Please select the size.", Snackbar.LENGTH_SHORT)
            snackBar.setBackgroundTint(resources.getColor(R.color.red))
            snackBar.setTextColor(resources.getColor(R.color.white))
            snackBar.show()
            vibratePhone()
        }else{
            val size = binding.chipGroup.findViewById<Chip>(binding.chipGroup.checkedChipId).text.toString()
            val cartIteam = CartItem(id, auth.currentUser!!.uid, price.toInt(), mrp.toInt(), color, size, image, "1", name, dis)
            FirestoreClass().addToCart(this,cartIteam,auth.currentUser!!.uid,id)
            findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
        }

        }
       

    fun addToCartProductExist(){
        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Product is already present in bag.", Snackbar.LENGTH_LONG)
        snackBar.setBackgroundTint(resources.getColor(R.color.black))
        snackBar.setTextColor(resources.getColor(R.color.white))
        snackBar.show()

        findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
    }

    fun checkSizes(){

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","S")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChipS.isEnabled = true
                }else{
                    binding.productChipS.isEnabled = false
                    binding.productChipS.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChipS.setTextColor(resources.getColor(R.color.gray))
                }
            }

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","M")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChipM.isEnabled = true
                }else{
                    binding.productChipM.isEnabled = false
                    binding.productChipM.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChipM.setTextColor(resources.getColor(R.color.gray))
                }
            }

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","L")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChipL.isEnabled = true
                }else{
                    binding.productChipL.isEnabled = false
                    binding.productChipL.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChipL.setTextColor(resources.getColor(R.color.gray))
                }
            }

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","XL")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChipXl.isEnabled = true
                }else{
                    binding.productChipXl.isEnabled = false
                    binding.productChipXl.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChipXl.setTextColor(resources.getColor(R.color.gray))
                }
            }

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","XXL")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChipXxl.isEnabled = true
                    dialog.dismiss()
                }else{
                    binding.productChipXxl.isEnabled = false
                    binding.productChipXxl.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChipXxl.setTextColor(resources.getColor(R.color.gray))
                    dialog.dismiss()
                }
            }

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","XXXL")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChipXxxl.isEnabled = true


                }else{
                    binding.productChipXxxl.isEnabled = false
                    binding.productChipXxxl.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChipXxxl.setTextColor(resources.getColor(R.color.gray))

                }
            }


        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","4XL")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChip4xl.isEnabled = true

                }else{
                    binding.productChip4xl.isEnabled = false
                    binding.productChip4xl.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChip4xl.setTextColor(resources.getColor(R.color.gray))

                }
            }

        FirestoreClass().mFirestore.collection("Products")
            .whereEqualTo("id",id)
            .whereArrayContains("size","5XL")
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    binding.productChip5xl.isEnabled = true

                }else{
                    binding.productChip5xl.isEnabled = false
                    binding.productChip5xl.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this.requireContext(), R.color.gray2))
                    binding.productChip5xl.setTextColor(resources.getColor(R.color.gray))


                }
            }




    }








}