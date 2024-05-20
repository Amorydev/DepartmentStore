package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.DonHangModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APICallDonHang {
    /*Call DonHangModel*/
    @POST("xemdonhang.php")
    @FormUrlEncoded
    fun xemdonhang(
        @Field("user_id") user_id: Int?
    ): Call<DonHangModel>
    @POST("updatetinhtrang.php")
    @FormUrlEncoded
    fun updatetinhtrang(
        @Field("id") id: Int?,
        @Field("status") status: Int?
    ): Call<DonHangModel>
}