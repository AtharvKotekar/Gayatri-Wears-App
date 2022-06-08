package com.gayatriladieswears.app.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.location.LocationRequestCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Adaptors.TopCategoriesAdaptor
import com.gayatriladieswears.app.FirestoreClass
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.R
import com.gayatriladieswears.app.databinding.FragmentCartBinding
import com.gayatriladieswears.app.vibratePhone
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class CartFragment : Fragment() {
    lateinit var binding:FragmentCartBinding
    lateinit var auth:FirebaseAuth
    lateinit var dialog:Dialog
    var outOfStockPresent = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        auth = FirebaseAuth.getInstance()

        FirestoreClass().getCartProducts(this,auth.currentUser!!.uid)

        binding.viewDetails.setOnClickListener {
            binding.ScrollView.smoothScrollBy(10000,10000,1000)
        }

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.cartFragment) {
                activity?.onBackPressed()
            }
        }

        binding.placeOrderButton.setOnClickListener {
            if(outOfStockPresent == true){
                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Please remove 'Out of Stock' Products from bag.", Snackbar.LENGTH_SHORT)
                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                snackBar.setTextColor(resources.getColor(R.color.white))
                snackBar.show()
                vibratePhone()
            }
            else{
                findNavController().navigate(R.id.action_cartFragment_to_orderAddressFragment)
            }
        }

        return binding.root
    }

    fun getCartProducts(iteamList: ArrayList<CartItem>) {
        outOfStockPresent = false
        if(iteamList.isEmpty()){
            binding.iteamSize.text = "Your Cart - 0 Items"
            binding.emtyCartImage.visibility = View.VISIBLE
            binding.emptyCartText.visibility = View.VISIBLE
            binding.shopBtn.visibility = View.VISIBLE
            binding.recyclerView9.visibility = View.GONE
            binding.stickyBottom.visibility = View.GONE
            binding.priceSummary.visibility = View.GONE
            binding.view15.visibility = View.GONE
            binding.view16.visibility = View.GONE
            dialog.dismiss()
        }else{
            binding.emtyCartImage.visibility = View.GONE
            binding.emptyCartText.visibility = View.GONE
            binding.shopBtn.visibility = View.GONE
            binding.recyclerView9.visibility = View.VISIBLE
            binding.stickyBottom.visibility = View.VISIBLE
            binding.priceSummary.visibility = View.VISIBLE
            binding.view15.visibility = View.VISIBLE
            binding.view16.visibility = View.VISIBLE
            val adaptor = CartAdaptor(requireContext(),this,iteamList)
            binding.recyclerView9.adapter = adaptor
            binding.recyclerView9.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            var mPrice = 0
            var mMrp = 0
            var mShippingCharges = 0
            for (i in iteamList){
                mPrice = mPrice + (i.price * i.cartQuantity.toInt())
                mMrp = mMrp + (i.mrp * i. cartQuantity.toInt())
            }
            if((mMrp - (mMrp - mPrice)) < 699){
                mShippingCharges = 99
            }else{
                mShippingCharges = 0
            }
            binding.iteamSize.text = "Your Cart - ${iteamList.size} Items"
            binding.bagPrice.text = mMrp.toString()
            binding.bagDiscount.text = "- ${(mMrp - mPrice)}"
            binding.shippingCharges.text = mShippingCharges.toString()
            binding.totalPrice.text = (mMrp - (mMrp - mPrice) + mShippingCharges).toString()
            binding.bottomPrice.text = (mMrp - (mMrp - mPrice) + mShippingCharges).toString()



            dialog.dismiss()
        }

    }

    fun getProductById(iteamList:ArrayList<Product>,holder: CartAdaptor.myViewHolder){
        val bundle = Bundle()
        for (i in iteamList){
            bundle.putString("name",i.name)
            bundle.putString("price",i.price.toString())
            bundle.putString("dis",i.dis)
            bundle.putString("fabric",i.fabric)
            bundle.putString("image",i.image)
            bundle.putString("category",i.category)
            bundle.putString("color",i.color)
            bundle.putString("pattern",i.pattern)
            bundle.putString("occasion",i.occasion)
            bundle.putString("mrp",i.mrp.toString())
            bundle.putString("id",i.id)
            bundle.putStringArrayList("sizes",i.size)
            bundle.putStringArrayList("tag",i.tag)
        }
        dialog.dismiss()
        holder.itemView.findNavController()
            .navigate(R.id.action_cartFragment_to_productDetailFragment, bundle)
    }



}