package com.amory.departmentstore.retrofit

import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    lateinit var retrofit: Retrofit
    val BASE_URL = "http://192.168.1.25/banhang/"
    val retrofitInstance: Retrofit
        get() {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }
}