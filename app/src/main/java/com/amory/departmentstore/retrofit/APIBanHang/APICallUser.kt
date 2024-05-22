package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.RegisterModel
import com.amory.departmentstore.model.UserModel
import retrofit2.Call
import retrofit2.http.Body
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


    /*@POST("orders")
    @FormUrlEncoded
    fun taodonhang(
        @Field("user_id") user_id: Int?,
        @Field("full_name") fullName:String,
        @Field("email") email:String,
        @Field("phone") phone:String,
        @Field("address") address:String,
        @Field("note") note:String,
        @Field("total_money") total_money: Float,
        @Field("payment_method") payment_method: String,
        @Field("items") items: String
    ): Call<UserModel>*/
    @POST("orders")
    fun taodonhang(@Body orderRequest: OrderRequest): Call<UserModel>

    /*@Field("detail") detail:String*/
}