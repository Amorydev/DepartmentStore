package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.LoginModel
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.RegisterModel
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.model.RefreshToken
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface APICallUser {
    /*Call UserModel*/
    @POST("users/register")
    @FormUrlEncoded
    fun dangkitaikhoan(
        @Field("firstName") first_name: String,
        @Field("lastName") last_name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("roleId") role_id: Int
    ): Call<RegisterModel>

    @POST("users/login")
    @FormUrlEncoded
    fun dangnhaptaikhoan(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginModel>

    @GET("users/me")
    fun getUser(): Call<User>

    @POST("orders")
    fun taodonhang(@Body orderRequest: OrderRequest): Call<OrderModel>

    @POST("users/refreshToken")
    @FormUrlEncoded
    fun refreshToken(
        @Body refreshToken: String
    ): Call<RefreshToken>
}