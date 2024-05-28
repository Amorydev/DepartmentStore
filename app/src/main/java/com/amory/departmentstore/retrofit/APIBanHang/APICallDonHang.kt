package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderModelAdmin
import com.amory.departmentstore.model.UpdateDiaChiOrder
import com.amory.departmentstore.model.UpdateOrderModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface APICallDonHang {
    /*Call DonHangModel*/
    @GET("orders/me")
    fun getOrder():Call<OrderModel>
    @GET("orders")
    fun getOrderAdmin():Call<OrderModelAdmin>
    @GET("orders/user/{id}")
    fun timKiemTheoId(
        @Path("id") userId:Int?
    ):Call<OrderModelAdmin>
    @PATCH("orders/{id}")
    @Multipart
    fun suaTinhTrang(
        @Path("id") id: Int?,
        @Part("status") name: RequestBody
    ):Call<UpdateOrderModel>

    @PATCH("orders/info/{id}")
    @FormUrlEncoded
    fun suaDiaChi(
        @Path("id") id: Int?,
        @Field("address") address: String,
        @Field("fullName") fullName: String,
        @Field("phone") phone: String
    ): Call<UpdateOrderModel>

    @DELETE("orders/{id}")
    fun huyDonHang(
        @Path("id") id: Int?
    ):Call<UpdateOrderModel>
}