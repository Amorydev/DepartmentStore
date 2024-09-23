package com.amory.departmentstore.manager

import com.amory.departmentstore.model.Product
import com.amory.departmentstore.model.ProductImages
import com.amory.departmentstore.model.ProductResponse
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient

object DetailProductImages {
    fun getDetailProductImages(id: Int, callback: (Product) -> Unit, callbackError: (String) -> Unit) {
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.getMoreInfo(id)
        call.enqueue(object : retrofit2.Callback<ProductResponse> {
            override fun onResponse(
                call: retrofit2.Call<ProductResponse>,
                response: retrofit2.Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val product = response.body()?.data?.get(0)
                    if (product != null) {
                        callbackError("Successfully")
                        callback(product)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ProductResponse>, t: Throwable) {
                callbackError("Fail ${t.message}")
            }
        })

    }
}
