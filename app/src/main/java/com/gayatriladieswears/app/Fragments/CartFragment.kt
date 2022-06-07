package com.gayatriladieswears.app.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.firebase.auth.FirebaseAuth


class CartFragment : Fragment() {
    lateinit var binding:FragmentCartBinding
    lateinit var auth:FirebaseAuth
    lateinit var dialog:Dialog
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

        return binding.root
    }

    fun getCartProducts(iteamList: ArrayList<CartItem>) {
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
            var mPrice = 0
            var mMrp = 0
            for (i in iteamList){
                mPrice = mPrice + (i.price * i.cartQuantity.toInt())
                mMrp = mMrp + (i.mrp * i. cartQuantity.toInt())
            }
            binding.bagPrice.text = mMrp.toString()
            binding.bagDiscount.text = "- ${(mMrp - mPrice)}"
            binding.totalPrice.text = (mMrp - (mMrp - mPrice)).toString()
            binding.bottomPrice.text = mPrice.toString()
            binding.iteamSize.text = "Your Cart - ${iteamList.size} Items"
            binding.shippingCharges.text = "0"
            val adaptor = CartAdaptor(requireContext(),this,iteamList)
            binding.recyclerView9.adapter = adaptor
            binding.recyclerView9.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
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