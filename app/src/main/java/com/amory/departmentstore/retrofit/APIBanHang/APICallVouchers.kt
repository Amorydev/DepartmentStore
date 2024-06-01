package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.VoucherModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APICallVouchers {
    @GET("vouchers")
    fun getVoucher(
    ): Call<VoucherModel>
    @GET("vouchers/{code}")
    fun timKiemVoucher(
        @Path("code") code:String
    ): Call<VoucherModel>
}