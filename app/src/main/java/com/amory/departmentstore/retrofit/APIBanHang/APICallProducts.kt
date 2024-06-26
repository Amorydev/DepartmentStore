package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.SanPhamModel
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
import retrofit2.http.Query

interface APICallProducts {
    /*Call SanPhamModel*/
    @GET("products")
    fun getData(): Call<SanPhamModel>
    @GET("products/{id}")
    fun getProductsById(
        @Path("id") id: Int?
    ): Call<SanPhamModel>
    @GET("products")
    fun getSanPhamTheoLoai(
        @Query("category_id") loai: Int
    ): Call<SanPhamModel>
    @GET("products")
    fun timkiem(
        @Query("category_id") categoryId:Int?,
        @Query("keyword") search:String
    ): Call<SanPhamModel>

    @Multipart
    @POST("products")
    fun themsanphammoi(
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part fileImage: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("categoryId") category_id: RequestBody
    ): Call<SanPhamModel>
    @Multipart
    @POST("products")
    fun themsanphammoi(
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("categoryId") category_id: RequestBody
    ): Call<SanPhamModel>
    @DELETE("products/{id}")
    fun xoaSanPham(
        @Path("id") id: Int?
    ): Call<SanPhamModel>

    @Multipart
    @PUT("products/{id}")
    fun suasanpham(
        @Path("id") id:Int?,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part fileImage: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("categoryId") category_id: RequestBody
    ): Call<SanPhamModel>
    @Multipart
    @PUT("products/{id}")
    fun suasanpham(
        @Path("id") id:Int?,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("categoryId") category_id: RequestBody
    ): Call<SanPhamModel>
}