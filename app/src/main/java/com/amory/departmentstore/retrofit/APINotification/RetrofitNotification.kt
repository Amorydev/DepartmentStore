package com.amory.departmentstore.retrofit.APINotification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitNotification {
    private lateinit var retrofit: Retrofit
    private var BASE_URL = "https://fcm.googleapis.com/"

    val retrofitInstance: Retrofit
        get() {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }
}