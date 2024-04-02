package com.amory.departmentstore.retrofit


import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.SanPhamModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiBanHang {
    @GET("layloaisanpham.php")
    fun getData(): Call<SanPhamModel>
    @GET("loaisanpham.php")
    fun getLoaisanPham():Call<LoaiSanPhamModel>
}
