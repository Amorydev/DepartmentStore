package com.amory.departmentstore.retrofit.APIBanHang

import android.content.Context
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.retrofit.AuthInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var BASE_URL = Utils.BASE_URL
    private lateinit var authInterceptor: AuthInterceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient.Builder().addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
    }

    private val gson: Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okBuilder.build())
            .build()
    }

    fun init(context: Context) {
        authInterceptor = AuthInterceptor(context)
    }
}