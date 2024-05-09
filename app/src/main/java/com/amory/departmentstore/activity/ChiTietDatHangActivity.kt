package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvChiTietDatHang
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityChiTietDatHangBinding
import com.amory.departmentstore.model.DonHangModel
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChiTietDatHangActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChiTietDatHangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietDatHangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layChiTietDatHang()
        onClickBack()
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun layChiTietDatHang() {
        val account = Paper.book().read<User>("user")
        val user_id = account?.id ?: Utils.user_current?.id
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.xemdonhang(user_id)
        call.enqueue(object : Callback<DonHangModel>{
            override fun onResponse(call: Call<DonHangModel>, response: Response<DonHangModel>) {
                if (response.isSuccessful) {
                   val result = response.body()?.result
                    binding.rvChitietdonhang.apply {
                        layoutManager = LinearLayoutManager(this@ChiTietDatHangActivity,LinearLayoutManager.VERTICAL,false)
                        adapter = RvChiTietDatHang(result)
                    }
                }
            }

            override fun onFailure(call: Call<DonHangModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}