package com.gayatriladieswears.app.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id:String = "",
    val phone:String = "",
    val firstName:String = "",
    val lastName:String = ""
): Parcelable