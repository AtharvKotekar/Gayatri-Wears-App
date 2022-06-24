package com.gayatriladieswears.app.Model

data class AWBResult(
    var awb_assign_status: Int?,
    var response: Responce?,
)

data class Responce(
    var data:Data?,

)

data class Data(
    var courier_company_id:Int?,
    var awb_code:String?,
    var cod:Int?,
    var order_id:Int?,
    var shipment_id:Int?,
    var awb_code_status:Int?,
    var assigned_date_time:Time?,
    var applied_weight:Float?,
    var company_id:Int?,
    var courier_name:String?,
    var child_courier_name:String?,
    var routing_code:String?,
    var rto_routing_code:String?,
    var invoice_no:String?,
    var transporter_id:String?,
    var transporter_name:String?,
    var shipped_by:ShippedBy?,
)

data class Time(
    var date:String?,
    var timezone_type:Int?,
    var timezone:String?
)

data class ShippedBy(
    var shipper_company_name:String?,
    var shipper_address_1:String?,
    var shipper_address_2:String?,
    var shipper_city:String?,
    var shipper_state:String?,
    var shipper_country:String?,
    var shipper_postcode:String?,
    var shipper_first_mile_activated:Int?,
    var shipper_phone:String?,
    var lat:String?,
    var long:String?,
    var shipper_email:String?,
    var rto_company_name:String,
    var rto_address_1:String?,
    var rto_address_2:String?,
    var rto_city:String?,
    var rto_state:String?,
    var rto_country:String?,
    var rto_postcode:String?,
    var rto_phone:String?,
    var rto_email:String?,
)

