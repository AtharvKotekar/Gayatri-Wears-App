package com.gayatriladieswears.app
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.gayatriladieswears.app.Activities.HomeActivity
import com.gayatriladieswears.app.Adaptors.CartAdaptor
import com.gayatriladieswears.app.Fragments.OrderDetailFragment
import com.gayatriladieswears.app.Fragments.*
import com.gayatriladieswears.app.Model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


open  class FirestoreClass {

    val mFirestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val phone = auth.currentUser?.phoneNumber.toString()


    fun register(fragment: TermsAndContionsFragment, userInfo: User) {

        mFirestore.collection("users")
            .document(userInfo.phone)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                fragment.requireActivity().run {
                    val emptyCache = Cache()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    addEmptyCache(emptyCache)
                }


            }
            .addOnFailureListener {
                val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Register Failed : ${it.localizedMessage}", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
            }

    }

    fun getOrderDetailProducts(fragment: Fragment,orderId:String){
        mFirestore.collection("Orders").
                whereEqualTo("orderId",orderId)
            .get()
            .addOnSuccessListener {document ->
                val iteamList: ArrayList<Order> = ArrayList()
                for (i in document.documents) {
                    val iteam = i.toObject(Order::class.java)
                    iteamList.add(iteam!!)
                }
                when (fragment){
                    is OrderDetailFragment -> {
                        fragment.getOrderedProducts(iteamList)
                    }
                }
            }
    }


    fun addEmptyCache(cache: Cache){
        mFirestore.collection("Cache")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .set(cache, SetOptions.merge())
    }

    fun getRecentSearches(fragment: SearchFrsgment){
        var array = ArrayList<String>()
        mFirestore.collection("Cache")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                array = it.get("recentSearches") as ArrayList<String>
                fragment.getRecentSearches(array)
                }
            .addOnFailureListener {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()

            }
            }



    fun addtorecentSearch(fragment: Fragment,search: String){
        mFirestore.collection("Cache")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .update("recentSearches",FieldValue.arrayUnion(search))

    }

    fun getSearchReasults(fragment: Fragment,search:String) {
        mFirestore.collection("Products")
            .whereArrayContains("keywords", search.trim())
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
            .addOnFailureListener {
                when(fragment){
                    is SearchFrsgment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                    }
                }
            }
    }




    suspend fun getOrderedProducts(fragment: Fragment){
        val productList:ArrayList<Order> = ArrayList()
        mFirestore.collection("Orders")
            .orderBy("orderId",Query.Direction.DESCENDING)
            .whereEqualTo("userId",FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener {doucument ->
                for (i in doucument.documents){
                    val iteam = i.toObject(Order::class.java)!!
                    productList.add(iteam)
                }
                    when(fragment){
                        is OrderFragment -> {
                            fragment.getProducts(productList)
                        }
                    }
            }
            .addOnFailureListener {
                when(fragment){
                    is OrderFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()
                    }
                }
            }
    }




    fun addAddress(fragment: Fragment,addressInfo:Address){
        mFirestore.collection("Address")
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener { document ->
                mFirestore.collection("Address")
            }
            .addOnFailureListener {
                when(fragment){
                    is OrderAddAdressFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                    }
                }
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
            .addOnFailureListener {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()

                }
            }

    fun deleteAddress(fragment: Fragment,userId: String,id: String){
        mFirestore.collection("Address")
            .document(id)
            .delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                when(fragment){
                    is OrderAddressFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()
                    }
                }
            }
    }


    fun updateCart(fragment: Fragment,quantity: String,userId: String,productId: String){
        val iteamList:ArrayList<CartItem> = ArrayList()
        mFirestore.collection("Cart")
            .document(userId+productId)
            .update("cartQuantity",quantity)
            .addOnFailureListener {
                when(fragment){
                    is CartFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()
                    }
                }

            }

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
            .addOnFailureListener {
                when(fragment){
                    is ProductDetailFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                    }
                }
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
            .addOnFailureListener {
                val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
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

                    is CheckOutFragment -> {
                        fragment.getAllCartProducts(iteamList)
                    }
                }
            }
            .addOnFailureListener {
                when(fragment){
                    is CartFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()
                    }
                    is CheckOutFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()
                    }
                }

            }
    }

    fun removeCartProduct(fragment: Fragment,productId: String,userId: String){
        mFirestore.collection("Cart")
            .document(userId+productId)
            .delete()
            .addOnFailureListener {
                when(fragment){
                    is CartFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()
                    }
                }


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
                                .addOnFailureListener {
                                    val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                                    snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                                    snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                                    snackBar.show()
                                    fragment.vibratePhone()
                                    fragment.mDialog.dismiss()
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
                .addOnFailureListener {
                    when(fragment){
                        is ShopingFragment -> {
                            val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                            snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                            snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                            snackBar.show()
                            fragment.vibratePhone()
                            fragment.mDialog.dismiss()
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
                .addOnFailureListener {
                    when(fragment){
                        is ShopingFragment -> {
                            val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                            snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                            snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                            snackBar.show()
                            fragment.vibratePhone()
                            fragment.mDialog.dismiss()
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
                .addOnFailureListener {
                    when(fragment){
                        is ShopingFragment -> {
                            val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                            snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                            snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                            snackBar.show()
                            fragment.vibratePhone()
                            fragment.mDialog.dismiss()
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
                .addOnFailureListener {
                    when(fragment){
                        is ShopingFragment -> {
                            val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                            snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                            snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                            snackBar.show()
                            fragment.vibratePhone()
                            fragment.mDialog.dismiss()
                        }
                    }
                }
        }
    }


    suspend fun getProducts(fragment: Fragment, field: String, filter: String) {
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
            .addOnFailureListener {
                when(fragment){
                    is HomeFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                        fragment.dialog.dismiss()
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
            .addOnFailureListener {
                when(fragment){
                    is ProductDetailFragment -> {
                        val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                        snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                        snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                        snackBar.show()
                        fragment.vibratePhone()
                    }
                }
            }
    }


    suspend fun getTopCategories(fragment: HomeFragment) {
            mFirestore.collection("Top Categories")
                .orderBy("name",Query.Direction.DESCENDING)
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
                .addOnFailureListener {
                    val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                    snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                    snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                    snackBar.show()
                    fragment.vibratePhone()
                    fragment.dialog.dismiss()
                }
    }

    suspend fun getFabrics(fragment: HomeFragment) {
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
            .addOnFailureListener {
                val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
                fragment.dialog.dismiss()
            }
    }

    suspend fun getColors(fragment: HomeFragment) {
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
            .addOnFailureListener {
                val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
                fragment.dialog.dismiss()
            }
    }

    suspend fun getSizes(fragment: HomeFragment) {
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
            .addOnFailureListener {
                val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
                fragment.dialog.dismiss()
            }
    }

    suspend fun getDeals(fragment: HomeFragment) {
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
            .addOnFailureListener {
                val snackBar = Snackbar.make(fragment.requireActivity().findViewById(android.R.id.content), "Please Check Your Network Connection.", Snackbar.LENGTH_LONG)
                snackBar.setBackgroundTint(fragment.resources.getColor(R.color.red))
                snackBar.setTextColor(fragment.resources.getColor(R.color.white))
                snackBar.show()
                fragment.vibratePhone()
                fragment.dialog.dismiss()
            }
    }
    }













