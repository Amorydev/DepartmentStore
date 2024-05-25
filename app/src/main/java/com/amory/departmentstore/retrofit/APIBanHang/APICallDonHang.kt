package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderModelAdmin
import com.amory.departmentstore.model.UpdateOrderModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
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
    @PATCH("orders/{id}")
    @Multipart
    fun suaTinhTrang(
        @Path("id") id: Int?,
        @Part("status") name: RequestBody
    ):Call<UpdateOrderModel>
    @DELETE("orders/{id}")
    @Multipart
    fun huyDonHang(
        @Path("id") id: Int?
    ):Call<UpdateOrderModel>
}