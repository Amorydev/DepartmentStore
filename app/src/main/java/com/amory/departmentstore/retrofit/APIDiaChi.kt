package com.amory.departmentstore.retrofit

import com.amory.departmentstore.model.Province
import retrofit2.Call
import retrofit2.http.GET

interface APIDiaChi {
    @GET("province")
    fun getTinh(): Call<List<Province>>
}