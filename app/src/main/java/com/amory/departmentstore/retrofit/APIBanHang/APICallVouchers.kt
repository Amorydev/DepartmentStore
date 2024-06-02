package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.VoucherModel
import com.amory.departmentstore.model.VoucherRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface APICallVouchers {
    @GET("vouchers")
    fun getVoucher(
    ): Call<VoucherModel>
    @GET("vouchers/{code}")
    fun searchVoucher(
        @Path("code") code:String
    ): Call<VoucherModel>
    @GET("vouchers/admin")
    fun getVoucherAdmin(
    ): Call<VoucherModel>
    @POST("vouchers")
    fun addVoucher(@Body voucherRequest: VoucherRequest):Call<VoucherModel>
    @DELETE("vouchers/{id}")
    fun deleteVoucher(@Path ("id") id:Int):Call<VoucherModel>
    @PUT("vouchers/{id}")
    fun updateVoucher(
        @Path ("id") id:Int,
        @Body voucherRequest: VoucherRequest
    ):Call<VoucherModel>
}