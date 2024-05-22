package com.amory.departmentstore.retrofit.APIDiaChi

import com.amory.departmentstore.model.Commune
import com.amory.departmentstore.model.District
import com.amory.departmentstore.model.Province
import retrofit2.Call
import retrofit2.http.GET

interface APIDiaChi {
    @GET("province")
    fun getTinh(): Call<List<Province>>
    @GET("district")
    fun getHuyen(): Call<List<District>>
    @GET("commune")
    fun getXa(): Call<List<Commune>>
}