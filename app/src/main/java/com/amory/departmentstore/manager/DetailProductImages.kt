package com.amory.departmentstore.manager

import com.amory.departmentstore.model.DetailProduct
import com.amory.departmentstore.model.ProductImages
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient

object DetailProductImages {
    fun getDetailProductImages(id: Int, callback: (List<DetailProduct>) -> Unit, callbackError: (String) -> Unit) {
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.getMoreInfo(id)
        call.enqueue(object : retrofit2.Callback<ProductImages> {
            override fun onResponse(
                call: retrofit2.Call<ProductImages>,
                response: retrofit2.Response<ProductImages>
            ) {
                if (response.isSuccessful) {
                    val productImages = response.body()?.data
                    if (productImages != null) {
                        callbackError("Successfully")
                        callback(productImages)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<ProductImages>, t: Throwable) {
                callbackError("Fail ${t.message}")
            }
        })

    }
}
