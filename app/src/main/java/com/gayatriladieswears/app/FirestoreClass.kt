package com.gayatriladieswears.app
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Fragments.HomeFragment
import com.gayatriladieswears.app.Fragments.ProductDetailFragment
import com.gayatriladieswears.app.Fragments.ShopingFragment
import com.gayatriladieswears.app.Fragments.SignupAddressFragment
import com.gayatriladieswears.app.Model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


open  class FirestoreClass {

    val mFirestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val phone = auth.currentUser?.phoneNumber.toString()


    fun register(fragment: SignupAddressFragment, userInfo: User, progressBar: ProgressBar) {

        mFirestore.collection("users")
            .document(userInfo.phone)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                fragment.requireActivity().run {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }


            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Log.i(TAG, "register: Failed")

            }

    }

    fun getFilterDialogProducts(fragment: ShopingFragment, listColor: MutableList<String>,size:String,price:Int,category:String,material:String,pattern:String,occasion:String) {
        val iteamList: ArrayList<Product> = ArrayList()

        val mcolor = mutableListOf<String>()
        var mPrice = 0
        var mCategory = ""
        var mSize = ""
        var mMaterial = ""
        var mPattern = ""
        var mOccasion = ""


        if(listColor.isEmpty()){
            mcolor.add("Pink")
            mcolor.add("Blue")
            mcolor.add("Green")
            mcolor.add("Black")
            mcolor.add("Yellow")
            mcolor.add("White")
            mcolor.add("Red")
            mcolor.add("Gray")
            mcolor.add("Navy Blue")
            mcolor.add("Orange")
        } else{ for (i in listColor){ mcolor.add(i)} }

        if(price == 0){ mPrice = 100000 } else{mPrice = price}

        if(category == ""){ mCategory = "" } else{mCategory = category}

        if(size == "") { mSize = "" } else{ mSize = size }

        if(material == ""){ mMaterial = ""} else{ mMaterial = material}

        if(pattern == ""){ mPattern = ""} else{ mPattern = pattern}

        if(occasion == ""){ mOccasion = ""} else{ mOccasion = occasion}

        Log.i(TAG, "getFilterDialogProducts Pattern: $pattern")
        Log.i(TAG, "getFilterDialogProducts mPattern: $mPattern")



        val query:Query = mFirestore.collection("Products")


        if(mCategory == ""){
            if (mSize == ""){
                if(mMaterial == ""){
                    if(mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)
                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)
                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)
                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }

                }
                else{
                    if(mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("fabric",mMaterial)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)
                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("fabric",mMaterial)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)
                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("fabric",mMaterial)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("fabric",mMaterial)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }

                }
            }
            else {
                if(mMaterial == ""){
                    if (mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }

                }
                else{
                    if(mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .whereEqualTo("fabric",mMaterial)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .whereEqualTo("fabric",mMaterial)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .whereEqualTo("fabric",mMaterial)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereArrayContains("size", mSize)
                                .whereEqualTo("fabric",mMaterial)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }

                }

            }
        }
        else{
            if (mSize == ""){
                if(mMaterial == ""){
                    if(mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }

                }
                else {
                    if(mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereEqualTo("category", mCategory)
                                .whereEqualTo("fabric", mMaterial)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereEqualTo("category", mCategory)
                                .whereEqualTo("fabric", mMaterial)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereEqualTo("category", mCategory)
                                .whereEqualTo("fabric", mMaterial)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color", mcolor)
                                .whereLessThanOrEqualTo("price", mPrice)
                                .whereEqualTo("category", mCategory)
                                .whereEqualTo("fabric", mMaterial)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }

                }

            }
            else{
                if(mMaterial == ""){
                    if(mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereArrayContains("size",mSize)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereArrayContains("size",mSize)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereArrayContains("size",mSize)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereArrayContains("size",mSize)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }

                }
                else{
                    if(mPattern == ""){
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereArrayContains("size",mSize)
                                .whereEqualTo("fabric",mMaterial)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereArrayContains("size",mSize)
                                .whereEqualTo("fabric",mMaterial)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                    else{
                        if(mOccasion == ""){
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereEqualTo("fabric",mMaterial)
                                .whereArrayContains("size",mSize)
                                .whereEqualTo("pattern",mPattern)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }
                        else{
                            query
                                .whereIn("color",mcolor)
                                .whereLessThanOrEqualTo("price",mPrice)
                                .whereEqualTo("category",mCategory)
                                .whereEqualTo("fabric",mMaterial)
                                .whereArrayContains("size",mSize)
                                .whereEqualTo("pattern",mPattern)
                                .whereEqualTo("occasion",mOccasion)
                                .get()
                                .addOnSuccessListener { document ->
                                    for (i in document.documents) {
                                        val iteam = i.toObject(Product::class.java)
                                        iteamList.add(iteam!!)

                                    }
                                    fragment.getProducts(iteamList)
                                    Log.i(TAG, "getFilterDialogProducts: $iteamList")
                                }
                        }

                    }
                }
            }
        }
        listColor.clear()
        iteamList.clear()
    }




    fun getSpecifyProducts(fragment: Fragment, field: String, filter: String) {
        if (field == "size") {
            mFirestore.collection("Products")
                .whereArrayContains(field, filter)
                .get()
                .addOnSuccessListener { document ->
                    val iteamList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val iteam = i.toObject(Product::class.java)
                        iteamList.add(iteam!!)
                    }

                    when (fragment) {
                        is ShopingFragment -> {
                            fragment.getProducts(iteamList)
                        }
                    }
                }
        } else {
            mFirestore.collection("Products")
                .whereEqualTo(field, filter)
                .get()
                .addOnSuccessListener { document ->
                    val iteamList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val iteam = i.toObject(Product::class.java)
                        iteamList.add(iteam!!)
                    }

                    when (fragment) {
                        is ShopingFragment -> {
                            fragment.getProducts(iteamList)
                        }
                    }
                }
        }
    }


    fun getFilteredProduct(
        fragment: Fragment,
        field: String,
        filter: String,
        filterBy: String,
        order: Query.Direction
    ) {
        if (field == "size") {
            mFirestore.collection("Products")
                .orderBy(filterBy, order)
                .whereArrayContains(field, filter)
                .get()
                .addOnSuccessListener { document ->
                    Log.i(TAG, "onCreateView: ${document.documents}")
                    val iteamList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val iteam = i.toObject(Product::class.java)
                        iteamList.add(iteam!!)
                    }
                    when (fragment) {
                        is ShopingFragment -> {
                            fragment.getProducts(iteamList)
                        }
                    }
                }
        } else {
            mFirestore.collection("Products")
                .orderBy(filterBy, order)
                .whereEqualTo(field, filter)
                .get()
                .addOnSuccessListener { document ->
                    Log.i(TAG, "onCreateView: ${document.documents}")
                    val iteamList: ArrayList<Product> = ArrayList()
                    for (i in document.documents) {
                        val iteam = i.toObject(Product::class.java)
                        iteamList.add(iteam!!)
                    }
                    when (fragment) {
                        is ShopingFragment -> {
                            fragment.getProducts(iteamList)
                        }
                    }
                }

        }
    }


    fun getProducts(fragment: Fragment, field: String, filter: String) {
        mFirestore.collection("Products")
            .whereArrayContains(field, filter)
            .get()
            .addOnSuccessListener { document ->
                Log.i(TAG, "getProducts: ${document.documents.toString()}")
                val iteamList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Product::class.java)
                    iteamList.add(iteam!!)
                }

                when (fragment) {
                    is HomeFragment -> {
                        if (filter == "Spotlight On") {
                            fragment.spotlightOn(iteamList)
                        } else if (filter == "Traditional") {
                            fragment.getTraditional(iteamList)
                        } else if (filter == "New Collection") {
                            fragment.getNewCollections(iteamList)
                        }
                    }
                }
            }
    }

    fun getCategorizeProduct(fragment:Fragment,field:String,filter:String,name:String){

        mFirestore.collection("Products")
            .whereEqualTo(field,filter)
            .whereNotEqualTo("name",name)
            .get()
            .addOnSuccessListener { document ->
                Log.i(TAG, "getCategorized: ${document.documents.toString()}")
                val iteamList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Product::class.java)
                    iteamList.add(iteam!!)
                }
                when(fragment){
                    is ProductDetailFragment -> {
                        fragment.getCategorizedProduct(iteamList)
                    }
                }
            }
    }




    fun getTopCategories(fragment: HomeFragment) {
        mFirestore.collection("Top Categories")
            .get()
            .addOnSuccessListener { document ->
                Log.i(TAG, "getTopCategories: ${document.documents.toString()}")
                val iteamList: ArrayList<Info> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Info::class.java)
                    iteamList.add(iteam!!)
                }
                fragment.infoGet(iteamList)
            }
    }

    fun getFabrics(fragment: HomeFragment) {
        mFirestore.collection("Fabric")
            .orderBy("name")
            .get()
            .addOnSuccessListener { document ->
                Log.i(TAG, "getFabrics: ${document.documents.toString()}")
                val iteamList: ArrayList<Info> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Info::class.java)
                    iteamList.add(iteam!!)
                }
                fragment.infoGetFabric(iteamList)
            }
    }

    fun getColors(fragment: HomeFragment) {
        mFirestore.collection("Color")
            .get()
            .addOnSuccessListener { document ->
                Log.i(TAG, "getColor: ${document.documents.toString()}")
                val iteamList: ArrayList<Info> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Info::class.java)
                    iteamList.add(iteam!!)
                }
                fragment.infoGetColor(iteamList)
            }
    }

    fun getSizes(fragment: HomeFragment) {
        mFirestore.collection("Size")
            .orderBy("no")
            .get()
            .addOnSuccessListener { document ->
                Log.i(TAG, "get Sizes: ${document.documents.toString()}")
                val iteamList: ArrayList<Sizes> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Sizes::class.java)
                    iteamList.add(iteam!!)
                }
                fragment.getSizes(iteamList)
            }
    }

    fun getDeals(fragment: HomeFragment) {
        mFirestore.collection("Deals")
            .orderBy("prefix")
            .get()
            .addOnSuccessListener { document ->
                Log.i(TAG, "get Deals: ${document.documents.toString()}")
                val iteamList: ArrayList<Deal> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Deal::class.java)
                    iteamList.add(iteam!!)
                }
                fragment.getDeals(iteamList)
            }
    }




}








