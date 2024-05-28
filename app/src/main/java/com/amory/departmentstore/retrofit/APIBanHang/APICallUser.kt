package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.LoginModel
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.RegisterModel
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.model.RefreshToken
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.model.UpdateOrderModel
import com.amory.departmentstore.model.UpdatePasswordModel
import com.amory.departmentstore.model.User
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

    @POST("users/forgot/{email}")
    fun sendOtpToEmail(
        @Path ("email") email:String
    ):Call<UpdatePasswordModel>

    @POST("users/verifyOtp/{otp}/{email}")
    fun verifyOtp(
        @Path("otp") otp:Int,
        @Path ("email") email:String
    ):Call<UpdatePasswordModel>
    @POST("users/update-password/{email}")
    @Multipart
    fun updatePassword(
        @Path ("email") email:String,
        @Part ("newPassword") newPassword:RequestBody
    ):Call<UpdatePasswordModel>
    @POST("users/change-password")
    @Multipart
    fun changePassword(
        @Part ("password") password:RequestBody,
        @Part ("newPassword") newPassword:RequestBody
    ):Call<UpdatePasswordModel>
    @GET("users/get-all-user")
    fun getAllUser():Call<UserModel>
    @PATCH("users/disable/{id}")
    fun blockUser(
        @Path("id") id:Int
    ):Call<UpdateOrderModel>
    @PATCH("users/enable/{id}")
    fun allowedUser(
        @Path("id") id:Int
    ):Call<UpdateOrderModel>
}