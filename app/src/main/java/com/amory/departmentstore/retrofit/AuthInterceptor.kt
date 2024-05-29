package com.amory.departmentstore.retrofit

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context):Interceptor {
    private var accessToken: String? = null
    private var refresherToken: String? = null
    private  var sharedPreferences: SharedPreferences = context.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferences.getString("token", "")
        var request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Authorization", "Bearer $token")
        /*Log.d("tokenne", token.toString())*/
        var response = chain.proceed(builder.build())

        if (response.isSuccessful) {
            return response
        }

        if (response.code == 401) {
            synchronized(this) {
                if (refresherToken != null) {
                    response.close()
                    val newToken = refreshToken(refresherToken!!)
                    accessToken = newToken
                    builder.addHeader("Authorization", "Bearer $newToken")
                    request = builder.build()
                    response = chain.proceed(request)
                }
            }
        }

        return response
    }

    private fun refreshToken(refreshToken: String): String {
        val service = RetrofitClient.retrofitInstance.create(APICallUser::class.java)
        val call = service.refreshToken(refreshToken)
        val response = call.execute()
        return if (response.isSuccessful) {
            response.body()?.access_token ?: ""
        } else {
            ""
        }
    }
}