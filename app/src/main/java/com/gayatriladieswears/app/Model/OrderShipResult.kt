package com.gayatriladieswears.app.Model


data class OrderShipResult(
    val order_id:Int,
    val shipment_id:Int,
    val status:String,
    val status_code:Int,
    val onboarding_completed_now:Int,
    val awb_code:String,
    val courier_company_id:String,
    val courier_name:String,
)



