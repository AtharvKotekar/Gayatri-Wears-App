package com.gayatriladieswears.app.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gayatriladieswears.app.*
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Model.*
import com.gayatriladieswears.app.databinding.FragmentCheckOutBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.gson.JsonObject
import com.razorpay.Checkout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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
    private lateinit var emailId:String
    private lateinit var productList:ArrayList<CartItem>
    private var totalAmount:Int = 0
    lateinit var dialog:Dialog
    private var totalquantiyt:Int = 0
    lateinit var mRequestQueue:RequestQueue
    var city:String = ""
    var state:String = ""
    var country:String = ""
    var token:String = ""

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
        emailId = arguments?.getString("email").toString()


        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.laoding_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()

        FirestoreClass().getCartProducts(this,FirebaseAuth.getInstance().currentUser!!.uid)

        mRequestQueue = Volley.newRequestQueue(requireContext())


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

        CoroutineScope(IO).launch {

            mRequestQueue.cache.clear()

            val url = "http://www.postalpincode.in/api/pincode/${pincode.toString().trim()}"

            val queue = Volley.newRequestQueue(requireContext())

            val objectRequest =
                JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        try {

                            val postOfficeArray = response?.getJSONArray("PostOffice")
                            if (response?.getString("Status") == "Error") {
                                dialog.dismiss()
                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Found Invalid Pincode in Address Please try again after Changeing Address.", Snackbar.LENGTH_LONG)
                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                snackBar.setTextColor(resources.getColor(R.color.white))
                                snackBar.show()
                                vibratePhone()
                            } else {
                                dialog.dismiss()
                                val obj = postOfficeArray?.getJSONObject(0)
                                city = obj?.getString("District").toString()
                                state = obj?.getString("State").toString()
                                country = obj?.getString("Country").toString()
                                Log.e(TAG, "onResume: $city  $state  $country")




                            }
                        } catch (e: JSONException) {
                            dialog.dismiss()
                            val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Went Wrong.", Snackbar.LENGTH_LONG)
                            snackBar.setBackgroundTint(resources.getColor(R.color.red))
                            snackBar.setTextColor(resources.getColor(R.color.white))
                            snackBar.show()
                            vibratePhone()
                        }
                    }) {

                    Log.e(TAG, "onResume: $it.")
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Pin code is not valid.", Toast.LENGTH_SHORT)
                        .show()

                }

            queue.add(objectRequest)
        }





            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            val orderServices = retrofitBuilder.create(OrderServices::class.java)

            val tokenc = Token("gayatriauth@gmail.com","Gayatri@2022")
            val call = orderServices.getToken(tokenc)

            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            call.enqueue(object : Callback<TokenCall>{
                override fun onResponse(
                    call: Call<TokenCall>,
                    response: Response<TokenCall>) {

                    token = "Bearer ${response.body()!!.token} "


                }

                override fun onFailure(call: Call<TokenCall>, t: Throwable) {
                    Log.e(TAG, "onResponse: ${t.localizedMessage}")
                }

            })



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
        if((mMrp - (mMrp - mPrice)) < 799){
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
        binding.checkoutTotalPrice.text = (mMrp - (mMrp - mPrice) + mShippingCharges).toString()
        dialog.dismiss()
    }



    fun startPayment(amount: String) {

        val co = Checkout()


        try {
            val options = JSONObject()
            options.put("name","Gayatri Ladies Wears")
            options.put("description","Order Payment")
            options.put("image","https://cdn.razorpay.com/logos/JevSZyzeyJ9mv2_medium.png")
            options.put("theme.color", "#FF0852")
            options.put("currency","INR")
            options.put("send_sms_hash",true)
            options.put("amount",amount)//pass amount in currency subunits

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

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
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formattedDate = current.format(formatter)
                    val orderId = getRandomString(20)
                    var shipmentid:String = ""

                    var shippingCharges:Int = 0

                    if((amount.toString().toInt()/100 )< 799){
                        shippingCharges = 99
                    }else{
                        shippingCharges = 0
                    }

                    Log.e(TAG, "onResponse: $token " )

                    val orderShipArray = ArrayList<OrderShipItem>()

                    for (i in productList){
                        val orderShipItem = OrderShipItem(i.name,i.productId,i.cartQuantity.toString().toInt(),i.price,0,0,"")
                        orderShipArray.add(orderShipItem)
                    }


                    val orderShip = OrderShip(
                        orderId, formattedDate.toString(), "Primary", "", "", "", "",
                        name, "", address, landmark,city,pincode.toString().toInt(),state,country,emailId,phone.toString().replace("+91","").toLong(),"",false,
                        name,"",address,landmark,"",city,pincode.toString().toInt(),country,state,emailId,phone.toString().replace("+91","").toLong(),"","",
                        orderShipArray,"Prepaid",shippingCharges,0,0,0,((amount.toString().toInt()/100) - shippingCharges),1f,1f,1f,
                        1f,"","","",""
                    )




                    val retrofitBuilder = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(BASE_URL)
                        .build()

                    val orderServices = retrofitBuilder.create(OrderServices::class.java)

                    val headerMap:HashMap<String,String> = HashMap<String,String>()
                    headerMap["Content-Encoding"] = "gzip, deflate, b"
                    headerMap["Connection"] = "keep-alive"
                    headerMap["Accept"] = "*/*"
                    headerMap["Content-Type"] = "application/json;charset=UTF-8"
                    headerMap["Cache-Control"] = "private, must-revalidate"
                    headerMap["ETag"] = "b6f88565113db5cce836da75fe716815d8fde80c"
                    headerMap["Server"] = "nginx"
                    headerMap["Vary"] = "Accept-Encoding"
                    headerMap["Authorization"] = token


                        val orderCall = orderServices.setOrder(headerMap, orderShip)
                        orderCall.enqueue(object :Callback<OrderShipResult>{
                            override fun onResponse(
                                call: Call<OrderShipResult>,
                                response: Response<OrderShipResult>) {
                                if(response.isSuccessful){
                                    shipmentid = response.body()?.shipment_id.toString()
                                    val orderID = response.body()?.order_id.toString()
                                    Log.e(TAG, "onResponse: Successes")
                                    Log.e(TAG, "onResponse: ${response.code()}")
                                    Log.e(TAG, "onResponse: ${response.body()?.shipment_id}", )
                                    Log.e(TAG, "onResponse: ${response.body()?.status_code}", )
                                    Log.e(TAG, "onResponse: ${response.body()?.order_id}", )
                                    Log.e(TAG, "onResponse: ${response.body()?.status}", )



                                    if(response.body()?.status == "CANCELED"){
                                        Toast.makeText(requireContext(), "Please Wait Getting Done", Toast.LENGTH_LONG).show()
                                        orderCall.enqueue(object :Callback<OrderShipResult>{
                                            override fun onResponse(
                                                call: Call<OrderShipResult>,
                                                response2: Response<OrderShipResult>
                                            ) {
                                                if(response2.isSuccessful){
                                                    createOrder(transactionId.toString(),orderID,shipmentid,response2)
                                                }else{
                                                    Log.e(TAG, "onResponse: ${response2.code()}")
                                                    Log.e(TAG, "onResponse: ${response2.message()}")
                                                    Log.e(TAG, "onResponse: ${response2.raw()}")
                                                    Log.e(TAG, "onResponse: ${response2.headers()}")
                                                    dialog.dismiss()
                                                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Wents Wrong.E - Order Failed", Snackbar.LENGTH_LONG)
                                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                                    snackBar.show()
                                                    vibratePhone()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<OrderShipResult>,
                                                t: Throwable
                                            ) {
                                                dialog.dismiss()
                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Wents Wrong.E - Order Failed", Snackbar.LENGTH_LONG)
                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                snackBar.show()
                                                vibratePhone()
                                            }

                                        })
                                    }else if(response.body()?.status == "CANCELLATION REQUESTED"){
                                        Toast.makeText(requireContext(), "Please Wait Getting Done", Toast.LENGTH_LONG).show()
                                        orderCall.enqueue(object :Callback<OrderShipResult>{
                                            override fun onResponse(
                                                call: Call<OrderShipResult>,
                                                response2: Response<OrderShipResult>
                                            ) {
                                                if(response2.isSuccessful){
                                                    createOrder(transactionId.toString(),orderID,shipmentid,response2)
                                                }else{
                                                    Log.e(TAG, "onResponse: ${response2.code()}")
                                                    Log.e(TAG, "onResponse: ${response2.message()}")
                                                    Log.e(TAG, "onResponse: ${response2.raw()}")
                                                    Log.e(TAG, "onResponse: ${response2.headers()}")
                                                    dialog.dismiss()
                                                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Wents Wrong.E - Order Failed", Snackbar.LENGTH_LONG)
                                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                                    snackBar.show()
                                                    vibratePhone()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<OrderShipResult>,
                                                t: Throwable
                                            ) {
                                                dialog.dismiss()
                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Wents Wrong.E - Order Failed", Snackbar.LENGTH_LONG)
                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                snackBar.show()
                                                vibratePhone()
                                            }

                                        })
                                    }else{
                                        createOrder(transactionId.toString(),orderID,shipmentid,response)
                                    }







                                }else{
                                    Log.e(TAG, "onResponse: ${response.code()}")
                                    Log.e(TAG, "onResponse: ${response.message()}")
                                    Log.e(TAG, "onResponse: ${response.raw()}")
                                    Log.e(TAG, "onResponse: ${response.headers()}")
                                    dialog.dismiss()
                                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Wents Wrong.E - Order Failed", Snackbar.LENGTH_LONG)
                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                    snackBar.show()
                                    vibratePhone()
                                }


                            }

                            override fun onFailure(call: Call<OrderShipResult>, t: Throwable) {
                                Log.e(
                                    TAG,
                                    "onFailure: ${t.localizedMessage}"
                                )
                                dialog.dismiss()
                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Wents Wrong. Order Failed", Snackbar.LENGTH_LONG)
                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                snackBar.setTextColor(resources.getColor(R.color.white))
                                snackBar.show()
                                vibratePhone()
                            }

                        })




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

    fun createOrder(transactionId:String,orderID:String,shipmentid:String,response:Response<OrderShipResult>){

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = current.format(formatter)

        val headerMap:HashMap<String,String> = HashMap<String,String>()
        headerMap["Content-Encoding"] = "gzip, deflate, b"
        headerMap["Connection"] = "keep-alive"
        headerMap["Accept"] = "*/*"
        headerMap["Content-Type"] = "application/json;charset=UTF-8"
        headerMap["Cache-Control"] = "private, must-revalidate"
        headerMap["ETag"] = "b6f88565113db5cce836da75fe716815d8fde80c"
        headerMap["Server"] = "nginx"
        headerMap["Vary"] = "Accept-Encoding"
        headerMap["Authorization"] = token

        val order = Order(
            name,
            addressTag,
            address,
            pincode,
            landmark,
            phone,
            FirebaseAuth.getInstance().currentUser!!.uid,
            addressId,
            productList,
            transactionId.toString(),
            totalAmount,
            orderID,
            formattedDate,
            "",
            totalquantiyt.toString(),
            emailId
        )

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        val orderServices = retrofitBuilder.create(OrderServices::class.java)

                    dialog.dismiss()
                    findNavController().navigate(R.id.action_checkOutFragment_to_paymentSuccessFragment)

                    val retrofitBuilder2 =
                        Retrofit.Builder()
                            .addConverterFactory(
                                GsonConverterFactory.create()
                            )
                            .baseUrl(BASE_URL_RZP)
                            .build()

                    val orderServices2 =
                        retrofitBuilder2.create(
                            OrderServices::class.java
                        )

                    val captureCall =
                        orderServices2.capturePayment(
                            transactionId.toString(),
                            amount.toString().toInt(),
                            "INR"
                        )

                    CoroutineScope(IO).launch {
                        captureCall.enqueue(object :
                            Callback<JsonObject> {
                            override fun onResponse(
                                call: Call<JsonObject>,
                                response: Response<JsonObject>
                            ) {
                                if (response.isSuccessful) {

                                } else {
                                    Log.e(
                                        TAG,
                                        "onResponse: ${response.raw()}",
                                    )
                                    Log.e(
                                        TAG,
                                        "onResponse: ${
                                            response.errorBody()
                                                .toString()
                                        }",
                                    )
                                }


                            }

                            override fun onFailure(
                                call: Call<JsonObject>,
                                t: Throwable
                            ) {
                                val snackBar =
                                    Snackbar.make(
                                        requireActivity().findViewById(
                                            android.R.id.content
                                        ),
                                        "Something Wents Wrong.",
                                        Snackbar.LENGTH_LONG
                                    )
                                snackBar.setBackgroundTint(
                                    resources.getColor(R.color.red)
                                )
                                snackBar.setTextColor(
                                    resources.getColor(R.color.white)
                                )
                                snackBar.show()
                                vibratePhone()
                            }

                        })

                        FirestoreClass().mFirestore.collection(
                            "Orders"
                        )
                            .document(orderID)
                            .set(order, SetOptions.merge())
                            .addOnSuccessListener {
                                for (i in productList) {
                                    var stock = 0
                                    FirestoreClass().removeCartProduct(
                                        this@CheckOutFragment,
                                        i.productId,
                                        i.userId
                                    )
                                    FirestoreClass().mFirestore.collection(
                                        "Products"
                                    )
                                        .whereEqualTo(
                                            "id",
                                            i.productId
                                        )
                                        .get()
                                        .addOnSuccessListener { document ->
                                            val products: ArrayList<Product> =
                                                ArrayList()
                                            for (j in document.documents) {
                                                val iteam =
                                                    j.toObject(
                                                        Product::class.java
                                                    )!!
                                                stock =
                                                    (iteam.stock.toInt() - i.cartQuantity.toInt())
                                                products.add(
                                                    iteam
                                                )
                                            }
                                            FirestoreClass().mFirestore.collection(
                                                "Products"
                                            )
                                                .document(orderID)
                                                .update(
                                                    "stock",
                                                    stock
                                                )
                                        }

                                }
                            }
                    }




    }




}



