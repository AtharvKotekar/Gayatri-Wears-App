package com.gayatriladieswears.app.Fragments

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gayatriladieswears.app.*
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Adaptors.ReturnIteamAdaptor
import com.gayatriladieswears.app.Model.*
import com.gayatriladieswears.app.databinding.FragmentOrderReturnBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OrderReturnFragment : Fragment() {

    lateinit var binding: FragmentOrderReturnBinding
    private lateinit var orderId: String
    private lateinit var transactionId: String
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var contact: String
    private lateinit var products: ArrayList<CartItem>
    private lateinit var amount: String
    private lateinit var pincode: String
    private lateinit var email: String
    private lateinit var landmark: String
    private lateinit var shippingCharges: String
    var city: String = ""
    var state: String = ""
    var country: String = ""
    lateinit var mdialog: Dialog
    var token = ""
    lateinit var selectedProducts: ArrayList<CartItem>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderReturnBinding.inflate(inflater, container, false)

        mdialog = Dialog(requireContext())
        mdialog.setContentView(R.layout.laoding_dialog)
        mdialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mdialog.setCancelable(false)
        mdialog.show()


        selectedProducts = ArrayList()

        orderId = arguments?.getString("orderID").toString()
        name = arguments?.getString("name").toString()
        address = arguments?.getString("address").toString()
        city = arguments?.getString("city").toString()
        state = arguments?.getString("state").toString()
        country = arguments?.getString("country").toString()
        products = arguments?.getParcelableArrayList<CartItem>("products") as ArrayList<CartItem>
        pincode = arguments?.getString("pincode").toString()
        email = arguments?.getString("email").toString()
        landmark = arguments?.getString("landmark").toString()
        shippingCharges = arguments?.getString("shippingCharges").toString()
        contact = arguments?.getString("phone").toString()
        transactionId = arguments?.getString("transactionID").toString()
        amount = arguments?.getString("amount").toString()
        Log.e(TAG, "onCreateView: $transactionId", )


        Log.e(TAG, "onCreateView: $city",)
        Log.e(TAG, "onCreateView: $state",)
        Log.e(TAG, "onCreateView: $country",)
        Log.e(TAG, "onCreateView: $pincode",)
        Log.e(
            TAG,
            "onCreateView: ${
                products.forEach {
                    it.name
                }
            }",
        )




        CoroutineScope(IO).launch {
            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            val orderServices = retrofitBuilder.create(OrderServices::class.java)

            val tokenc = Token("gayatriauth@gmail.com", "Gayatri@2022")
            val call = orderServices.getToken(tokenc)

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            call.enqueue(object : Callback<TokenCall> {
                override fun onResponse(call: Call<TokenCall>, response: Response<TokenCall>) {
                    if (response.isSuccessful) {
                        token = "Bearer ${response.body()!!.token}"
                    } else {
                        Log.e(TAG, "onResponse: ${response.raw()}",)
                        Log.e(TAG, "onResponse: ${response.message()}",)
                        val snackBar = Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "Something Went Wrong,Please Try Again.",
                            Snackbar.LENGTH_LONG
                        )
                        snackBar.setBackgroundTint(resources.getColor(R.color.red))
                        snackBar.setTextColor(resources.getColor(R.color.white))
                        snackBar.show()
                        vibratePhone()
                    }
                }

                override fun onFailure(call: Call<TokenCall>, t: Throwable) {
                    val snackBar = Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Something Went Wrong,Please Try Again.",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                    snackBar.setTextColor(resources.getColor(R.color.white))
                    snackBar.show()
                    vibratePhone()
                    Log.e(TAG, "onFailure: ${t.localizedMessage}",)
                }

            })
        }

        getIteams()

        binding.backBtn.setOnClickListener {
            activity?.onBackPressed()
            selectedProducts.clear()
        }

        binding.cancelBtn.setOnClickListener {
            activity?.onBackPressed()
            selectedProducts.clear()
        }

        binding.returnBtn.setOnClickListener {
            if (selectedProducts.isEmpty()) {
                val snackBar = Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "You must have to select at least 1 product.",
                    Snackbar.LENGTH_LONG
                )
                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                snackBar.setTextColor(resources.getColor(R.color.white))
                snackBar.show()
                vibratePhone()
            } else {
                val productDialog = Dialog(requireContext())
                productDialog.setContentView(R.layout.retrun_dialog)
                productDialog.setCancelable(false)
                productDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                val recylerView = productDialog.findViewById<RecyclerView>(R.id.recyclerView11)
                val adaptor = CartAdaptor(requireContext(), this, selectedProducts)
                recylerView.adapter = adaptor
                recylerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                productDialog.show()

                val returnbtn = productDialog.findViewById<Button>(R.id.return_btn)
                val cancelbtn = productDialog.findViewById<Button>(R.id.cancel_btn)
                val close = productDialog.findViewById<ImageView>(R.id.imageView10)

                cancelbtn.setOnClickListener {
                    productDialog.dismiss()
                }

                close.setOnClickListener {
                    productDialog.dismiss()
                }

                returnbtn.setOnClickListener {
                    productDialog.dismiss()
                    mdialog.show()

                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formattedDate = current.format(formatter)
                    val returnItemList = ArrayList<OrderReturnIteam>()
                    var mPrice = 0
                    var mMrp = 0

                    for (i in selectedProducts) {
                        mPrice = mPrice + (i.price * i.cartQuantity.toInt())
                        mMrp = mMrp + (i.mrp * i.cartQuantity.toInt())
                        val returnItem = OrderReturnIteam(
                            i.name,
                            i.productId,
                            i.cartQuantity.toString().toInt(),
                            i.price,
                            0,
                            0,
                            false,
                            "",
                            "",
                            ""
                        )
                        returnItemList.add(returnItem)
                    }

                    Log.e(TAG, "onCreateView: ${(mMrp - (mMrp - mPrice)) * 100}", )

                    Log.e(TAG, "onCreateView: ${generateAlphanumericString(20)}",)
                    Log.e(TAG, "onCreateView: ${generateAlphanumericString(20)}",)
                    Log.e(TAG, "onCreateView: ${generateAlphanumericString(20)}",)


                    val orderReturn = OrderReturn(
                        generateAlphanumericString(20).toString(),
                        formattedDate,
                        "2994444",
                        name,
                        "",
                        address,
                        landmark,
                        city,
                        state,
                        country,
                        pincode.toString().toInt(),
                        email,
                        contact.replace("+91", ""),
                        "",
                        "",
                        "Gayatri Ladies Wears",
                        "",
                        "3146 A Ward, Rajyopadhye Lane, Mahadwar Road , Kolhapur, Maharashtra 416012",
                        "",
                        "Kolhapur",
                        "India",
                        416012,
                        "Maharashtra",
                        "gayatriladieswears@gmail.com",
                        "",
                        "9970364646",
                        returnItemList,
                        "PREPAID",
                        "0",
                        (mMrp - (mMrp - mPrice)),
                        1,
                        1,
                        1,
                        1
                    )

                    val gson = Gson()
                    val json = gson.toJson(orderReturn)
                    Log.e(TAG, "onCreateView: $json",)








                    CoroutineScope(IO).launch {


                        val retrofitBuilder = Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(BASE_URL)
                            .build()

                        val orderServices = retrofitBuilder.create(OrderServices::class.java)

                        val headerMap: HashMap<String, String> = HashMap<String, String>()
                        headerMap["Content-Type"] = "application/json;charset=UTF-8"
                        headerMap["Authorization"] = token

                        val returnCall = orderServices.returnProduct(headerMap, orderReturn)
                        returnCall.enqueue(object : Callback<JsonObject> {
                            override fun onResponse(
                                call: Call<JsonObject>,
                                response: Response<JsonObject>
                            ) {
                                if (response.isSuccessful) {
                                    val returnId = "R$orderId"

                                    val retrofitBuilder2 = Retrofit.Builder()
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .baseUrl(BASE_URL_RZP)
                                        .build()

                                    val orderServices2 =
                                        retrofitBuilder2.create(OrderServices::class.java)
                                    val refundCall = orderServices2.refundPayment(
                                        transactionId,
                                        (mMrp - (mMrp - mPrice)) * 100,"Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo"
                                    )
                                    refundCall.enqueue(object : Callback<JsonObject> {
                                        override fun onResponse(
                                            call: Call<JsonObject>,
                                            response2: Response<JsonObject>
                                        ) {
                                            if (response2.isSuccessful) {

                                                val refundId =
                                                    response2.body()?.get("id").toString()

                                                mdialog.dismiss()
                                                Log.e(
                                                    TAG,
                                                    "onResponse: ${response2.body().toString()}"
                                                )
                                                findNavController().navigate(R.id.action_orderReturnFragment_to_orderFragment)
                                                val snackBar = Snackbar.make(
                                                    requireActivity().findViewById(android.R.id.content),
                                                    "Order Returned Successfully.",
                                                    3000
                                                )
                                                snackBar.setBackgroundTint(resources.getColor(R.color.black))
                                                snackBar.setTextColor(resources.getColor(R.color.white))
                                                snackBar.show()
                                                vibratePhone()

                                                FirestoreClass().mFirestore.collection("Orders")
                                                    .document(orderId)
                                                    .update("transactionId", refundId)


                                                FirestoreClass().mFirestore.collection("Orders")
                                                    .document(orderId)
                                                    .update("orderId", returnId)


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
                                                val snackBar = Snackbar.make(
                                                    requireActivity().findViewById(android.R.id.content),
                                                    "Order Return Failed.",
                                                    Snackbar.LENGTH_LONG
                                                )
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
                                            val snackBar = Snackbar.make(
                                                requireActivity().findViewById(android.R.id.content),
                                                "Order Return Failed.",
                                                Snackbar.LENGTH_LONG
                                            )
                                            snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                            snackBar.setTextColor(resources.getColor(R.color.white))
                                            snackBar.show()
                                            vibratePhone()
                                        }

                                    })


                                } else {
                                    mdialog.dismiss()
                                    val snackBar = Snackbar.make(
                                        requireActivity().findViewById(android.R.id.content),
                                        "Order Return Failed.",
                                        Snackbar.LENGTH_LONG
                                    )
                                    snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                    snackBar.setTextColor(resources.getColor(R.color.white))
                                    snackBar.show()
                                    vibratePhone()
                                    Log.e(TAG, "onResponse: ${response.raw()}",)
                                    Log.e(TAG, "onResponse: ${response.code()}",)
                                    Log.e(TAG, "onResponse: ${response.message()}",)
                                }


                            }

                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                mdialog.dismiss()
                                val snackBar = Snackbar.make(
                                    requireActivity().findViewById(android.R.id.content),
                                    "Order Return Failed Please Try Again.",
                                    Snackbar.LENGTH_LONG
                                )
                                snackBar.setBackgroundTint(resources.getColor(R.color.red))
                                snackBar.setTextColor(resources.getColor(R.color.white))
                                snackBar.show()
                                vibratePhone()
                            }

                        })
                    }


                }

            }
        }







        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedProducts.clear()
    }

    fun getIteams() {
        val adaptor = ReturnIteamAdaptor(requireContext(), this, products)
        binding.recyclerView.adapter = adaptor
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mdialog.dismiss()

    }




    fun generateAlphanumericString(length: Int): String {
        val ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val random = Random()
        val sb = StringBuilder(length)
        for (i in 0 until length)
            sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
        return sb.toString()
    }
}





