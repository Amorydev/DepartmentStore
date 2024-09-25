package com.amory.departmentstore.manager

import com.amory.departmentstore.model.Voucher
import com.amory.departmentstore.model.VoucherModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallVouchers
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object VoucherManager {
    private val service = RetrofitClient.retrofitInstance.create(APICallVouchers::class.java)
    fun getVoucher(callbackSuccess: (MutableList<Voucher>) -> Unit, callbackError : (String) -> Unit) {
        val call = service.getVoucher()
        call.enqueue(object : Callback<VoucherModel>{
            override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                callbackError(t.message.toString())
            }

            override fun onResponse(call: Call<VoucherModel>, response: Response<VoucherModel>) {
                if (response.isSuccessful) {
                    callbackSuccess((response.body()?.data ?: emptyList()) as MutableList<Voucher>)
                } else {
                    callbackError(response.message())
                }
            }
        })
    }

    fun searchVoucher(key: String, callbackSuccess: (MutableList<Voucher>) -> Unit, callbackError : (String) -> Unit){
        val call = service.searchVoucher(key)
        call.enqueue(object : Callback<VoucherModel>{
            override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                callbackError(t.message.toString())
            }

            override fun onResponse(call: Call<VoucherModel>, response: Response<VoucherModel>) {
                if (response.isSuccessful){
                    callbackSuccess((response.body()?.data ?: emptyList()).toMutableList())
                } else {
                    callbackError(response.message())
                }
            }
        })
    }
}