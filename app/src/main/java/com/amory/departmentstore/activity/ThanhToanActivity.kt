package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityThanhToanBinding
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.NotificationReponse
import com.amory.departmentstore.model.SendNotification
import com.amory.departmentstore.model.UserModel
import com.amory.departmentstore.retrofit.APIPushNotification
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.amory.departmentstore.retrofit.RetrofitNotification
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class ThanhToanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThanhToanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThanhToanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tienHang()
        onClickBack()
        onClickDatHang()
        binding.txtTongtien.text = formatAmount(tienHang().toString())

    }

    private fun onClickDatHang() {
        val full_name =
            Utils.user_current?.first_name.toString() + " " + Utils.user_current?.last_name.toString()
        val phone = Utils.user_current?.mobiphone.toString()
        val user_id = Utils.user_current?.id
        val total = Utils.mangmuahang.getSoluong()
        val total_money = tienHang().toFloat()
        val gson: Gson = GsonBuilder().setLenient().create()
        val detail = gson.toJson(Utils.mangmuahang)
        binding.nameEt.setText(full_name)
        binding.phoneET.setText(phone)
        /*  Toast.makeText(this,user_id.toString(),Toast.LENGTH_LONG).show()*/
        binding.btnDathang.setOnClickListener {
            val address = binding.addressET.text.toString().trim()
            val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
            val call =
                service.taodonhang(user_id, full_name, phone, total, total_money, address, detail)
            call.enqueue(object : Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    if (response.isSuccessful) {
                        pushNotification()
                        val gioHangSet = Utils.manggiohang.map { it.tensanphamgiohang }.toSet()
                        val muaHangSet = Utils.mangmuahang.map { it.tensanphamgiohang }.toSet()
                        val itemsToRemove = muaHangSet.intersect(gioHangSet)
                        Utils.manggiohang.removeAll { gioHang -> itemsToRemove.contains(gioHang.tensanphamgiohang) }
                        Utils.mangmuahang.clear()
                        Toast.makeText(this@ThanhToanActivity, address, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    Toast.makeText(
                        this@ThanhToanActivity,
                        "Khong Thanh cong ",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("loi", t.message.toString())
                    t.printStackTrace()
                }
            })
        }
    }

    private fun pushNotification() {
        val token = "f9eBM0y8TPOtuCQEqVnK_o:APA91bHWJ_irRTzKhlTvlveAxZgNYSYQVq0jkl1QBUerq3toSmHbGw1syGuwQx2-hyMXBn-Q9b8ErOnIDgq_OZcfjk0rQY_gkrxLzVd8_8EBz7tczRCBRCb1uMKelCu4r97XAwuEXDT2"
        val data: MutableMap<String, String> = HashMap()
        data["title"] = "Thông báo"
        data["body"] = "Bạn có đơn hàng mới"
        val sendNoti = SendNotification(token,data)
        val service = RetrofitNotification.retrofitInstance.create(APIPushNotification::class.java)
        val call = service.sendNotification(sendNoti)
        call.enqueue(object : Callback<NotificationReponse>{
            override fun onResponse(
                call: Call<NotificationReponse>,
                response: Response<NotificationReponse>
            ) {
            }

            override fun onFailure(call: Call<NotificationReponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun MutableList<GioHang>.getSoluong(): Int {
        var totalSoluong = 0
        for (gioHang in Utils.mangmuahang) {
            totalSoluong += gioHang.soluongsanphamgiohang
        }
        return totalSoluong
    }

    private fun onClickBack() {
        binding.imvBackThanhtoan.setOnClickListener {
            onBackPressed()
        }
    }

    private fun tienHang(): Long {
        var tienhang = intent.getLongExtra("tienhang", 0)
        tienhang += 30000
        return tienhang
    }

    private fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${format.format(number)}đ"
    }
}