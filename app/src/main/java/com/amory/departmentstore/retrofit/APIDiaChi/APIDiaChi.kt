package com.amory.departmentstore.retrofit.APIDiaChi

import com.amory.departmentstore.model.Commune
import com.amory.departmentstore.model.CommuneModel
import com.amory.departmentstore.model.District
import com.amory.departmentstore.model.DistrictModel
import com.amory.departmentstore.model.Province
import com.amory.departmentstore.model.ProvinceModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIDiaChi {
    @GET("province")
    fun getTinh(): Call<ProvinceModel>
    @GET("district")
    fun getHuyen(@Query("province") province: String): Call<DistrictModel>
    @GET("commune")
    fun getXa(
        @Query("district") district:String
    ): Call<CommuneModel>
}