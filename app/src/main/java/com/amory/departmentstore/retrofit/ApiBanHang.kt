package com.amory.departmentstore.retrofit


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
}
