package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.PromotionModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface APICallBanners {
    /*Call KhuyenMaiModel*/
    @GET("banners")
    fun layKhuyenMai(
    ): Call<PromotionModel>
    @Multipart
    @POST("banners")
    fun themKhuyenMaiMoi(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody
    ): Call<PromotionModel>
    @Multipart
    @POST("banners")
    fun themKhuyenMaiMoi(
        @Part("name") name: RequestBody,
        @Part fileImage: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<PromotionModel>
    @DELETE("banners/{id}")
    fun xoaKhuyenMai(
        @Path("id") id: Int?
    ): Call<PromotionModel>
    @Multipart
    @PUT("banners/{id}")
    fun suaKhuyenMai(
        @Path("id") id: Int?,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody
    ): Call<PromotionModel>
    @Multipart
    @PUT("banners/{id}")
    fun suaKhuyenMai(
        @Path("id") id: Int?,
        @Part("name") name: RequestBody,
        @Part fileImage: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<PromotionModel>
}