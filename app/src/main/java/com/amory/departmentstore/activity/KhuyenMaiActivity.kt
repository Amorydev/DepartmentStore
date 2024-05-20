package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvKhuyenMai
import com.amory.departmentstore.databinding.ActivityKhuyenMaiBinding
import com.amory.departmentstore.model.KhuyenMai
import com.amory.departmentstore.model.KhuyenMaiModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KhuyenMaiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityKhuyenMaiBinding
    private lateinit var listKhuyenMai: List<KhuyenMai>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKhuyenMaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listKhuyenMai = mutableListOf()
        showRvKhuyenMai()
        onClickBack()
    }

    private fun onClickBack() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showRvKhuyenMai() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.laykhuyenmai()
        call.enqueue(object : Callback<KhuyenMaiModel>{
            override fun onResponse(
                call: Call<KhuyenMaiModel>,
                response: Response<KhuyenMaiModel>
            ) {
                if (response.isSuccessful){
                    listKhuyenMai = response.body()?.banners!!
                    val adapter = RvKhuyenMai(listKhuyenMai)
                    binding.rvKhuyenmai.adapter = adapter
                    binding.rvKhuyenmai.layoutManager = LinearLayoutManager(this@KhuyenMaiActivity,LinearLayoutManager.VERTICAL,false)
                    binding.rvKhuyenmai.setHasFixedSize(true)
                }
            }

            override fun onFailure(call: Call<KhuyenMaiModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}