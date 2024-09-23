package com.amory.departmentstore.manager

import com.amory.departmentstore.model.Category
import com.amory.departmentstore.model.CategoryModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallCategories
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CategoryManager {
    fun getCategories(callbackSuccess : (MutableList<Category>?) -> Unit, callbackError : (String) -> Unit)
    {
        val service = RetrofitClient.retrofitInstance.create(APICallCategories::class.java)
        val call = service.getLoaisanPham()
        call.enqueue(object : Callback<CategoryModel>{
            override fun onResponse(call: Call<CategoryModel>, response: Response<CategoryModel>) {
                if (response.isSuccessful){
                    val listCategories = response.body()?.data
                    callbackSuccess(listCategories)
                    callbackError("Successfully")
                }
            }

            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                callbackError("Error: ${t.message}")
            }
        })
    }
}