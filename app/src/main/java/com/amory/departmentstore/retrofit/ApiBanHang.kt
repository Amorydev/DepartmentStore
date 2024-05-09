package com.amory.departmentstore.retrofit


import com.amory.departmentstore.model.DonHangModel
import com.amory.departmentstore.model.KhuyenMaiModel
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.SanPhamModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiBanHang {
    /*Call SanPhamModel*/
    @GET("laysanpham.php")
    fun getData(): Call<SanPhamModel>
    @POST("laysanphamtheoloai.php")
    @FormUrlEncoded
    fun getSanPhamTheoLoai(
        @Field("loai") loai:Int
    ):Call<SanPhamModel>
    @POST("timkiem.php")
    @FormUrlEncoded
    fun timkiem(
        @Field("search") search:String
    ):Call<SanPhamModel>
    @POST("themsanphammoi.php")
    @FormUrlEncoded
    fun themsanphammoi(
        @Field("name") name: String,
        @Field("price") price:Long,
        @Field("image_url") image_url:String,
        @Field("category_id") category_id:Int,
        @Field("description") description: String
    ):Call<SanPhamModel>
    @POST("suasanpham.php")
    @FormUrlEncoded
    fun suasanpham(
        @Field("name") name: String,
        @Field("price") price:Long,
        @Field("image_url") image_url:String,
        @Field("category_id") category_id:Int,
        @Field("description") description: String,
        @Field("id") id: Int
    ):Call<SanPhamModel>
    @Multipart
    @POST("uploadhinhanh.php")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("file") name: RequestBody
    ): Call<SanPhamModel>
    @POST("xoasanpham.php")
    @FormUrlEncoded
    fun xoasanpham(
        @Field("id") id:Int
    ):Call<SanPhamModel>
    @POST("xoaloaisanpham.php")
    @FormUrlEncoded
    fun xoaloaisanpham(
        @Field("id") id:Int
    ):Call<SanPhamModel>

    /*Call LoaiSanPhamModel*/
    @GET("layloaisanpham.php")
    fun getLoaisanPham():Call<LoaiSanPhamModel>
    @POST("themloaisanpham.php")
    @FormUrlEncoded
    fun themloaisanphammoi(
        @Field("name") name: String,
        @Field("image_url") image_url:String,
        @Field("category_id") category_id:Int
    ):Call<LoaiSanPhamModel>
    @POST("sualoaisanpham.php")
    @FormUrlEncoded
    fun sualoaisanpham(
        @Field("name") name: String,
        @Field("image_url") image_url:String,
        @Field("category_id") category_id:Int,
        @Field("id") id: Int
    ):Call<LoaiSanPhamModel>

    /*Call UserModel*/
    @POST("dangki.php")
    @FormUrlEncoded
    fun dangkitaikhoan(
        @Field("first_name") first_name:String,
        @Field("last_name") last_name:String,
        @Field("email") email:String,
        @Field("password") password:String,
        @Field("mobiphone") mobile:String,
        @Field("uid") uid:String,
        @Field("token") token:String
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
    @POST("updatetoken.php")
    @FormUrlEncoded
    fun updateToken(
        @Field("token") token:String,
        @Field("id") id: Int?
    ):Call<UserModel>
    @POST("getToken.php")
    @FormUrlEncoded
    fun getToken(
        @Field("role") role: Int,
    ):Call<UserModel>

    /*Call DonHangModel*/
    @POST("xemdonhang.php")
    @FormUrlEncoded
    fun xemdonhang(
        @Field("user_id") user_id: Int?
    ):Call<DonHangModel>
    @POST("updatetinhtrang.php")
    @FormUrlEncoded
    fun updatetinhtrang(
        @Field("id") id: Int?,
        @Field("status") status: Int?
    ):Call<DonHangModel>

    /*Call KhuyenMaiModel*/
    @POST("laykhuyenmai.php")
    fun laykhuyenmai(
    ):Call<KhuyenMaiModel>
    @POST("themkhuyenmai.php")
    @FormUrlEncoded
    fun themkhuyenmai(
        @Field("khuyenmai") khuyenmai: String,
        @Field("thongtin") thongtin: String,
        @Field("image_url") image_url: String
    ):Call<KhuyenMaiModel>
    @POST("xoakhuyenmai.php")
    @FormUrlEncoded
    fun xoakhuyenmai(
        @Field("id") id: Int?,
    ):Call<KhuyenMaiModel>
    @POST("suakhuyenmai.php")
    @FormUrlEncoded
    fun suakhuyenmai(
        @Field("khuyenmai") khuyenmai: String,
        @Field("thongtin") thongtin: String,
        @Field("image_url") image_url: String,
        @Field("id") id: Int
    ):Call<KhuyenMaiModel>
}
