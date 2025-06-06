package com.gayatriladieswears.app.Model

import android.os.Parcelable
import com.gayatriladieswears.app.Fragments.OrderReturnFragment
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class OrderReturn(
    @SerializedName("order_id")var order_id:String? = null,
    @SerializedName("order_date")var order_date:String? = null,
    @SerializedName("channel_id")var channel_id:String? = null,
    @SerializedName("pickup_customer_name")var pickup_customer_name:String? = null,
    @SerializedName("pickup_last_name")var pickup_last_name:String? = null,
    @SerializedName("pickup_address")var pickup_address:String? = null,
    @SerializedName("pickup_address_2")var pickup_address_2:String? = null,
    @SerializedName("pickup_city")var pickup_city:String? = null,
    @SerializedName("pickup_state")var pickup_state:String? = null,
    @SerializedName("pickup_country")var pickup_country:String? = null,
    @SerializedName("pickup_pincode")var pickup_pincode:Int? = null,
    @SerializedName("pickup_email")var pickup_email:String? = null,
    @SerializedName("pickup_phone")var pickup_phone:String? = null,
    @SerializedName("pickup_isd_code")var pickup_isd_code:String? = null,
    @SerializedName("pickup_location_id")var pickup_location_id:String? = null,
    @SerializedName("shipping_customer_name")var shipping_customer_name:String? = null,
    @SerializedName("shipping_last_name")var shipping_last_name:String? = null,
    @SerializedName("shipping_address")var shipping_address:String? = null,
    @SerializedName("shipping_address_2")var shipping_address_2:String? = null,
    @SerializedName("shipping_city")var shipping_city:String? = null,
    @SerializedName("shipping_country")var shipping_country:String? = null,
    @SerializedName("shipping_pincode")var shipping_pincode:Int? = null,
    @SerializedName("shipping_state")var shipping_state:String? = null,
    @SerializedName("shipping_email")var shipping_email:String? = null,
    @SerializedName("shipping_isd_code")var shipping_isd_code:String? = null,
    @SerializedName("shipping_phone")var shipping_phone: String? = null,
    @SerializedName("order_items")var order_items:ArrayList<OrderReturnIteam>? = ArrayList<OrderReturnIteam>(),
    @SerializedName("payment_method")var payment_method:String? = null,
    @SerializedName("total_discount")var total_discount:String? = null,
    @SerializedName("sub_total")var sub_total:Int? = null,
    @SerializedName("length")var length:Int? = null,
    @SerializedName("breadth")var breadth:Int? = null,
    @SerializedName("height")var height:Int? = null,
    @SerializedName("weight")var weight:Int? = null
)



