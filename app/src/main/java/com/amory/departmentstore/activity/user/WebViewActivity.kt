package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityWebViewBinding
import com.amory.departmentstore.model.OrderModel
import com.amory.departmentstore.model.OrderRequest
import com.amory.departmentstore.model.PaymentCallbackResponse
import com.amory.departmentstore.model.PaymentResponse
import com.amory.departmentstore.retrofit.APIBanHang.APICallUser
import com.amory.departmentstore.retrofit.APIBanHang.APICallVnPay
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page finished loading: $url")
                if (isFinalPage(url)) {
                    extractDataFromWebView(url)
                }
            }
        }


        val url = intent.getStringExtra("URL")
        if (url != null) {
            binding.webView.loadUrl(url)
        }
    }

    private fun extractDataFromWebView(url: String?) {

        val hoadonName = intent.getStringExtra("hoadon_name")
        val hoadonPhone = intent.getStringExtra("hoadon_phone")
        val hoadonAddress = intent.getStringExtra("hoadon_address")
        val hoadonGia = intent.getDoubleExtra("hoadon_gia", 0.0)
        val hoadonTongtien = intent.getDoubleExtra("hoadon_tongtien", 0.0)
        val hoadonGiamgia = intent.getDoubleExtra("hoadon_giamgia", 0.0)

        Log.d("TAG", "Hoa don name: $hoadonName")
        Log.d("TAG", "Hoa don phone: $hoadonPhone")
        Log.d("TAG", "Hoa don address: $hoadonAddress")
        Log.d("TAG", "Hoa don gia: $hoadonGia")
        Log.d("TAG", "Hoa don tong tien: $hoadonTongtien")
        Log.d("TAG", "Hoa don giam gia: $hoadonGiamgia")

        if (intent != null && intent.hasExtra("order_request")) {
            val orderRequest: OrderRequest? = intent.getParcelableExtra("order_request")
            Log.d("VIEW", orderRequest.toString())

            val query = url?.substringAfter("?")

            // Tách các cặp key-value
            val params = query?.split("&")?.associate {
                val (key, value) = it.split("=")
                key to URLDecoder.decode(value, "UTF-8")
            }

            // Lấy các giá trị cần thiết
            val vnpAmount = params?.get("vnp_Amount").toString()
            val vnpBankCode = params?.get("vnp_BankCode").toString()
            val vnpBankTranNo = params?.get("vnp_BankTranNo").toString()
            val vnpCardType = params?.get("vnp_CardType").toString()
            val vnpOrderInfo = params?.get("vnp_OrderInfo").toString()
            val vnpPayDate = params?.get("vnp_PayDate").toString()
            val vnpResponseCode = params?.get("vnp_ResponseCode").toString()
            val vnpTmnCode = params?.get("vnp_TmnCode").toString()
            val vnpTransactionNo = params?.get("vnp_TransactionNo").toString()
            val vnpTransactionStatus = params?.get("vnp_TransactionStatus").toString()
            val vnpTxnRef = params?.get("vnp_TxnRef").toString()
            val vnpSecureHash = params?.get("vnp_SecureHash").toString()

            /*Log.d("VIEW",vnpAmount.toString())
        Log.d("VIEW",vnpBankTranNo.toString())
        Log.d("VIEW",vnpBankCode.toString())
        Log.d("VIEW",vnpCardType.toString())
        Log.d("VIEW",vnpOrderInfo.toString())
        Log.d("VIEW",vnpPayDate.toString())
        Log.d("VIEW",vnpResponseCode.toString())
        Log.d("VIEW",vnpTmnCode.toString())
        Log.d("VIEW",vnpTransactionNo.toString())
        Log.d("VIEW",vnpTransactionStatus.toString())
        Log.d("VIEW",vnpTxnRef.toString())
        Log.d("VIEW",vnpSecureHash.toString())*/

            val service = RetrofitClient.retrofitInstance.create(APICallVnPay::class.java)
            val call = service.getPaymentCallback(
                vnpAmount,
                vnpBankCode,
                vnpBankTranNo,
                vnpCardType,
                vnpOrderInfo,
                vnpPayDate,
                vnpResponseCode,
                vnpTmnCode,
                vnpTransactionNo,
                vnpTransactionStatus,
                vnpTxnRef,
                vnpSecureHash
            )
            call.enqueue(object : Callback<PaymentCallbackResponse> {
                override fun onResponse(
                    call: Call<PaymentCallbackResponse>,
                    response: Response<PaymentCallbackResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.code == "OK") {
                            val serviceOrder =
                                RetrofitClient.retrofitInstance.create(APICallUser::class.java)
                            val callOrder = serviceOrder.taodonhang(orderRequest!!)
                            callOrder.enqueue(object : Callback<OrderModel> {
                                override fun onResponse(
                                    call: Call<OrderModel>,
                                    response: Response<OrderModel>
                                ) {
                                    if (response.isSuccessful) {
                                        val intent = Intent(this@WebViewActivity, HoaDonActivity::class.java)
                                        intent.putExtra("hoadon_name", hoadonName)
                                        intent.putExtra("hoadon_phone", hoadonPhone)
                                        intent.putExtra("hoadon_address", hoadonAddress)
                                        intent.putExtra("hoadon_gia", hoadonGia)
                                        intent.putExtra("hoadon_tongtien", hoadonTongtien)
                                        intent.putExtra("hoadon_giamgia", hoadonGiamgia)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@WebViewActivity,
                                            "Đặt hàng thất bại",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        /*Log.d("response error", response.errorBody()?.string().orEmpty())*/
                                    }
                                }

                                override fun onFailure(call: Call<OrderModel>, t: Throwable) {
                                    Toast.makeText(
                                        this@WebViewActivity,
                                        "Không thành công",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    /* Log.d("error", t.message.toString())*/
                                    t.printStackTrace()
                                }
                            })
                        }
                    }
                }

                override fun onFailure(call: Call<PaymentCallbackResponse>, t: Throwable) {
                }
            })
        }
    }


    private fun isFinalPage(url: String?): Boolean {
        return url?.contains("vn-pay-callback") ?: false
    }


}
