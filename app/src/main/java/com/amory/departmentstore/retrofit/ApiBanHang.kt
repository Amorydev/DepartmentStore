package com.amory.departmentstore.retrofit


import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.SanPhamModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiBanHang {
    @GET("laysanpham.php")
    fun getData(): Call<SanPhamModel>
    @GET("layloaisanpham.php")
    fun getLoaisanPham():Call<LoaiSanPhamModel>
    @POST("laysanphamtheoloai.php")
    @FormUrlEncoded
    fun getSanPhamTheoLoai(
        @Field("loai") loai:Int
    ):Call<SanPhamModel>
    @POST("dangki.php")
    @FormUrlEncoded
    fun dangkitaikhoan(
        @Field("first_name") first_name:String,
        @Field("last_name") last_name:String,
        @Field("email") email:String,
        @Field("password") password:String,
        @Field("mobiphone") mobile:String
    ):Call<UserModel>
    @POST("dangnhap.php")
    @FormUrlEncoded
    fun dangnhaptaikhoan(
        @Field("email") email:String,
        @Field("password") password:String
    ):Call<UserModel>
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
    ):Call<UserModel>

    @POST("timkiem.php")
    @FormUrlEncoded
    fun timkiem(
        @Field("search") search:String
    ):Call<SanPhamModel>

}
