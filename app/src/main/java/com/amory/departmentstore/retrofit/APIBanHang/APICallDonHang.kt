package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.DonHangModel
import com.amory.departmentstore.model.OrderModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface APICallDonHang {
    /*Call DonHangModel*/
    @GET("orders/me")
    fun getOrder():Call<OrderModel>
}