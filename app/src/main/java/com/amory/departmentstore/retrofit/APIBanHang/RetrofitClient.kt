package com.amory.departmentstore.retrofit.APIBanHang

import android.content.Context
import android.content.SharedPreferences
import com.amory.departmentstore.Utils.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private lateinit var retrofit: Retrofit
    private var BASE_URL = Utils.BASE_URL
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences("SAVE_TOKEN",Context.MODE_PRIVATE)
    }

    private val interceptor:Interceptor = Interceptor { chain ->
        val token = sharedPreferences.getString("token","")
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Authorization","Bearer $token")
        chain.proceed(builder.build())
    }
    private val okBuilder : OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(interceptor)
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    val retrofitInstance: Retrofit
        get() {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okBuilder.build())
                .build()
            return retrofit
        }
}