package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.RegisterModel
import com.amory.departmentstore.model.UserModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APICallUser {
    /*Call UserModel*/
    @POST("users/register")
    @FormUrlEncoded
    fun dangkitaikhoan(
        @Field("firstName") first_name:String,
        @Field("lastName") last_name:String,
        @Field("email") email:String,
        @Field("password") password:String,
        @Field("roleId") role_id:Int
    ): Call<RegisterModel>
    @POST("users/login")
    @FormUrlEncoded
    fun dangnhaptaikhoan(
        @Field("email") email:String,
        @Field("password") password:String
    ): Call<UserModel>


    @POST("donhang.php")
    @FormUrlEncoded
    fun taodonhang(
        @Field("user_id") user_id: Int?,
        @Field("full_name") fullName:String,
        @Field("phone") phone:String,
        @Field("total") total:Int,
        @Field("total_money") total_money: Float,
        @Field("address") address:String,
        @Field("detail") detail:String
    ): Call<UserModel>
    @POST("updatetoken.php")
    @FormUrlEncoded
    fun updateToken(
        @Field("token") token:String,
        @Field("id") id: Int?
    ): Call<UserModel>
    @POST("getToken.php")
    @FormUrlEncoded
    fun getToken(
        @Field("role") role: Int,
    ): Call<UserModel>
}