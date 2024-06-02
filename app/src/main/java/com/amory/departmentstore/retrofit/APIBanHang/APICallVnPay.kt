package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.VnPayResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APICallVnPay {
    @POST("payment/vn-pay")
    fun getUrlVNPay(
        @Body orderRequest:OrderRequest
    ): Call<VnPayResponse>
}