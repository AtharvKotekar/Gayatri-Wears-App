package com.gayatriladieswears.app.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
    val productId:String = "",
    val userId:String = "",
    val price:Int = 0,
    val mrp:Int = 0,
    val color:String = "",
    val size:String = "",
    val image:String = "",
    val cartQuantity:String = "",
    val name:String = "",
    val dis:String = ""
): Parcelable
