package com.gayatriladieswears.app.Fragments

import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Color.red
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gayatriladieswears.app.*
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Model.*
import com.gayatriladieswears.app.databinding.FragmentOrderDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class OrderDetailFragment : Fragment() {

    private lateinit var binding:FragmentOrderDetailBinding
    private lateinit var orderId:String
    private lateinit var transactionId:String
    private lateinit var date:String
    private lateinit var name:String
    private lateinit var address:String
    private lateinit var contact:String
    private lateinit var tAg:String
    private lateinit var products:ArrayList<CartItem>
    private lateinit var amount:String
    private lateinit var awb:String
    private lateinit var pincode:String
    private lateinit var email:String
    private lateinit var landmark:String
    private lateinit var shippingCharges:String
    var city:String = ""
    var state:String = ""
    var country:String = ""
    lateinit var mdialog:Dialog
    var token = ""
    var orderID = ""
    var checkOrderID = ""
    var edd = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater,container,false)



        mdialog = Dialog(requireContext())
        mdialog.setContentView(R.layout.laoding_dialog)
        mdialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mdialog.setCancelable(false)
        mdialog.show()


        orderId = arguments?.getString("orderId").toString()
        transactionId = arguments?.getString("transactionId").toString()
        date = arguments?.getString("date").toString()
        name = arguments?.getString("name").toString()
        address = arguments?.getString("address").toString()
        contact = arguments?.getString("contact").toString()
        tAg = arguments?.getString("tag").toString()
        products = arguments?.getParcelableArrayList<CartItem>("products") as ArrayList<CartItem>
        amount = arguments?.getString("amount").toString()
        awb = arguments?.getString("awb").toString()
        pincode = arguments?.getString("pincode").toString()
        email = arguments?.getString("email").toString()
        landmark = arguments?.getString("landmark").toString()
        shippingCharges = arguments?.getString("shippingCharges").toString()

        Log.e(TAG, "onCreateView: $awb")
        
        

        

        FirestoreClass().getOrderDetailProducts(this,orderId)

        binding.orderIdText.text = "Order ID  -  $orderId"
        binding.orderTransactionIdText.text = "Transaction ID  -  $transactionId"
        binding.orderDateText.text = "Ordered Date  -  $date"
        binding.checkoutName.text = name
        binding.checkoutAddressText.text = address
        binding.checkoutPhoneText.text = contact
        binding.addressTagText.text = tAg
        binding.totalAmount.text = amount

        Log.e(TAG, "onResponse: ------------ $orderId", )

        CoroutineScope(Dispatchers.IO).launch {

            val url = "http://www.postalpincode.in/api/pincode/${pincode.toString().trim()}"

            val queue = Volley.newRequestQueue(requireContext())

            val objectRequest =
                JsonObjectRequest(
                    Request.Method.GET, url, null,
                    { response ->
                        try {

                            val postOfficeArray = response?.getJSONArray("PostOffice")
                            if (response?.getString("Status") == "Error") {
                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Found Invalid Pincode in Address Please try again after Changeing Address.", Snackbar.LENGTH_LONG)
                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                snackBar.setTextColor(resources.getColor(R.color.white))
                                snackBar.show()
                                vibratePhone()
                            } else {
                                val obj = postOfficeArray?.getJSONObject(0)
                                city = obj?.getString("District").toString()
                                state = obj?.getString("State").toString()
                                country = obj?.getString("Country").toString()
                                Log.e(ContentValues.TAG, "onResume: $city  $state  $country")




                            }
                        } catch (e: JSONException) {
                            mdialog.dismiss()
                            val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Went Wrong.", Snackbar.LENGTH_LONG)
                            snackBar.setBackgroundTint(resources.getColor(R.color.red))
                            snackBar.setTextColor(resources.getColor(R.color.white))
                            snackBar.show()
                            vibratePhone()
                        }
                    }) {

                    Log.e(ContentValues.TAG, "onResume: $it.")
                    mdialog.dismiss()
                    Toast.makeText(requireContext(), "Pin code is not valid.", Toast.LENGTH_SHORT)
                        .show()

                }

            queue.add(objectRequest)
        }




        CoroutineScope(Dispatchers.IO).launch {
            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            val orderServices = retrofitBuilder.create(OrderServices::class.java)

            val tokenc = Token("gayatriauth@gmail.com","Gayatri@2022")
            val call = orderServices.getToken(tokenc)

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            call.enqueue(object : Callback<TokenCall> {
                override fun onResponse(
                    call: Call<TokenCall>,
                    response: Response<TokenCall>
                ) {
                    token = "Bearer ${response.body()!!.token}"
                    Log.e(TAG, "onResponse: $token")

                    val headerMap:HashMap<String,String> = HashMap<String,String>()
                    headerMap["Content-Type"] = "application/json"
                    headerMap["Authorization"] = token

                        if(awb == ""){
                            edd = date
                            if("R" in orderId){
                                binding.cancelBtn.visibility = View.GONE
                                binding.cancelBtn.isClickable = false
                                binding.trackBtn.visibility = View.GONE
                                binding.cancelBtn.isClickable = false
                                binding.orderStatusText.text = "Returned"
                                binding.orderStatusText.setTextColor(resources.getColor( R.color.gray))
                                binding.view18.visibility = View.INVISIBLE
                                mdialog.dismiss()
                            }else if("C" in orderId){
                                binding.cancelBtn.visibility = View.GONE
                                binding.cancelBtn.isClickable = false
                                binding.trackBtn.visibility = View.GONE
                                binding.cancelBtn.isClickable = false
                                binding.orderStatusText.text = "Canceled"
                                binding.orderStatusText.setTextColor(resources.getColor( R.color.red))
                                binding.view18.visibility = View.INVISIBLE
                                mdialog.dismiss()
                            }else{
                                val ordercall = orderServices.getOrderDetail(headerMap,orderId)
                                ordercall.enqueue(object :Callback<JsonObject>{
                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>) = if(response.isSuccessful){

                                        val data = response.body()?.getAsJsonObject("data")
                                        val status = data?.get("status")
                                        orderID = data?.get("id").toString()
                                        Log.e(TAG, "onResponse: $orderID")
                                        binding.orderStatusText.text = status.toString().replace("\"", "").lowercase().capitalize()
                                        mdialog.dismiss()
                                        val current = LocalDateTime.now()
                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        val formattedDate = current.format(formatter).toString()

                                        val todayDate = SimpleDateFormat("yyyy-MM-dd").parse(formattedDate)
                                        val expiryDate = SimpleDateFormat("yyyy-MM-dd").parse(getCalculatedDate(edd, "yyyy-MM-dd",6))
                                        Log.e(TAG, "onResponse: $todayDate")
                                        Log.e(TAG, "onResponse: $expiryDate")

                                        if(status.toString().replace("\"", "") == "DELIVERED"){
                                            binding.cancelBtn.text = "Return Order"
                                            if(todayDate.before(expiryDate)){
                                                binding.cancelBtn.setOnClickListener {

                                                    val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
                                                    dialog.setTitle("Return Order")
                                                    dialog.setMessage("Do you really want to Return the order?")
                                                    dialog.background = context?.resources!!.getDrawable(R.drawable.black_btn_bg)
                                                    dialog.setNegativeButton("Cancel") { dialog, which ->
                                                        dialog.dismiss()
                                                    }
                                                    dialog.setPositiveButton("Sure") { dialog, which ->
                                                        val dialog2 = MaterialAlertDialogBuilder(
                                                            requireContext(),
                                                            R.style.AppCompatAlertDialogStyle
                                                        )
                                                        dialog2.setTitle("Remember")
                                                        dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                        dialog2.background =
                                                            resources.getDrawable(R.drawable.black_btn_bg)
                                                        dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                            dialog.dismiss()
                                                            dialog2.dismiss()

                                                        }
                                                        dialog2.setPositiveButton("Agree") { dialog2, which ->

                                                            val bundle = Bundle()
                                                            bundle.putString("orderID",orderID)
                                                            bundle.putString("transactionID",transactionId)
                                                            bundle.putString("name",name)
                                                            bundle.putString("address",address)
                                                            bundle.putString("city",city)
                                                            bundle.putString("state",state)
                                                            bundle.putString("country",country)
                                                            bundle.putString("pincode",pincode)
                                                            bundle.putString("email",email)
                                                            bundle.putString("phone",contact)
                                                            bundle.putString("amount",amount)
                                                            bundle.putString("shippingCharges",shippingCharges)
                                                            bundle.putParcelableArrayList("products", products as java.util.ArrayList<CartItem>)
                                                            findNavController().navigate(R.id.action_orderDetailFragment_to_orderReturnFragment,bundle)
                                                        }
                                                        dialog2.show()
                                                    }
                                                    dialog.show()

                                                }
                                            }else{
                                                binding.cancelBtn.setOnClickListener {
                                                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "The Order Can be Only Return Within 5 Days after Order.", 3000)
                                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                                    snackBar.show()
                                                    vibratePhone()
                                                }
                                            }
                                        }
                                        else if (status.toString().replace("\"", "") == "CANCELED"){
                                            binding.cancelBtn.visibility = View.INVISIBLE
                                            binding.cancelBtn.isClickable = false
                                        }
                                        else if (status.toString().replace("\"", "") == "CANCELLATION REQUESTED"){
                                            binding.cancelBtn.visibility = View.INVISIBLE
                                            binding.cancelBtn.isClickable = false
                                        }else if(status.toString().replace("\"", "") == "NEW"){
                                            binding.orderStatusText.text = "Packing"
                                            binding.trackBtn.setOnClickListener {
                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Please wait the order will be tracked soon.", 3000)
                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                snackBar.show()
                                                vibratePhone()
                                            }

                                            binding.cancelBtn.setOnClickListener {
                                                val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
                                                dialog.setTitle("Cancel Order")
                                                dialog.setMessage("Do you really want to cancel the order?")
                                                dialog.background = context?.resources!!.getDrawable(R.drawable.black_btn_bg)
                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                    dialog.dismiss()
                                                }
                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                    val dialog2 = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
                                                    dialog2.setTitle("Remember")
                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                    dialog2.background = resources.getDrawable(R.drawable.black_btn_bg)
                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                        dialog.dismiss()
                                                        dialog2.dismiss()

                                                    }
                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                        mdialog.show()
                                                        val idList:ArrayList<Int> = ArrayList()
                                                        idList.add(orderId.toString().toInt())

                                                        val cancelOrder = CancelOrder(idList)

                                                        val cancelOrderCall = orderServices.cancelOrder(headerMap,cancelOrder)
                                                        cancelOrderCall.enqueue(object :Callback<JsonObject>{
                                                            override fun onResponse(
                                                                call: Call<JsonObject>,
                                                                response: Response<JsonObject>
                                                            ) {
                                                                if(response.isSuccessful){
                                                                    val retrofitBuilder2 = Retrofit.Builder()
                                                                        .addConverterFactory(GsonConverterFactory.create())
                                                                        .baseUrl(BASE_URL_RZP)
                                                                        .build()

                                                                    val orderServices2 = retrofitBuilder2.create(OrderServices::class.java)
                                                                    val refundCall = orderServices2.refundPayment(transactionId,(amount.toString().toInt() - shippingCharges.toString().toInt())*100,"Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo")
                                                                    refundCall.enqueue(object :Callback<JsonObject>{
                                                                        override fun onResponse(
                                                                            call: Call<JsonObject>,
                                                                            response2: Response<JsonObject>
                                                                        ) {
                                                                            if(response2.isSuccessful){

                                                                                val refundId = response2.body()?.get("id").toString()

                                                                                mdialog.dismiss()
                                                                                Log.e(TAG, "onResponse: ${response2.body().toString()}")
                                                                                findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
                                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Canceled Successfully.", 3000)
                                                                                snackBar.setBackgroundTint(resources.getColor(R.color.black))
                                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                                snackBar.show()
                                                                                vibratePhone()

                                                                                FirestoreClass().mFirestore.collection("Orders")
                                                                                    .document(orderID)
                                                                                    .update("transactionId",refundId)

                                                                                FirestoreClass().mFirestore.collection("Orders")
                                                                                    .document(orderId)
                                                                                    .update("orderId",
                                                                                        "C$orderID"
                                                                                    )

                                                                            }else{
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${response2.raw()}"
                                                                                )
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${
                                                                                        response2.errorBody()
                                                                                            .toString()
                                                                                    }"
                                                                                )
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${
                                                                                        response2.headers()
                                                                                            .toString()
                                                                                    }"
                                                                                )
                                                                                mdialog.dismiss()

                                                                                mdialog.dismiss()
                                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                                snackBar.show()
                                                                                vibratePhone()
                                                                            }
                                                                        }

                                                                        override fun onFailure(
                                                                            call: Call<JsonObject>,
                                                                            t: Throwable
                                                                        ) {
                                                                            mdialog.dismiss()
                                                                            Log.e(
                                                                                TAG,
                                                                                "onFailure: ${t.localizedMessage}"
                                                                            )
                                                                            val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                            snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                            snackBar.setTextColor(resources.getColor(R.color.white))
                                                                            snackBar.show()
                                                                            vibratePhone()
                                                                        }

                                                                    })

                                                                }else{
                                                                    mdialog.dismiss()
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${response.raw()}"
                                                                    )
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${
                                                                            response.errorBody().toString()
                                                                        }"
                                                                    )
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${
                                                                            response.message().toString()
                                                                        }"
                                                                    )
                                                                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Went Wrong.", Snackbar.LENGTH_LONG)
                                                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                                                    snackBar.show()
                                                                    vibratePhone()
                                                                }

                                                            }

                                                            override fun onFailure(
                                                                call: Call<JsonObject>,
                                                                t: Throwable
                                                            ) {
                                                                mdialog.dismiss()
                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }

                                                        })


                                                    }
                                                    dialog2.show()
                                                }
                                                dialog.show()

                                            }
                                        }else if(status.toString().replace("\"", "") == "INVOICED"){
                                            binding.orderStatusText.text = "Packing"
                                            binding.trackBtn.setOnClickListener {
                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Please wait the order will be tracked soon.", 3000)
                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                snackBar.show()
                                                vibratePhone()
                                            }
                                            binding.cancelBtn.setOnClickListener {
                                                val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
                                                dialog.setTitle("Cancel Order")
                                                dialog.setMessage("Do you really want to cancel the order?")
                                                dialog.background = context?.resources!!.getDrawable(R.drawable.black_btn_bg)
                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                    dialog.dismiss()
                                                }
                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                    val dialog2 = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
                                                    dialog2.setTitle("Remember")
                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                    dialog2.background = resources.getDrawable(R.drawable.black_btn_bg)
                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                        dialog.dismiss()
                                                        dialog2.dismiss()

                                                    }
                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                        mdialog.show()
                                                        val idList:ArrayList<Int> = ArrayList()
                                                        idList.add(orderId.toString().toInt())

                                                        val cancelOrder = CancelOrder(idList)

                                                        val cancelOrderCall = orderServices.cancelOrder(headerMap,cancelOrder)
                                                        cancelOrderCall.enqueue(object :Callback<JsonObject>{
                                                            override fun onResponse(
                                                                call: Call<JsonObject>,
                                                                response: Response<JsonObject>
                                                            ) {
                                                                if(response.isSuccessful){
                                                                    val retrofitBuilder2 = Retrofit.Builder()
                                                                        .addConverterFactory(GsonConverterFactory.create())
                                                                        .baseUrl(BASE_URL_RZP)
                                                                        .build()

                                                                    val orderServices2 = retrofitBuilder2.create(OrderServices::class.java)
                                                                    val refundCall = orderServices2.refundPayment(transactionId,(amount.toString().toInt() - shippingCharges.toString().toInt())*100,"Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo")
                                                                    refundCall.enqueue(object :Callback<JsonObject>{
                                                                        override fun onResponse(
                                                                            call: Call<JsonObject>,
                                                                            response2: Response<JsonObject>
                                                                        ) {
                                                                            if(response2.isSuccessful){

                                                                                val refundId = response2.body()?.get("id").toString()

                                                                                mdialog.dismiss()
                                                                                Log.e(TAG, "onResponse: ${response2.body().toString()}")
                                                                                findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
                                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Canceled Successfully.", 3000)
                                                                                snackBar.setBackgroundTint(resources.getColor(R.color.black))
                                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                                snackBar.show()
                                                                                vibratePhone()

                                                                                FirestoreClass().mFirestore.collection("Orders")
                                                                                    .document(orderID)
                                                                                    .update("transactionId",refundId)

                                                                                FirestoreClass().mFirestore.collection("Orders")
                                                                                    .document(orderId)
                                                                                    .update("orderId",
                                                                                        "C$orderID"
                                                                                    )

                                                                            }else{
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${response2.raw()}"
                                                                                )
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${
                                                                                        response2.errorBody()
                                                                                            .toString()
                                                                                    }"
                                                                                )
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${
                                                                                        response2.headers()
                                                                                            .toString()
                                                                                    }"
                                                                                )
                                                                                mdialog.dismiss()

                                                                                mdialog.dismiss()
                                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                                snackBar.show()
                                                                                vibratePhone()
                                                                            }
                                                                        }

                                                                        override fun onFailure(
                                                                            call: Call<JsonObject>,
                                                                            t: Throwable
                                                                        ) {
                                                                            mdialog.dismiss()
                                                                            Log.e(
                                                                                TAG,
                                                                                "onFailure: ${t.localizedMessage}"
                                                                            )
                                                                            val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                            snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                            snackBar.setTextColor(resources.getColor(R.color.white))
                                                                            snackBar.show()
                                                                            vibratePhone()
                                                                        }

                                                                    })

                                                                }else{
                                                                    mdialog.dismiss()
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${response.raw()}"
                                                                    )
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${
                                                                            response.errorBody().toString()
                                                                        }"
                                                                    )
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${
                                                                            response.message().toString()
                                                                        }"
                                                                    )
                                                                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Went Wrong.", Snackbar.LENGTH_LONG)
                                                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                                                    snackBar.show()
                                                                    vibratePhone()
                                                                }

                                                            }

                                                            override fun onFailure(
                                                                call: Call<JsonObject>,
                                                                t: Throwable
                                                            ) {
                                                                mdialog.dismiss()
                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }

                                                        })


                                                    }
                                                    dialog2.show()
                                                }
                                                dialog.show()

                                            }
                                        }else{
                                            binding.cancelBtn.text = "Cancel Order"
                                            binding.cancelBtn.setOnClickListener {
                                                val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
                                                dialog.setTitle("Cancel Order")
                                                dialog.setMessage("Do you really want to cancel the order?")
                                                dialog.background = context?.resources!!.getDrawable(R.drawable.black_btn_bg)
                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                    dialog.dismiss()
                                                }
                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                    val dialog2 = MaterialAlertDialogBuilder(requireContext(),R.style.AppCompatAlertDialogStyle)
                                                    dialog2.setTitle("Remember")
                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                    dialog2.background = resources.getDrawable(R.drawable.black_btn_bg)
                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                        dialog.dismiss()
                                                        dialog2.dismiss()

                                                    }
                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                        mdialog.show()
                                                        val idList:ArrayList<Int> = ArrayList()
                                                        idList.add(orderId.toString().toInt())

                                                        val cancelOrder = CancelOrder(idList)

                                                        val cancelOrderCall = orderServices.cancelOrder(headerMap,cancelOrder)
                                                        cancelOrderCall.enqueue(object :Callback<JsonObject>{
                                                            override fun onResponse(
                                                                call: Call<JsonObject>,
                                                                response: Response<JsonObject>
                                                            ) {
                                                                if(response.isSuccessful){
                                                                    val retrofitBuilder2 = Retrofit.Builder()
                                                                        .addConverterFactory(GsonConverterFactory.create())
                                                                        .baseUrl(BASE_URL_RZP)
                                                                        .build()

                                                                    val orderServices2 = retrofitBuilder2.create(OrderServices::class.java)
                                                                    val refundCall = orderServices2.refundPayment(transactionId,(amount.toString().toInt() - shippingCharges.toString().toInt())*100,"Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo")
                                                                    refundCall.enqueue(object :Callback<JsonObject>{
                                                                        override fun onResponse(
                                                                            call: Call<JsonObject>,
                                                                            response2: Response<JsonObject>
                                                                        ) {
                                                                            if(response2.isSuccessful){

                                                                                val refundId = response2.body()?.get("id").toString()

                                                                                mdialog.dismiss()
                                                                                Log.e(TAG, "onResponse: ${response2.body().toString()}")
                                                                                findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
                                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Canceled Successfully.", 3000)
                                                                                snackBar.setBackgroundTint(resources.getColor(R.color.black))
                                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                                snackBar.show()
                                                                                vibratePhone()

                                                                                FirestoreClass().mFirestore.collection("Orders")
                                                                                    .document(orderID)
                                                                                    .update("transactionId",refundId)

                                                                                FirestoreClass().mFirestore.collection("Orders")
                                                                                    .document(orderId)
                                                                                    .update("orderId",
                                                                                        "C$orderID"
                                                                                    )

                                                                            }else{
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${response2.raw()}"
                                                                                )
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${
                                                                                        response2.errorBody()
                                                                                            .toString()
                                                                                    }"
                                                                                )
                                                                                Log.e(
                                                                                    TAG,
                                                                                    "onResponse: ${
                                                                                        response2.headers()
                                                                                            .toString()
                                                                                    }"
                                                                                )
                                                                                mdialog.dismiss()

                                                                                mdialog.dismiss()
                                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                                snackBar.show()
                                                                                vibratePhone()
                                                                            }
                                                                        }

                                                                        override fun onFailure(
                                                                            call: Call<JsonObject>,
                                                                            t: Throwable
                                                                        ) {
                                                                            mdialog.dismiss()
                                                                            Log.e(
                                                                                TAG,
                                                                                "onFailure: ${t.localizedMessage}"
                                                                            )
                                                                            val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                            snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                            snackBar.setTextColor(resources.getColor(R.color.white))
                                                                            snackBar.show()
                                                                            vibratePhone()
                                                                        }

                                                                    })

                                                                }else{
                                                                    mdialog.dismiss()
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${response.raw()}"
                                                                    )
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${
                                                                            response.errorBody().toString()
                                                                        }"
                                                                    )
                                                                    Log.e(
                                                                        TAG,
                                                                        "onResponse: ${
                                                                            response.message().toString()
                                                                        }"
                                                                    )
                                                                    val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Went Wrong.", Snackbar.LENGTH_LONG)
                                                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                                                    snackBar.show()
                                                                    vibratePhone()
                                                                }

                                                            }

                                                            override fun onFailure(
                                                                call: Call<JsonObject>,
                                                                t: Throwable
                                                            ) {
                                                                mdialog.dismiss()
                                                                val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Order Cancellation Failed.", Snackbar.LENGTH_LONG)
                                                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }

                                                        })


                                                    }
                                                    dialog2.show()
                                                }
                                                dialog.show()

                                            }
                                        }

                                    }else{
                                        mdialog.dismiss()
                                        Log.e(TAG, "onResponse: ${response.code().toString()}")
                                        Log.e(TAG, "onResponse: ${response.message().toString()}")
                                        Log.e(TAG, "onResponse: ${response.raw().toString()}")
                                        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Went Wrong.", Snackbar.LENGTH_LONG)
                                        snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                        snackBar.setTextColor(resources.getColor(R.color.white))
                                        snackBar.show()
                                        vibratePhone()
                                    }

                                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                        mdialog.dismiss()
                                        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Something Went Wrong.", Snackbar.LENGTH_LONG)
                                        snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                        snackBar.setTextColor(resources.getColor(R.color.white))
                                        snackBar.show()
                                        vibratePhone()
                                    }

                                })
                            }

                        }else{
                            val retrofitBuilder2 = Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl(BASE_URL)
                                .build()

                            val orderServices2 = retrofitBuilder2.create(OrderServices::class.java)

                            val trackingCall = orderServices2.getTrackingData(headerMap,awb)
                            trackingCall.enqueue(object :Callback<JsonObject>{
                                override fun onResponse(
                                    call: Call<JsonObject>,
                                    response2: Response<JsonObject>
                                ) {
                                    if(response.isSuccessful) {

                                        Log.e(
                                            TAG,
                                            "onResponse: ${
                                                response2.body()?.getAsJsonObject("tracking_data")
                                                    ?.get("etd").toString().replace("\"", "")
                                                    .replaceAfter(" ", "").trim()
                                            }",
                                        )

                                        if (response2.body()?.getAsJsonObject("tracking_data")
                                                ?.get("etd").toString().replace("\"", "") == ""
                                        ) {
                                            edd = date
                                            if ("R" in orderId) {
                                                binding.cancelBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.trackBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.orderStatusText.text = "Returned"
                                                binding.orderStatusText.setTextColor(
                                                    resources.getColor(
                                                        R.color.gray
                                                    )
                                                )
                                                binding.view18.visibility = View.INVISIBLE
                                                mdialog.dismiss()
                                            } else if ("C" in orderId) {
                                                binding.cancelBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.trackBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.orderStatusText.text = "Canceled"
                                                binding.orderStatusText.setTextColor(
                                                    resources.getColor(
                                                        R.color.red
                                                    )
                                                )
                                                binding.view18.visibility = View.INVISIBLE
                                                mdialog.dismiss()
                                            } else {
                                                val ordercall =
                                                    orderServices.getOrderDetail(headerMap, orderId)
                                                ordercall.enqueue(object : Callback<JsonObject> {
                                                    override fun onResponse(
                                                        call: Call<JsonObject>,
                                                        response: Response<JsonObject>
                                                    ) = if (response.isSuccessful) {

                                                        val data =
                                                            response.body()?.getAsJsonObject("data")
                                                        val status = data?.get("status")
                                                        orderID = data?.get("id").toString()
                                                        Log.e(TAG, "onResponse: $orderID")
                                                        binding.orderStatusText.text =
                                                            status.toString().replace("\"", "")
                                                                .lowercase().capitalize()
                                                        mdialog.dismiss()
                                                        val current = LocalDateTime.now()
                                                        val formatter =
                                                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                        val formattedDate =
                                                            current.format(formatter).toString()

                                                        val todayDate =
                                                            SimpleDateFormat("yyyy-MM-dd").parse(
                                                                formattedDate
                                                            )
                                                        val expiryDate =
                                                            SimpleDateFormat("yyyy-MM-dd").parse(
                                                                getCalculatedDate(
                                                                    edd,
                                                                    "yyyy-MM-dd",
                                                                    6
                                                                )
                                                            )
                                                        Log.e(TAG, "onResponse: $todayDate")
                                                        Log.e(TAG, "onResponse: $expiryDate")

                                                        if (status.toString()
                                                                .replace("\"", "") == "NEW"
                                                        ) {
                                                            binding.cancelBtn.text = "Return Order"
                                                            if (todayDate.before(expiryDate)) {
                                                                binding.cancelBtn.setOnClickListener {

                                                                    val dialog =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog.setTitle("Return Order")
                                                                    dialog.setMessage("Do you really want to Return the order?")
                                                                    dialog.background =
                                                                        context?.resources!!.getDrawable(
                                                                            R.drawable.black_btn_bg
                                                                        )
                                                                    dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                        dialog.dismiss()
                                                                    }
                                                                    dialog.setPositiveButton("Sure") { dialog, which ->
                                                                        val dialog2 =
                                                                            MaterialAlertDialogBuilder(
                                                                                requireContext(),
                                                                                R.style.AppCompatAlertDialogStyle
                                                                            )
                                                                        dialog2.setTitle("Remember")
                                                                        dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                        dialog2.background =
                                                                            resources.getDrawable(R.drawable.black_btn_bg)
                                                                        dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                            dialog.dismiss()
                                                                            dialog2.dismiss()

                                                                        }
                                                                        dialog2.setPositiveButton("Agree") { dialog2, which ->

                                                                            val bundle = Bundle()
                                                                            bundle.putString(
                                                                                "orderID",
                                                                                orderID
                                                                            )
                                                                            bundle.putString(
                                                                                "transactionID",
                                                                                transactionId
                                                                            )
                                                                            bundle.putString(
                                                                                "name",
                                                                                name
                                                                            )
                                                                            bundle.putString(
                                                                                "address",
                                                                                address
                                                                            )
                                                                            bundle.putString(
                                                                                "city",
                                                                                city
                                                                            )
                                                                            bundle.putString(
                                                                                "state",
                                                                                state
                                                                            )
                                                                            bundle.putString(
                                                                                "country",
                                                                                country
                                                                            )
                                                                            bundle.putString(
                                                                                "pincode",
                                                                                pincode
                                                                            )
                                                                            bundle.putString(
                                                                                "email",
                                                                                email
                                                                            )
                                                                            bundle.putString(
                                                                                "phone",
                                                                                contact
                                                                            )
                                                                            bundle.putString(
                                                                                "amount",
                                                                                amount
                                                                            )
                                                                            bundle.putString(
                                                                                "shippingCharges",
                                                                                shippingCharges
                                                                            )
                                                                            bundle.putParcelableArrayList(
                                                                                "products",
                                                                                products as java.util.ArrayList<CartItem>
                                                                            )
                                                                            findNavController().navigate(
                                                                                R.id.action_orderDetailFragment_to_orderReturnFragment,
                                                                                bundle
                                                                            )
                                                                        }
                                                                        dialog2.show()
                                                                    }
                                                                    dialog.show()

                                                                }
                                                            } else {
                                                                binding.cancelBtn.setOnClickListener {
                                                                    val snackBar = Snackbar.make(
                                                                        requireActivity().findViewById(
                                                                            android.R.id.content
                                                                        ),
                                                                        "The Order Can be Only Return Within 5 Days after Order.",
                                                                        3000
                                                                    )
                                                                    snackBar.setBackgroundTint(
                                                                        resources.getColor(R.color.red)
                                                                    )
                                                                    snackBar.setTextColor(
                                                                        resources.getColor(
                                                                            R.color.white
                                                                        )
                                                                    )
                                                                    snackBar.show()
                                                                    vibratePhone()
                                                                }
                                                            }
                                                        } else if (status.toString()
                                                                .replace("\"", "") == "CANCELED"
                                                        ) {
                                                            binding.cancelBtn.visibility =
                                                                View.INVISIBLE
                                                            binding.cancelBtn.isClickable = false
                                                        } else if (status.toString().replace(
                                                                "\"",
                                                                ""
                                                            ) == "CANCELLATION REQUESTED"
                                                        ) {
                                                            binding.cancelBtn.visibility =
                                                                View.INVISIBLE
                                                            binding.cancelBtn.isClickable = false
                                                        } else if (status.toString().replace(
                                                                "\"",
                                                                ""
                                                            ) == "READY TO SHIP"
                                                        ) {
                                                            binding.orderStatusText.text = "Packing"
                                                            binding.trackBtn.setOnClickListener {
                                                                val snackBar = Snackbar.make(
                                                                    requireActivity().findViewById(
                                                                        android.R.id.content
                                                                    ),
                                                                    "Please wait the order will be tracked soon.",
                                                                    3000
                                                                )
                                                                snackBar.setBackgroundTint(
                                                                    resources.getColor(
                                                                        R.color.red
                                                                    )
                                                                )
                                                                snackBar.setTextColor(
                                                                    resources.getColor(
                                                                        R.color.white
                                                                    )
                                                                )
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }

                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(R.drawable.black_btn_bg)
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: ArrayList<Int> =
                                                                            ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        } else if (status.toString()
                                                                .replace("\"", "") == "INVOICED"
                                                        ) {
                                                            binding.orderStatusText.text = "Packing"
                                                            binding.trackBtn.setOnClickListener {
                                                                val snackBar = Snackbar.make(
                                                                    requireActivity().findViewById(
                                                                        android.R.id.content
                                                                    ),
                                                                    "Please wait the order will be tracked soon.",
                                                                    3000
                                                                )
                                                                snackBar.setBackgroundTint(
                                                                    resources.getColor(
                                                                        R.color.red
                                                                    )
                                                                )
                                                                snackBar.setTextColor(
                                                                    resources.getColor(
                                                                        R.color.white
                                                                    )
                                                                )
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }
                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(R.drawable.black_btn_bg)
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: ArrayList<Int> =
                                                                            ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        } else {
                                                            binding.cancelBtn.text = "Cancel Order"
                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(R.drawable.black_btn_bg)
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: ArrayList<Int> =
                                                                            ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        }

                                                    } else {
                                                        mdialog.dismiss()
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.code().toString()
                                                            }"
                                                        )
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.message().toString()
                                                            }"
                                                        )
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.raw().toString()
                                                            }"
                                                        )
                                                        val snackBar = Snackbar.make(
                                                            requireActivity().findViewById(android.R.id.content),
                                                            "Something Went Wrong.",
                                                            Snackbar.LENGTH_LONG
                                                        )
                                                        snackBar.setBackgroundTint(
                                                            resources.getColor(
                                                                R.color.red
                                                            )
                                                        )
                                                        snackBar.setTextColor(resources.getColor(R.color.white))
                                                        snackBar.show()
                                                        vibratePhone()
                                                    }

                                                    override fun onFailure(
                                                        call: Call<JsonObject>,
                                                        t: Throwable
                                                    ) {
                                                        mdialog.dismiss()
                                                        val snackBar = Snackbar.make(
                                                            requireActivity().findViewById(android.R.id.content),
                                                            "Something Went Wrong.",
                                                            Snackbar.LENGTH_LONG
                                                        )
                                                        snackBar.setBackgroundTint(
                                                            resources.getColor(
                                                                R.color.red
                                                            )
                                                        )
                                                        snackBar.setTextColor(resources.getColor(R.color.white))
                                                        snackBar.show()
                                                        vibratePhone()
                                                    }

                                                })
                                            }
                                        } else if (response2.body()
                                                ?.getAsJsonObject("tracking_data")?.get("etd")
                                                .toString().replace("\"", "") == "null"
                                        ) {
                                            edd = date
                                            if ("R" in orderId) {
                                                binding.cancelBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.trackBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.orderStatusText.text = "Returned"
                                                binding.orderStatusText.setTextColor(
                                                    resources.getColor(
                                                        R.color.gray
                                                    )
                                                )
                                                binding.view18.visibility = View.INVISIBLE
                                                mdialog.dismiss()
                                            } else if ("C" in orderId) {
                                                binding.cancelBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.trackBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.orderStatusText.text = "Canceled"
                                                binding.orderStatusText.setTextColor(
                                                    resources.getColor(
                                                        R.color.red
                                                    )
                                                )
                                                binding.view18.visibility = View.INVISIBLE
                                                mdialog.dismiss()
                                            } else {
                                                val ordercall =
                                                    orderServices.getOrderDetail(headerMap, orderId)
                                                ordercall.enqueue(object : Callback<JsonObject> {
                                                    override fun onResponse(
                                                        call: Call<JsonObject>,
                                                        response: Response<JsonObject>
                                                    ) = if (response.isSuccessful) {

                                                        val data =
                                                            response.body()?.getAsJsonObject("data")
                                                        val status = data?.get("status")
                                                        orderID = data?.get("id").toString()
                                                        Log.e(TAG, "onResponse: $orderID")
                                                        binding.orderStatusText.text =
                                                            status.toString().replace("\"", "")
                                                                .lowercase().capitalize()
                                                        mdialog.dismiss()
                                                        val current = LocalDateTime.now()
                                                        val formatter =
                                                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                        val formattedDate =
                                                            current.format(formatter).toString()

                                                        val todayDate =
                                                            SimpleDateFormat("yyyy-MM-dd").parse(
                                                                formattedDate
                                                            )
                                                        val expiryDate =
                                                            SimpleDateFormat("yyyy-MM-dd").parse(
                                                                getCalculatedDate(
                                                                    edd,
                                                                    "yyyy-MM-dd",
                                                                    6
                                                                )
                                                            )
                                                        Log.e(TAG, "onResponse: $todayDate")
                                                        Log.e(TAG, "onResponse: $expiryDate")

                                                        if (status.toString()
                                                                .replace("\"", "") == "NEW"
                                                        ) {
                                                            binding.cancelBtn.text = "Return Order"
                                                            if (todayDate.before(expiryDate)) {
                                                                binding.cancelBtn.setOnClickListener {

                                                                    val dialog =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog.setTitle("Return Order")
                                                                    dialog.setMessage("Do you really want to Return the order?")
                                                                    dialog.background =
                                                                        context?.resources!!.getDrawable(
                                                                            R.drawable.black_btn_bg
                                                                        )
                                                                    dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                        dialog.dismiss()
                                                                    }
                                                                    dialog.setPositiveButton("Sure") { dialog, which ->
                                                                        val dialog2 =
                                                                            MaterialAlertDialogBuilder(
                                                                                requireContext(),
                                                                                R.style.AppCompatAlertDialogStyle
                                                                            )
                                                                        dialog2.setTitle("Remember")
                                                                        dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                        dialog2.background =
                                                                            resources.getDrawable(R.drawable.black_btn_bg)
                                                                        dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                            dialog.dismiss()
                                                                            dialog2.dismiss()

                                                                        }
                                                                        dialog2.setPositiveButton("Agree") { dialog2, which ->

                                                                            val bundle = Bundle()
                                                                            bundle.putString(
                                                                                "orderID",
                                                                                orderID
                                                                            )
                                                                            bundle.putString(
                                                                                "transactionID",
                                                                                transactionId
                                                                            )
                                                                            bundle.putString(
                                                                                "name",
                                                                                name
                                                                            )
                                                                            bundle.putString(
                                                                                "address",
                                                                                address
                                                                            )
                                                                            bundle.putString(
                                                                                "city",
                                                                                city
                                                                            )
                                                                            bundle.putString(
                                                                                "state",
                                                                                state
                                                                            )
                                                                            bundle.putString(
                                                                                "country",
                                                                                country
                                                                            )
                                                                            bundle.putString(
                                                                                "pincode",
                                                                                pincode
                                                                            )
                                                                            bundle.putString(
                                                                                "email",
                                                                                email
                                                                            )
                                                                            bundle.putString(
                                                                                "phone",
                                                                                contact
                                                                            )
                                                                            bundle.putString(
                                                                                "amount",
                                                                                amount
                                                                            )
                                                                            bundle.putString(
                                                                                "shippingCharges",
                                                                                shippingCharges
                                                                            )
                                                                            bundle.putParcelableArrayList(
                                                                                "products",
                                                                                products as java.util.ArrayList<CartItem>
                                                                            )
                                                                            findNavController().navigate(
                                                                                R.id.action_orderDetailFragment_to_orderReturnFragment,
                                                                                bundle
                                                                            )
                                                                        }
                                                                        dialog2.show()
                                                                    }
                                                                    dialog.show()

                                                                }
                                                            } else {
                                                                binding.cancelBtn.setOnClickListener {
                                                                    val snackBar = Snackbar.make(
                                                                        requireActivity().findViewById(
                                                                            android.R.id.content
                                                                        ),
                                                                        "The Order Can be Only Return Within 5 Days after Order.",
                                                                        3000
                                                                    )
                                                                    snackBar.setBackgroundTint(
                                                                        resources.getColor(R.color.red)
                                                                    )
                                                                    snackBar.setTextColor(
                                                                        resources.getColor(
                                                                            R.color.white
                                                                        )
                                                                    )
                                                                    snackBar.show()
                                                                    vibratePhone()
                                                                }
                                                            }
                                                        } else if (status.toString()
                                                                .replace("\"", "") == "CANCELED"
                                                        ) {
                                                            binding.cancelBtn.visibility =
                                                                View.INVISIBLE
                                                            binding.cancelBtn.isClickable = false
                                                        } else if (status.toString().replace(
                                                                "\"",
                                                                ""
                                                            ) == "CANCELLATION REQUESTED"
                                                        ) {
                                                            binding.cancelBtn.visibility =
                                                                View.INVISIBLE
                                                            binding.cancelBtn.isClickable = false
                                                        } else if (status.toString().replace(
                                                                "\"",
                                                                ""
                                                            ) == "READY TO SHIP"
                                                        ) {
                                                            binding.orderStatusText.text = "Packing"
                                                            binding.trackBtn.setOnClickListener {
                                                                val snackBar = Snackbar.make(
                                                                    requireActivity().findViewById(
                                                                        android.R.id.content
                                                                    ),
                                                                    "Please wait the order will be tracked soon.",
                                                                    3000
                                                                )
                                                                snackBar.setBackgroundTint(
                                                                    resources.getColor(
                                                                        R.color.red
                                                                    )
                                                                )
                                                                snackBar.setTextColor(
                                                                    resources.getColor(
                                                                        R.color.white
                                                                    )
                                                                )
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }

                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(R.drawable.black_btn_bg)
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: ArrayList<Int> =
                                                                            ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        } else if (status.toString()
                                                                .replace("\"", "") == "INVOICED"
                                                        ) {
                                                            binding.orderStatusText.text = "Packing"
                                                            binding.trackBtn.setOnClickListener {
                                                                val snackBar = Snackbar.make(
                                                                    requireActivity().findViewById(
                                                                        android.R.id.content
                                                                    ),
                                                                    "Please wait the order will be tracked soon.",
                                                                    3000
                                                                )
                                                                snackBar.setBackgroundTint(
                                                                    resources.getColor(
                                                                        R.color.red
                                                                    )
                                                                )
                                                                snackBar.setTextColor(
                                                                    resources.getColor(
                                                                        R.color.white
                                                                    )
                                                                )
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }
                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(R.drawable.black_btn_bg)
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: ArrayList<Int> =
                                                                            ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        } else {
                                                            binding.cancelBtn.text = "Cancel Order"
                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(R.drawable.black_btn_bg)
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: ArrayList<Int> =
                                                                            ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        }

                                                    } else {
                                                        mdialog.dismiss()
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.code().toString()
                                                            }"
                                                        )
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.message().toString()
                                                            }"
                                                        )
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.raw().toString()
                                                            }"
                                                        )
                                                        val snackBar = Snackbar.make(
                                                            requireActivity().findViewById(android.R.id.content),
                                                            "Something Went Wrong.",
                                                            Snackbar.LENGTH_LONG
                                                        )
                                                        snackBar.setBackgroundTint(
                                                            resources.getColor(
                                                                R.color.red
                                                            )
                                                        )
                                                        snackBar.setTextColor(resources.getColor(R.color.white))
                                                        snackBar.show()
                                                        vibratePhone()
                                                    }

                                                    override fun onFailure(
                                                        call: Call<JsonObject>,
                                                        t: Throwable
                                                    ) {
                                                        mdialog.dismiss()
                                                        val snackBar = Snackbar.make(
                                                            requireActivity().findViewById(android.R.id.content),
                                                            "Something Went Wrong.",
                                                            Snackbar.LENGTH_LONG
                                                        )
                                                        snackBar.setBackgroundTint(
                                                            resources.getColor(
                                                                R.color.red
                                                            )
                                                        )
                                                        snackBar.setTextColor(resources.getColor(R.color.white))
                                                        snackBar.show()
                                                        vibratePhone()
                                                    }

                                                })
                                            }
                                        } else {

                                            edd = response2.body()?.getAsJsonObject("tracking_data")
                                                ?.get("etd").toString().replace("\"", "")
                                                .replaceAfter(" ", "").trim()
                                            if ("R" in orderId) {
                                                binding.cancelBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.trackBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.orderStatusText.text = "Returned"
                                                binding.orderStatusText.setTextColor(
                                                    resources.getColor(
                                                        R.color.gray
                                                    )
                                                )
                                                binding.view18.visibility = View.INVISIBLE
                                                mdialog.dismiss()
                                            } else if ("C" in orderId) {
                                                binding.cancelBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.trackBtn.visibility = View.INVISIBLE
                                                binding.cancelBtn.isClickable = false
                                                binding.orderStatusText.text = "Canceled"
                                                binding.orderStatusText.setTextColor(
                                                    resources.getColor(
                                                        R.color.red
                                                    )
                                                )
                                                binding.view18.visibility = View.INVISIBLE
                                                mdialog.dismiss()
                                            } else {
                                                val ordercall =
                                                    orderServices.getOrderDetail(headerMap, orderId)
                                                ordercall.enqueue(object : Callback<JsonObject> {
                                                    override fun onResponse(
                                                        call: Call<JsonObject>,
                                                        response: Response<JsonObject>
                                                    ) = if (response.isSuccessful) {

                                                        val data =
                                                            response.body()?.getAsJsonObject("data")
                                                        val status = data?.get("status")
                                                        orderID = data?.get("id").toString()
                                                        Log.e(TAG, "onResponse: $orderID")
                                                        binding.orderStatusText.text =
                                                            status.toString().replace("\"", "")
                                                                .lowercase().capitalize()
                                                        mdialog.dismiss()
                                                        val current = LocalDateTime.now()
                                                        val formatter =
                                                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                        val formattedDate =
                                                            current.format(formatter).toString()

                                                        val todayDate =
                                                            SimpleDateFormat("yyyy-MM-dd").parse(
                                                                formattedDate
                                                            )
                                                        val expiryDate =
                                                            SimpleDateFormat("yyyy-MM-dd").parse(
                                                                getCalculatedDate(
                                                                    edd,
                                                                    "yyyy-MM-dd",
                                                                    6
                                                                )
                                                            )
                                                        Log.e(TAG, "onResponse: $todayDate")
                                                        Log.e(TAG, "onResponse: $expiryDate")

                                                        if (status.toString()
                                                                .replace("\"", "") == "NEW"
                                                        ) {
                                                            binding.cancelBtn.text = "Return Order"
                                                            if (todayDate.before(expiryDate)) {
                                                                binding.cancelBtn.setOnClickListener {

                                                                    val dialog =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog.setTitle("Return Order")
                                                                    dialog.setMessage("Do you really want to Return the order?")
                                                                    dialog.background =
                                                                        context?.resources!!.getDrawable(
                                                                            R.drawable.black_btn_bg
                                                                        )
                                                                    dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                        dialog.dismiss()
                                                                    }
                                                                    dialog.setPositiveButton("Sure") { dialog, which ->
                                                                        val dialog2 =
                                                                            MaterialAlertDialogBuilder(
                                                                                requireContext(),
                                                                                R.style.AppCompatAlertDialogStyle
                                                                            )
                                                                        dialog2.setTitle("Remember")
                                                                        dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                        dialog2.background =
                                                                            resources.getDrawable(
                                                                                R.drawable.black_btn_bg
                                                                            )
                                                                        dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                            dialog.dismiss()
                                                                            dialog2.dismiss()

                                                                        }
                                                                        dialog2.setPositiveButton("Agree") { dialog2, which ->

                                                                            val bundle = Bundle()
                                                                            bundle.putString(
                                                                                "orderID",
                                                                                orderID
                                                                            )
                                                                            bundle.putString(
                                                                                "transactionID",
                                                                                transactionId
                                                                            )
                                                                            bundle.putString(
                                                                                "name",
                                                                                name
                                                                            )
                                                                            bundle.putString(
                                                                                "address",
                                                                                address
                                                                            )
                                                                            bundle.putString(
                                                                                "city",
                                                                                city
                                                                            )
                                                                            bundle.putString(
                                                                                "state",
                                                                                state
                                                                            )
                                                                            bundle.putString(
                                                                                "country",
                                                                                country
                                                                            )
                                                                            bundle.putString(
                                                                                "pincode",
                                                                                pincode
                                                                            )
                                                                            bundle.putString(
                                                                                "email",
                                                                                email
                                                                            )
                                                                            bundle.putString(
                                                                                "phone",
                                                                                contact
                                                                            )
                                                                            bundle.putString(
                                                                                "amount",
                                                                                amount
                                                                            )
                                                                            bundle.putString(
                                                                                "shippingCharges",
                                                                                shippingCharges
                                                                            )
                                                                            bundle.putParcelableArrayList(
                                                                                "products",
                                                                                products as java.util.ArrayList<CartItem>
                                                                            )
                                                                            findNavController().navigate(
                                                                                R.id.action_orderDetailFragment_to_orderReturnFragment,
                                                                                bundle
                                                                            )
                                                                        }
                                                                        dialog2.show()
                                                                    }
                                                                    dialog.show()

                                                                }
                                                            } else {
                                                                binding.cancelBtn.setOnClickListener {
                                                                    val snackBar = Snackbar.make(
                                                                        requireActivity().findViewById(
                                                                            android.R.id.content
                                                                        ),
                                                                        "The Order Can be Only Return Within 5 Days after Order.",
                                                                        3000
                                                                    )
                                                                    snackBar.setBackgroundTint(
                                                                        resources.getColor(
                                                                            R.color.red
                                                                        )
                                                                    )
                                                                    snackBar.setTextColor(
                                                                        resources.getColor(
                                                                            android.R.color.white
                                                                        )
                                                                    )
                                                                    snackBar.show()
                                                                    vibratePhone()
                                                                }
                                                            }
                                                        } else if (status.toString()
                                                                .replace("\"", "") == "CANCELED"
                                                        ) {
                                                            binding.cancelBtn.visibility =
                                                                View.INVISIBLE
                                                            binding.cancelBtn.isClickable = false
                                                        } else if (status.toString().replace(
                                                                "\"",
                                                                ""
                                                            ) == "CANCELLATION REQUESTED"
                                                        ) {
                                                            binding.cancelBtn.visibility =
                                                                View.INVISIBLE
                                                            binding.cancelBtn.isClickable = false
                                                        } else if (status.toString().replace(
                                                                "\"",
                                                                ""
                                                            ) == "READY TO SHIP"
                                                        ) {
                                                            binding.orderStatusText.text = "Packing"
                                                            binding.trackBtn.setOnClickListener {
                                                                val snackBar = Snackbar.make(
                                                                    requireActivity().findViewById(
                                                                        android.R.id.content
                                                                    ),
                                                                    "Please wait the order will be tracked soon.",
                                                                    3000
                                                                )
                                                                snackBar.setBackgroundTint(
                                                                    resources.getColor(
                                                                        R.color.red
                                                                    )
                                                                )
                                                                snackBar.setTextColor(
                                                                    resources.getColor(
                                                                        android.R.color.white
                                                                    )
                                                                )
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }

                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(
                                                                            R.drawable.black_btn_bg
                                                                        )
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: java.util.ArrayList<Int> =
                                                                            java.util.ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            android.R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                android.R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            android.R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        } else if (status.toString()
                                                                .replace("\"", "") == "INVOICED"
                                                        ) {
                                                            binding.orderStatusText.text = "Packing"
                                                            binding.trackBtn.setOnClickListener {
                                                                val snackBar = Snackbar.make(
                                                                    requireActivity().findViewById(
                                                                        android.R.id.content
                                                                    ),
                                                                    "Please wait the order will be tracked soon.",
                                                                    3000
                                                                )
                                                                snackBar.setBackgroundTint(
                                                                    resources.getColor(
                                                                        R.color.red
                                                                    )
                                                                )
                                                                snackBar.setTextColor(
                                                                    resources.getColor(
                                                                        android.R.color.white
                                                                    )
                                                                )
                                                                snackBar.show()
                                                                vibratePhone()
                                                            }
                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                       R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(
                                                                           R.drawable.black_btn_bg
                                                                        )
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: java.util.ArrayList<Int> =
                                                                            java.util.ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            android.R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                android.R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            android.R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        } else {
                                                            binding.cancelBtn.text = "Cancel Order"
                                                            binding.cancelBtn.setOnClickListener {
                                                                val dialog =
                                                                    MaterialAlertDialogBuilder(
                                                                        requireContext(),
                                                                        R.style.AppCompatAlertDialogStyle
                                                                    )
                                                                dialog.setTitle("Cancel Order")
                                                                dialog.setMessage("Do you really want to cancel the order?")
                                                                dialog.background =
                                                                    context?.resources!!.getDrawable(
                                                                        R.drawable.black_btn_bg
                                                                    )
                                                                dialog.setNegativeButton("Cancel") { dialog, which ->
                                                                    dialog.dismiss()
                                                                }
                                                                dialog.setPositiveButton("Sure") { dialog, which ->
                                                                    val dialog2 =
                                                                        MaterialAlertDialogBuilder(
                                                                            requireContext(),
                                                                            R.style.AppCompatAlertDialogStyle
                                                                        )
                                                                    dialog2.setTitle("Remember")
                                                                    dialog2.setMessage("As our policy your money will be refund in 5-10 Business Days. Remember that your shipping charges will 'not' be refund.")
                                                                    dialog2.background =
                                                                        resources.getDrawable(
                                                                            R.drawable.black_btn_bg
                                                                        )
                                                                    dialog2.setNegativeButton("Cancel") { dialog2, which ->
                                                                        dialog.dismiss()
                                                                        dialog2.dismiss()

                                                                    }
                                                                    dialog2.setPositiveButton("Agree") { dialog2, which ->
                                                                        mdialog.show()
                                                                        val idList: java.util.ArrayList<Int> =
                                                                            java.util.ArrayList()
                                                                        idList.add(
                                                                            orderId.toString()
                                                                                .toInt()
                                                                        )

                                                                        val cancelOrder =
                                                                            CancelOrder(idList)

                                                                        val cancelOrderCall =
                                                                            orderServices.cancelOrder(
                                                                                headerMap,
                                                                                cancelOrder
                                                                            )
                                                                        cancelOrderCall.enqueue(
                                                                            object :
                                                                                Callback<JsonObject> {
                                                                                override fun onResponse(
                                                                                    call: Call<JsonObject>,
                                                                                    response: Response<JsonObject>
                                                                                ) {
                                                                                    if (response.isSuccessful) {
                                                                                        val retrofitBuilder2 =
                                                                                            Retrofit.Builder()
                                                                                                .addConverterFactory(
                                                                                                    GsonConverterFactory.create()
                                                                                                )
                                                                                                .baseUrl(
                                                                                                    BASE_URL_RZP
                                                                                                )
                                                                                                .build()

                                                                                        val orderServices2 =
                                                                                            retrofitBuilder2.create(
                                                                                                OrderServices::class.java
                                                                                            )
                                                                                        val refundCall =
                                                                                            orderServices2.refundPayment(
                                                                                                transactionId,
                                                                                                (amount.toString()
                                                                                                    .toInt() - shippingCharges.toString()
                                                                                                    .toInt()) * 100,
                                                                                                "Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                                                                            )
                                                                                        refundCall.enqueue(
                                                                                            object :
                                                                                                Callback<JsonObject> {
                                                                                                override fun onResponse(
                                                                                                    call: Call<JsonObject>,
                                                                                                    response2: Response<JsonObject>
                                                                                                ) {
                                                                                                    if (response2.isSuccessful) {

                                                                                                        val refundId =
                                                                                                            response2.body()
                                                                                                                ?.get(
                                                                                                                    "id"
                                                                                                                )
                                                                                                                .toString()

                                                                                                        mdialog.dismiss()
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.body()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        findNavController().navigate(
                                                                                                            R.id.action_orderDetailFragment_to_orderFragment
                                                                                                        )
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Canceled Successfully.",
                                                                                                                3000
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.black
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderID
                                                                                                            )
                                                                                                            .update(
                                                                                                                "transactionId",
                                                                                                                refundId
                                                                                                            )

                                                                                                        FirestoreClass().mFirestore.collection(
                                                                                                            "Orders"
                                                                                                        )
                                                                                                            .document(
                                                                                                                orderId
                                                                                                            )
                                                                                                            .update(
                                                                                                                "orderId",
                                                                                                                "C$orderID"
                                                                                                            )

                                                                                                    } else {
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${response2.raw()}"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.errorBody()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        Log.e(
                                                                                                            TAG,
                                                                                                            "onResponse: ${
                                                                                                                response2.headers()
                                                                                                                    .toString()
                                                                                                            }"
                                                                                                        )
                                                                                                        mdialog.dismiss()

                                                                                                        mdialog.dismiss()
                                                                                                        val snackBar =
                                                                                                            Snackbar.make(
                                                                                                                requireActivity().findViewById(
                                                                                                                    android.R.id.content
                                                                                                                ),
                                                                                                                "Order Cancellation Failed.",
                                                                                                                Snackbar.LENGTH_LONG
                                                                                                            )
                                                                                                        snackBar.setBackgroundTint(
                                                                                                            resources.getColor(
                                                                                                                R.color.red
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.setTextColor(
                                                                                                            resources.getColor(
                                                                                                                android.R.color.white
                                                                                                            )
                                                                                                        )
                                                                                                        snackBar.show()
                                                                                                        vibratePhone()
                                                                                                    }
                                                                                                }

                                                                                                override fun onFailure(
                                                                                                    call: Call<JsonObject>,
                                                                                                    t: Throwable
                                                                                                ) {
                                                                                                    mdialog.dismiss()
                                                                                                    Log.e(
                                                                                                        TAG,
                                                                                                        "onFailure: ${t.localizedMessage}"
                                                                                                    )
                                                                                                    val snackBar =
                                                                                                        Snackbar.make(
                                                                                                            requireActivity().findViewById(
                                                                                                                android.R.id.content
                                                                                                            ),
                                                                                                            "Order Cancellation Failed.",
                                                                                                            Snackbar.LENGTH_LONG
                                                                                                        )
                                                                                                    snackBar.setBackgroundTint(
                                                                                                        resources.getColor(
                                                                                                            R.color.red
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.setTextColor(
                                                                                                        resources.getColor(
                                                                                                            android.R.color.white
                                                                                                        )
                                                                                                    )
                                                                                                    snackBar.show()
                                                                                                    vibratePhone()
                                                                                                }

                                                                                            })

                                                                                    } else {
                                                                                        mdialog.dismiss()
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${response.raw()}"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.errorBody()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        Log.e(
                                                                                            TAG,
                                                                                            "onResponse: ${
                                                                                                response.message()
                                                                                                    .toString()
                                                                                            }"
                                                                                        )
                                                                                        val snackBar =
                                                                                            Snackbar.make(
                                                                                                requireActivity().findViewById(
                                                                                                    android.R.id.content
                                                                                                ),
                                                                                                "Something Went Wrong.",
                                                                                                Snackbar.LENGTH_LONG
                                                                                            )
                                                                                        snackBar.setBackgroundTint(
                                                                                            resources.getColor(
                                                                                                R.color.red
                                                                                            )
                                                                                        )
                                                                                        snackBar.setTextColor(
                                                                                            resources.getColor(
                                                                                                android.R.color.white
                                                                                            )
                                                                                        )
                                                                                        snackBar.show()
                                                                                        vibratePhone()
                                                                                    }

                                                                                }

                                                                                override fun onFailure(
                                                                                    call: Call<JsonObject>,
                                                                                    t: Throwable
                                                                                ) {
                                                                                    mdialog.dismiss()
                                                                                    val snackBar =
                                                                                        Snackbar.make(
                                                                                            requireActivity().findViewById(
                                                                                                android.R.id.content
                                                                                            ),
                                                                                            "Order Cancellation Failed.",
                                                                                            Snackbar.LENGTH_LONG
                                                                                        )
                                                                                    snackBar.setBackgroundTint(
                                                                                        resources.getColor(
                                                                                            R.color.red
                                                                                        )
                                                                                    )
                                                                                    snackBar.setTextColor(
                                                                                        resources.getColor(
                                                                                            android.R.color.white
                                                                                        )
                                                                                    )
                                                                                    snackBar.show()
                                                                                    vibratePhone()
                                                                                }

                                                                            })


                                                                    }
                                                                    dialog2.show()
                                                                }
                                                                dialog.show()

                                                            }
                                                        }

                                                    } else {
                                                        mdialog.dismiss()
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.code().toString()
                                                            }"
                                                        )
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.message().toString()
                                                            }"
                                                        )
                                                        Log.e(
                                                            TAG,
                                                            "onResponse: ${
                                                                response.raw().toString()
                                                            }"
                                                        )
                                                        val snackBar = Snackbar.make(
                                                            requireActivity().findViewById(android.R.id.content),
                                                            "Something Went Wrong.",
                                                            Snackbar.LENGTH_LONG
                                                        )
                                                        snackBar.setBackgroundTint(
                                                            resources.getColor(
                                                                R.color.red
                                                            )
                                                        )
                                                        snackBar.setTextColor(
                                                            resources.getColor(
                                                                android.R.color.white
                                                            )
                                                        )
                                                        snackBar.show()
                                                        vibratePhone()
                                                        
                                                    }

                                                    override fun onFailure(
                                                        call: Call<JsonObject>,
                                                        t: Throwable
                                                    ) {
                                                        mdialog.dismiss()
                                                        val snackBar = Snackbar.make(
                                                            requireActivity().findViewById(android.R.id.content),
                                                            "Something Went Wrong.",
                                                            Snackbar.LENGTH_LONG
                                                        )
                                                        snackBar.setBackgroundTint(
                                                            resources.getColor(
                                                                R.color.red
                                                            )
                                                        )
                                                        snackBar.setTextColor(
                                                            resources.getColor(
                                                                android.R.color.white
                                                            )
                                                        )
                                                        snackBar.show()
                                                        vibratePhone()
                                                    }

                                                })
                                            }
                                        }
                                    
                                    }else{
                                        Log.e(TAG, "onResponse************: ${response2.raw()}", )
                                    }
                                    
                                    
                                }

                                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                    Log.e(TAG, "onResponse**********: ${t.message}", )
                                }

                            })
                        }



                }

                override fun onFailure(call: Call<TokenCall>, t: Throwable) {
                    Log.e(ContentValues.TAG, "onResponse: ${t.localizedMessage}")
                }

            })
        }


        binding.trackBtn.setOnClickListener {
            val url = "https://shiprocket.co/tracking/$awb"
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(context?.resources!!.getColor(R.color.black))
            builder.addDefaultShareMenuItem()

            val anotherCustomTab = CustomTabsIntent.Builder().build()

            val intent = anotherCustomTab.intent
            intent.data = Uri.parse("https://shiprocket.co/tracking/$awb")

            builder.setShowTitle(true)

            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context!!, Uri.parse(url))
        }

        binding.backBtn.setOnClickListener {
            activity?.onBackPressed()
        }


        return binding.root
    }

    fun getOrderedProducts(iteamList: ArrayList<Order>){
        val cartIteamList = ArrayList<CartItem>()
        for (i in iteamList){
            cartIteamList.addAll(i.orderedProducts)
        }
        val adaptor = CartAdaptor(requireContext(),this,cartIteamList)
        binding.recyclerView10.adapter = adaptor
        binding.recyclerView10.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

    }

    fun getCalculatedDate(date: String, dateFormat: String, days: Int): String {
        val sdf = SimpleDateFormat(dateFormat)

        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(date) as Date // parsed date and setting to calendar


        calendar.add(Calendar.DATE, days) // number of days to add

        return sdf.format(calendar.time)
    }




}