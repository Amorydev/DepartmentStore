package com.amory.departmentstore.retrofit.APIBanHang

import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.PaymentCallbackResponse
import com.amory.departmentstore.model.VnPayResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface APICallVnPay {
    @POST("payment/vn-pay")
    fun getUrlVNPay(
        @Body orderRequest:OrderRequest
    ): Call<VnPayResponse>
    @GET("payment/vn-pay-callback")
    fun getPaymentCallback(
        @Query("vnp_Amount")  vnpAmount :String,
        @Query("vnp_BankCode") vnpBankCode :String,
        @Query("vnp_BankTranNo") vnpBankTranNo :String,
        @Query("vnp_CardType") vnpCardType :String,
        @Query("vnp_OrderInfo") vnpOrderInfo :String,
        @Query("vnp_PayDate") vnpPayDate :String,
        @Query("vnp_ResponseCode") vnpResponseCode :String,
        @Query("vnp_TmnCode") vnpTmnCode :String,
        @Query("vnp_TransactionNo") vnpTransactionNo:String,
        @Query("vnp_TransactionStatus") vnpTransactionStatus :String,
        @Query("vnp_TxnRef") vnpTxnRef :String,
        @Query("vnp_SecureHash") vnpSecureHash :String
    ):Call<PaymentCallbackResponse>
}