package com.gayatriladieswears.app.Model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
class Order(
    var name: String,
    var tag: String,
    var address: String,
    var pincode: String,
    var landMark: String,
    var contact: String,
    var userId: String,
    var addressId:String,
    var orderedProducts: ArrayList<CartItem>,
    var transactionId:String,
    var amout:Int,
    var orderId:String,
    var date:String,
    var courierId:String,
    var totalQuantity:String,
    var email:String,
): Parcelable {
    constructor():this("","","","","","","","",
        ArrayList(),"",-1,"","","","","")
}


data class CancelOrder(
    @SerializedName("ids")var ids:ArrayList<Int>
)




