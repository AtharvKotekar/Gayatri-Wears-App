package com.gayatriladieswears.app

import android.app.Dialog
import android.content.ContentValues.TAG
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.databinding.FragmentCheckOutBinding
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAmount


class CheckOutFragment : Fragment() {

    private lateinit var binding:FragmentCheckOutBinding
    private lateinit var productId:String
    private lateinit var amount:String
    private lateinit var name:String
    private lateinit var phone:String
    private lateinit var pincode:String
    private lateinit var address:String
    private lateinit var landmark:String
    private lateinit var addressTag:String
    private lateinit var addressId:String
    lateinit var dialog:Dialog
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckOutBinding.inflate(inflater,container,false)

        Checkout.preload(requireContext())

        name = arguments?.getString("name").toString()
        phone = arguments?.getString("phone").toString()
        pincode = arguments?.getString("pincode").toString()
        address = arguments?.getString("address").toString()
        landmark = arguments?.getString("landmark").toString()
        addressTag = arguments?.getString("addressTag").toString()
        addressId = arguments?.getString("addressId").toString()


        dialog = Dialog(requireContext())
        dialog.setContentView(com.gayatriladieswears.app.R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        FirestoreClass().getCartProducts(this,FirebaseAuth.getInstance().currentUser!!.uid)



        binding.checkoutName.text = name
        binding.checkoutPhoneText.text = phone
        binding.addressTagText.text = addressTag
        binding.checkoutAddressText.text = address

        binding.checkoutViewDetails.setOnClickListener {
            binding.ScrollViewCheckout.smoothScrollTo(100000,100000,1000)
        }

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.checkOutFragment) {
                activity?.onBackPressed()
            }
        }

        binding.checkoutChangeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_checkOutFragment_to_orderAddressFragment)
        }

        binding.payBtn.setOnClickListener {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm")
            val formatted = current.format(formatter)
            startPayment(amount,formatted)
        }

        Log.i(TAG, "onCreateView: $id")

        return binding.root
    }

    fun getAllCartProducts(iteamList: ArrayList<CartItem>){
        val adaptor = CartAdaptor(requireContext(),this,iteamList)
        binding.recyclerView10.adapter = adaptor
        binding.recyclerView10.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
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
        binding.checkoutPrice.text = mMrp.toString()
        binding.checkoutDiscount.text = "- ${(mMrp - mPrice)}"
        binding.checkoutShippingCharges.text = mShippingCharges.toString()
        amount = ((mMrp - (mMrp - mPrice) + mShippingCharges) * 100).toString()
        binding.checkoutTotalPrice.text = (mMrp - (mMrp - mPrice) + mShippingCharges).toString()
        binding.checkotBottomPrice.text = (mMrp - (mMrp - mPrice) + mShippingCharges).toString()
        dialog.dismiss()
    }


    private fun startPayment(amount: String,time:String) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val co = Checkout()
        co.setKeyID("rzp_test_DHlYWw0aTc4Usu")

        try {
            val options = JSONObject()
            options.put("name","Gayatri Ladies Wears")
            options.put("description","Order Payment")
            options.put("image","https://cdn.razorpay.com/logos/JevSZyzeyJ9mv2_medium.png")
            options.put("theme.color", "#1D1D1F")
            options.put("currency","INR")
            options.put("amount",amount)//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }



}