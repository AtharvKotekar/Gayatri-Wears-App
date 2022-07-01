package com.gayatriladieswears.app

import com.gayatriladieswears.app.Model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

const val BASE_URL = "https://apiv2.shiprocket.in"
const val BASE_URL_RZP = "https://api.razorpay.com"


interface OrderServices {


    @POST("/v1/external/auth/login")
    fun getToken(
        @Body
        token: Token
    ):Call<TokenCall>


    @POST("/v1/external/orders/create/adhoc")
    fun setOrder(
        @HeaderMap headerMap: Map<String,String>,
        @Body orderShip: OrderShip,
    ):Call<OrderShipResult>

    @POST("/v1/external/courier/assign/awb")
    fun assignAWB(
        @HeaderMap headerMap: Map<String, String>,
        @Body genrateAWB: GenrateAWB
    ):Call<AWBResult>

    @POST("/v1/external/orders/cancel")
    fun cancelOrder(
        @HeaderMap headerMap: Map<String, String>,
        @Body id: CancelOrder
    ):Call<JsonObject>

    @GET("/v1/external/orders/show/{id}")
    fun getOrderDetail(
        @HeaderMap headerMap: Map<String, String>,
        @Path("id") orderId: String
    ):Call<JsonObject>


    @Headers("Content-Type: application/json")
    @POST("/v1/payments/{pay_id}/refund")
    @FormUrlEncoded
    fun refundPayment(
        @Path("pay_id") transactionID: String,
        @Field("amount") amount:Int,
        @Header("Authorization") auth:String
    ):Call<JsonObject>

    @POST("/v1/external/orders/create/return")
    fun returnProduct(
        @HeaderMap headerMap: Map<String, String>,
        @Body orderReturn: OrderReturn
    ):Call<JsonObject>


    @Headers("Authorization:Basic cnpwX3Rlc3RfVXljYlBRNmpkTFRKdGQ6OXFkWU5mSWpCbHJZemc1ckNDVFQzUTdo")
    @POST("/v1/payments/{pay_id}/capture")
    @FormUrlEncoded
    fun capturePayment(
        @Path("pay_id") transactionID: String,
        @Field("amount") amount: Int,
        @Field("currency") currency: String
    ):Call<JsonObject>

    @GET("/v1/external/courier/track/awb/{awb_code}")
    fun getTrackingData(
        @HeaderMap headerMap: Map<String, String>,
        @Path("awb_code") awbcode: String
    ):Call<JsonObject>




}



