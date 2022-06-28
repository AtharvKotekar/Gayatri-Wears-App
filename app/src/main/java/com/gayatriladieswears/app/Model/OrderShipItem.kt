package com.gayatriladieswears.app.Model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class OrderShipItem(
    @SerializedName("name")val name:String? = null,
    @SerializedName("sku")val sku:String? = null,
    @SerializedName("units")val units:Int? = null,
    @SerializedName("selling_price")val selling_price:Int? = null,
    @SerializedName("discount")val discount:Int? = null,
    @SerializedName("tax")val tax:Int? = null,
    @SerializedName("hsn")val hsn: String? = null
         )


@Parcelize
data class OrderReturnIteam(
    @SerializedName("name")val name:String? = null,
    @SerializedName("sku")val sku:String? = null,
    @SerializedName("units")val units:Int? = null,
    @SerializedName("selling_price")val selling_price:Int? = null,
    @SerializedName("discount")val discount:Int? = null,
    @SerializedName("tax")val tax:Int? = null,
    @SerializedName("qc_enable")val qc_enable: Boolean? = null,
    @SerializedName("qc_product_name")val qc_product_name: String? = null,
    @SerializedName("qc_product_image")val qc_product_image: String? = null,
    @SerializedName("hsn")val hsn: String? = null

):Parcelable