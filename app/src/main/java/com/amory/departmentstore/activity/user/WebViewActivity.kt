package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityWebViewBinding
import com.amory.departmentstore.model.PaymentResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONException
import org.json.JSONObject

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
                    extractDataFromWebView()
                }
            }
        }


        val url = intent.getStringExtra("URL")
        if (url != null) {
            binding.webView.loadUrl(url)
        }
    }
    private fun extractDataFromWebView() {
        binding.webView.evaluateJavascript("JSON.stringify({ status: 200, message: 'Pay with VNPay successfully' })") { result ->
            val jsonObject = JSONObject(result)

            val status = jsonObject.getInt("status")
            val message = jsonObject.getString("message")

            if (status == 200) {
                // Xử lý khi status là 200
                Log.d("Status", "Success")
                Log.d("Message", message)
            } else {
                // Xử lý khi status không phải là 200
                Log.d("Status", "Error")
                Log.d("Message", message)
            }
        }
    }

    private fun isFinalPage(url: String?): Boolean {
        return url?.contains("vn-pay-callback") ?: false
    }


}
