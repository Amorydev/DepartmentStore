package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.RevenueByCategoriesModel
import com.amory.departmentstore.model.RevenueByCategoriesResponse
import com.amory.departmentstore.model.RevenueByProductModel
import com.amory.departmentstore.model.RevenueByTimeModel
import com.amory.departmentstore.model.RevenueByYearModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query

interface APICallDoanhThu {
    @GET("admin/revenue/revenue-by-category")
    fun layDoanhThuTheoDanhMuc():Call<RevenueByCategoriesModel>
    @GET("admin/revenue/revenue-by-product")
    fun layDoanhThuTheoSanPham():Call<RevenueByProductModel>
    @GET("admin/revenue/by-month")
    fun layDoanhThuTheoNam(
        @Query("year") year: Int
    ): Call<RevenueByYearModel>
    @GET("admin/revenue/total-revenue")
    fun layDoanhThuTheoThoiGian(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Call<RevenueByTimeModel>
}