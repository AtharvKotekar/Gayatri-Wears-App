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
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Fragments.*
import com.gayatriladieswears.app.Model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject


open  class FirestoreClass {

    val mFirestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val phone = auth.currentUser?.phoneNumber.toString()


    fun register(fragment: SignupOtpFragment, userInfo: User, progressBar: ProgressBar) {

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

    fun addAddress(fragment: Fragment,addressInfo:Address){
        mFirestore.collection("Address")
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener { document ->
                mFirestore.collection("Address")

            }
    }

    fun getAddress(fragment:OrderAddressFragment,userId: String){
        mFirestore.collection("Address")
            .whereEqualTo("userId",userId)
            .get()
            .addOnSuccessListener { document ->

                Log.i(TAG, "getAddress: ${document.documents.toString()}")
                
                val addressList:ArrayList<Address> = ArrayList()
                for (i in document.documents){
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                    mFirestore.collection("Address").document(address.id).update("id",address.id.toString())
                }

                fragment.getAddressList(addressList)
            }
    }

    fun deleteAddress(fragment: Fragment,userId: String,id: String){
        mFirestore.collection("Address")
            .document(id)
            .delete()
            .addOnSuccessListener {
            }
    }


    fun updateCart(fragment: Fragment,quantity: String,userId: String,productId: String){
        val iteamList:ArrayList<CartItem> = ArrayList()
        mFirestore.collection("Cart")
            .document(userId+productId)
            .update("cartQuantity",quantity)

    }


    fun addToCart(fragment: Fragment,addtoCartItem: CartItem,userId: String,productId: String){
        mFirestore.collection("Cart")
            .document(userId+productId)
            .set(addtoCartItem, SetOptions.merge())
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                Log.i(TAG, "addToCart On Failed:${it.localizedMessage} ")
            }
    }


    fun checkProductExistInCart(fragment: ProductDetailFragment,productId:String,userId:String){
        mFirestore.collection("Cart")
            .whereEqualTo("userId",userId)
            .whereEqualTo("productId",productId)
            .get()
            .addOnSuccessListener { document ->
                    if(document.documents.size > 0){
                        fragment.addToCartProductExist()
                    }else{
                        fragment.addToCart()
                    }
                }

            }

    fun getCartProducts(fragment: Fragment,userId: String){
        val iteamList:ArrayList<CartItem> = ArrayList()
        mFirestore.collection("Cart")
            .whereEqualTo("userId",userId)
            .get()
            .addOnSuccessListener { document ->
                for (i in document.documents) {
                    val iteam = i.toObject(CartItem::class.java)
                    iteamList.add(iteam!!)
                }
                when(fragment){
                    is CartFragment -> {
                        fragment.getCartProducts(iteamList)
                    }
                }
            }
    }

    fun removeCartProduct(fragment: CartFragment,productId: String,userId: String){
        mFirestore.collection("Cart")
            .document(userId+productId)
            .delete()
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

    fun getCategorizeProduct(fragment:Fragment,field:String,filter:String,id:String){
        mFirestore.collection("Products")
            .whereEqualTo(field,filter)
            .whereNotEqualTo("id",id)
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

    fun getProductById(fragment: Fragment,productId: String,holder: CartAdaptor.myViewHolder){
        val iteamList: ArrayList<Product> = ArrayList()
        mFirestore.collection("Products")
            .whereEqualTo("id",productId)
            .get()
            .addOnSuccessListener { document ->
                for (i in document.documents){
                    val iteam = i.toObject(Product::class.java)
                    iteamList.add(iteam!!)
                    when(fragment){
                        is CartFragment -> {
                            fragment.getProductById(iteamList,holder)
                        }
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








