package com.amory.departmentstore.manager

import com.amory.departmentstore.model.Product
import com.amory.departmentstore.model.ProductResponse
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ProductManager {
    fun getProducts(
        callbackSuccess: (MutableList<Product>?) -> Unit,
        callbackError: (String) -> Unit
    ) {
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.getData()
        call.enqueue(object : Callback<ProductResponse> {
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                callbackError("Error ${t.message}")
            }

            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    callbackSuccess(response.body()?.data)
                } else {
                    callbackError("Error ${response.code()}")
                }
            }
        })
    }
}