package com.gayatriladieswears.app

import android.R
import android.animation.AnimatorSet
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.Model.AWBResult
import com.gayatriladieswears.app.Model.GenrateAWB
import com.gayatriladieswears.app.Model.Product
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.firestore.SetOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val FragmentManager.currentNavigationFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()


fun Fragment.vibratePhone() {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= 26) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(200)
    }
}

fun getData(fragment: HomeFragment) {
    FirestoreClass().getTopCategories(fragment)
    FirestoreClass().getProducts(fragment,"tag", "Spotlight On")
    FirestoreClass().getSizes(fragment)
    FirestoreClass().getProducts(fragment,"tag", "Traditional")
    FirestoreClass().getFabrics(fragment)
    FirestoreClass().getProducts(fragment,"tag", "New Collection")
    FirestoreClass().getColors(fragment)
    FirestoreClass().getDeals(fragment)
}

fun getRandomString(length: Int) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9').shuffled().first()
    return (1..length)
        .shuffled()
        .map { allowedChars.random() }
        .joinToString("")
}









//
//
//
//
//
//
//val genrator = GenrateAWB(shipmentid, "", "")
//val genrateAWBCall =
//    orderServices.assignAWB(headerMap, genrator)
//genrateAWBCall.enqueue(object :
//    Callback<AWBResult> {
//    override fun onResponse(
//        call: Call<AWBResult>,
//        response: Response<AWBResult>
//    ) {
//
//        if (response.isSuccessful) {
//
//            dialog.dismiss()
//            findNavController().navigate(com.gayatriladieswears.app.R.id.action_checkOutFragment_to_paymentSuccessFragment)
//
//            val retrofitBuilder2 =
//                Retrofit.Builder()
//                    .addConverterFactory(
//                        GsonConverterFactory.create()
//                    )
//                    .baseUrl(BASE_URL_RZP)
//                    .build()
//
//            val orderServices2 =
//                retrofitBuilder2.create(
//                    OrderServices::class.java
//                )
//
//            val captureCall =
//                orderServices2.capturePayment(
//                    transactionId.toString(),
//                    amount.toString().toInt(),
//                    "INR"
//                )
//
//            CoroutineScope(Dispatchers.IO).launch {
//                captureCall.enqueue(object :
//                    Callback<JsonObject> {
//                    override fun onResponse(
//                        call: Call<JsonObject>,
//                        response: Response<JsonObject>
//                    ) {
//                        if (response.isSuccessful) {
//
//                        } else {
//                            Log.e(
//                                TAG,
//                                "onResponse: ${response.raw()}",
//                            )
//                            Log.e(
//                                TAG,
//                                "onResponse: ${
//                                    response.errorBody()
//                                        .toString()
//                                }",
//                            )
//                        }
//
//
//                    }
//
//                    override fun onFailure(
//                        call: Call<JsonObject>,
//                        t: Throwable
//                    ) {
//                        val snackBar =
//                            Snackbar.make(
//                                requireActivity().findViewById(
//                                    android.R.id.content
//                                ),
//                                "Something Wents Wrong.",
//                                Snackbar.LENGTH_LONG
//                            )
//                        snackBar.setBackgroundTint(
//                            resources.getColor(com.gayatriladieswears.app.R.color.red)
//                        )
//                        snackBar.setTextColor(
//                            resources.getColor(com.gayatriladieswears.app.R.color.white)
//                        )
//                        snackBar.show()
//                        vibratePhone()
//                    }
//
//                })
//
//                FirestoreClass().mFirestore.collection(
//                    "Orders"
//                )
//                    .document(orderID)
//                    .set(order, SetOptions.merge())
//                    .addOnSuccessListener {
//                        for (i in productList) {
//                            var stock = 0
//                            FirestoreClass().removeCartProduct(
//                                this@CheckOutFragment,
//                                i.productId,
//                                i.userId
//                            )
//                            FirestoreClass().mFirestore.collection(
//                                "Products"
//                            )
//                                .whereEqualTo(
//                                    "id",
//                                    i.productId
//                                )
//                                .get()
//                                .addOnSuccessListener { document ->
//                                    val products: ArrayList<Product> =
//                                        ArrayList()
//                                    for (j in document.documents) {
//                                        val iteam =
//                                            j.toObject(
//                                                Product::class.java
//                                            )!!
//                                        stock =
//                                            (iteam.stock.toInt() - i.cartQuantity.toInt())
//                                        products.add(
//                                            iteam
//                                        )
//                                    }
//                                    FirestoreClass().mFirestore.collection(
//                                        "Products"
//                                    )
//                                        .document(i.name)
//                                        .update(
//                                            "stock",
//                                            stock
//                                        )
//                                }
//
//                        }
//                    }
//            }
//
//
//        } else {
//            dialog.dismiss()
//
//            Log.e(
//                TAG,
//                "onResponse AWB : ${response.code()}"
//            )
//            Log.e(
//                TAG,
//                "onResponse AWB : ${response.message()}"
//            )
//            val snackBar = Snackbar.make(
//                requireActivity().findViewById(
//                    android.R.id.content
//                ),
//                "Something Wents Wrong.",
//                Snackbar.LENGTH_LONG
//            )
//            snackBar.setBackgroundTint(
//                resources.getColor(
//                    com.gayatriladieswears.app.R.color.red
//                )
//            )
//            snackBar.setTextColor(
//                resources.getColor(
//                    com.gayatriladieswears.app.R.color.white
//                )
//            )
//            snackBar.show()
//            vibratePhone()
//        }
//
//
//    }
//
//    override fun onFailure(
//        call: Call<AWBResult>,
//        t: Throwable
//    ) {
//
//        Log.e(
//            TAG,
//            "onResponse AWB : ${response.code()}"
//        )
//        Log.e(
//            TAG,
//            "onResponse AWB : ${response.message()}"
//        )
//        Log.e(
//            TAG,
//            "onResponse AWB : ${t.localizedMessage}"
//        )
//        dialog.dismiss()
//        val snackBar = Snackbar.make(
//            requireActivity().findViewById(
//                android.R.id.content
//            ),
//            "Something Wents Wrong.E - Failed",
//            Snackbar.LENGTH_LONG
//        )
//        snackBar.setBackgroundTint(
//            resources.getColor(
//                com.gayatriladieswears.app.R.color.red
//            )
//        )
//        snackBar.setTextColor(
//            resources.getColor(
//                com.gayatriladieswears.app.R.color.white
//            )
//        )
//        snackBar.show()
//        vibratePhone()
//    }
//
//})











