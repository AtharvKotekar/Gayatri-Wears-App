package com.gayatriladieswears.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Model.CartItem
import com.gayatriladieswears.app.Model.Order
import com.gayatriladieswears.app.Model.Product
import com.gayatriladieswears.app.databinding.FragmentCheckOutBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.razorpay.Checkout
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
    private lateinit var productList:ArrayList<CartItem>
    private var totalAmount:Int = 0
    lateinit var dialog:Dialog
    private var totalquantiyt:Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckOutBinding.inflate(inflater,container,false)
        productList = ArrayList()
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
            startPayment(amount)
        }







        Log.i(TAG, "onCreateView: $id")

        return binding.root
    }

    fun getAllCartProducts(iteamList: ArrayList<CartItem>){
        productList.clear()
        productList.addAll(iteamList)
        for(i in productList){
            totalquantiyt = totalquantiyt+i.cartQuantity.toInt()
        }
        val adaptor = CartAdaptor(requireContext(),this,iteamList)
        binding.recyclerView10.adapter = adaptor
        binding.recyclerView10.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        var mPrice = 0
        var mMrp = 0
        var mShippingCharges = 0
        totalAmount = 0
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
        totalAmount = (mMrp - (mMrp - mPrice) + mShippingCharges)
        binding.checkotBottomPrice.text = (mMrp - (mMrp - mPrice) + mShippingCharges).toString()
        dialog.dismiss()
    }



    fun startPayment(amount: String) {

        val co = Checkout()


        try {
            val options = JSONObject()
            options.put("name","Gayatri Ladies Wears")
            options.put("description","Order Payment")
            options.put("image","https://cdn.razorpay.com/logos/JevSZyzeyJ9mv2_medium.png")
            options.put("theme.color", "#1D1D1F")
            options.put("currency","INR")
            options.put("send_sms_hash",true)
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

    fun checkRazorResponse(p0: String?, b: Boolean) {
        dialog.show()
            onResume(b,p0)
    }

    @SuppressLint("NewApi")
    fun onResume(success:Boolean,transactionId:String?) {
        super.onResume()
        if(success){
            val handler = Handler()
            handler.postDelayed(
                Runnable {
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss")
                    val formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    val formatted = current.format(formatter)
                    val formattedDate = current.format(formatter2)


                    val order = Order(name!!,addressTag!!,address!!,pincode!!,landmark!!,phone!!,FirebaseAuth.getInstance().currentUser!!.uid!!,addressId!!,productList!!,
                        transactionId.toString()!!,totalAmount!!,FirebaseAuth.getInstance().currentUser!!.uid.toString() + "_" + formatted!!,
                        "Packing"!!,formattedDate,""!!,totalquantiyt.toString()!!)

                    FirestoreClass().mFirestore.collection("Orders")
                        .document(FirebaseAuth.getInstance().currentUser!!.uid.toString() + "_" + formatted)
                        .set(order, SetOptions.merge())
                        .addOnSuccessListener {
                            dialog.dismiss()
                            findNavController().navigate(R.id.action_checkOutFragment_to_paymentSuccessFragment)
                            for(i in productList){
                                var stock = 0
                                FirestoreClass().removeCartProduct(this,i.productId,i.userId)
                                FirestoreClass().mFirestore.collection("Products")
                                    .whereEqualTo("id",i.productId)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        val products:ArrayList<Product> = ArrayList()
                                        for(j in document.documents){
                                            val iteam = j.toObject(Product::class.java)!!
                                            stock = (iteam.stock.toInt() - i.cartQuantity.toInt())
                                            products.add(iteam)
                                        }
                                        FirestoreClass().mFirestore.collection("Products").document(i.name).update("stock",stock)
                                    }

                            }
                        }
                },1
            )

        }else{
            val handler = Handler()
            handler.postDelayed(
                Runnable {

                    dialog.dismiss()
                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Failed please try again.", Snackbar.LENGTH_LONG)
                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                    snackBar.setTextColor(resources.getColor(R.color.white))
                    snackBar.show()
                    vibratePhone()
                },1
            )
        }

    }
}