package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.CategoryModel
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

interface APICallCategories {
    /*Call LoaiSanPhamModel*/
    @GET("categories")
    fun getLoaisanPham(): Call<CategoryModel>
    @Multipart
    @POST("categories")
    fun themLoaiSanPhamMoi(
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<CategoryModel>
    @Multipart
    @POST("categories")
    fun themLoaiSanPhamMoi(
        @Part("name") name: RequestBody
    ): Call<CategoryModel>
    @Multipart
    @PUT("categories/{id}")
    fun suaLoaiSanPham(
        @Path("id") id: Int?,
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<CategoryModel>
    @Multipart
    @PUT("categories/{id}")
    fun suaLoaiSanPham(
        @Path("id") id: Int?,
        @Part("name") name: RequestBody
    ): Call<CategoryModel>
    @DELETE("categories/{id}")
    fun xoaLoaiSanPham(
        @Path("id") id: Int?
    ): Call<CategoryModel>
}