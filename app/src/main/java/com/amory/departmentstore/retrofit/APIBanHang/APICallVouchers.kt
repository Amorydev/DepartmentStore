package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.VoucherModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface APICallVouchers {
    @GET("layvoucher.php")
    fun getVoucher(
    ): Call<VoucherModel>
    @POST("timkiemvoucher.php")
    @FormUrlEncoded
    fun timkiemvoucher(
        @Field("search") search:String
    ): Call<VoucherModel>
}