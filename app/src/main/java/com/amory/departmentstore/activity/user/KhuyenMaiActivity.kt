package com.amory.departmentstore.activity.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvKhuyenMai
import com.amory.departmentstore.databinding.ActivityKhuyenMaiBinding
import com.amory.departmentstore.model.Promotion
import com.amory.departmentstore.model.PromotionModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallBanners
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KhuyenMaiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityKhuyenMaiBinding
    private lateinit var listPromotions: List<Promotion>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKhuyenMaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listPromotions = mutableListOf()
        showRvKhuyenMai()
        onClickBack()
    }

    private fun onClickBack() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showRvKhuyenMai() {
        val service = RetrofitClient.retrofitInstance.create(APICallBanners::class.java)
        val call = service.layKhuyenMai()
        call.enqueue(object : Callback<PromotionModel>{
            override fun onResponse(
                call: Call<PromotionModel>,
                response: Response<PromotionModel>
            ) {
                if (response.isSuccessful){
                    listPromotions = response.body()?.data!!
                    val adapter = RvKhuyenMai(listPromotions)
                    binding.rvKhuyenmai.adapter = adapter
                    binding.rvKhuyenmai.layoutManager = LinearLayoutManager(this@KhuyenMaiActivity,LinearLayoutManager.VERTICAL,false)
                    binding.rvKhuyenmai.setHasFixedSize(true)
                }
            }

            override fun onFailure(call: Call<PromotionModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("Error Banner",t.message.toString())
            }
        })
    }
}