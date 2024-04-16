package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.User

class Utils {
    companion object{
        val manggiohang: MutableList<GioHang> = mutableListOf()
        val mangmuahang: MutableList<GioHang> = mutableListOf()
        lateinit var user:User
        @SuppressLint("ObsoleteSdkInt")
        fun kiemTraKetNoi(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            } else {
                val wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                val mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                wifiNetworkInfo?.isConnected == true || mobileNetworkInfo?.isConnected == true
            }
        }
    }
}