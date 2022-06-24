package com.gayatriladieswears.app.Model

import com.google.gson.annotations.SerializedName

data class GenrateAWB(
    @SerializedName("shipment_id")var shipment_id:String? = null,
    @SerializedName("courier_id")var courier_id:String? = null,
    @SerializedName("status")var status:String? = null,
)
