package com.amory.departmentstore.retrofit.APINotification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitNotification {
    private const val BASE_URL = "https://fcm.googleapis.com/"

    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}