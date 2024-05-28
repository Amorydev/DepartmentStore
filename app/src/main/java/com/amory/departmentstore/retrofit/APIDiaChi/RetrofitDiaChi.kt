package com.amory.departmentstore.retrofit.APIDiaChi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitDiaChi {
    private lateinit var retrofit: Retrofit
    private var BASE_URL = "https://api.mysupership.vn/v1/partner/areas/"

    val retrofitInstance: Retrofit
        get() {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }
}