package com.amory.departmentstore.manager

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvKhuyenMai
import com.amory.departmentstore.model.Promotion
import com.amory.departmentstore.model.PromotionModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallBanners
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PromotionManager {
    fun getPromotions(callbackSuccess : (MutableList<Promotion>?) -> Unit, callbackError : (String) -> Unit) {
        val service = RetrofitClient.retrofitInstance.create(APICallBanners::class.java)
        val call = service.layKhuyenMai()
        call.enqueue(object : Callback<PromotionModel> {
            override fun onResponse(
                call: Call<PromotionModel>,
                response: Response<PromotionModel>
            ) {
                if (response.isSuccessful){
                    val listPromotions = response.body()?.data!!
                    callbackSuccess(listPromotions)
                    callbackError("Successfully")
                }
            }

            override fun onFailure(call: Call<PromotionModel>, t: Throwable) {
                callbackError("Failure ${t.message}")
            }
        })
    }
}