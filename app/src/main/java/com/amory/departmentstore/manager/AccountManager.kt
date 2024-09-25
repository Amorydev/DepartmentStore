package com.amory.departmentstore.manager

import com.amory.departmentstore.model.LoginModel
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AccountManager {
    private val service: APICallUser =
        RetrofitClient.retrofitInstance.create(APICallUser::class.java)

    fun login(
        email: String,
        password: String,
        callbackSuccess: (LoginModel) -> Unit,
        callbackError: (String) -> Unit
    ) {
        val call = service.dangnhaptaikhoan(email, password)
        call.enqueue(object : Callback<LoginModel> {
            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                callbackError(t.message.toString())
            }

            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                if (response.isSuccessful) {
                    callbackSuccess(response.body()!!)
                } else {
                    callbackError(response.message())
                }
            }
        })
    }

    fun getInfoUser(callbackSuccess: (User) -> Unit, callbackError: (String) -> Unit) {
        val callUser = service.getUser()
        callUser.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                callbackError(t.message.toString())
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    callbackSuccess(response.body()!!)
                } else {
                    callbackError(response.message())
                }
            }
        })
    }
}