package com.amory.departmentstore.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.User

class Utils {
    companion object{
        const val BASE_URL = "http://192.168.1.23/banhang/"
        val manggiohang: MutableList<GioHang> = mutableListOf()
        val mangmuahang: MutableList<GioHang> = mutableListOf()
        var user_current: User? = null
        lateinit var ID_NHAN:String
        const val GUI_ID = "id_gui"
        const val NHAN_ID = "id_nhan"
        const val MESS = "message"
        const val DATE_TIME = "datetime"
        const val PATH = "chat"

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