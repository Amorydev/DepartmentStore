package com.amory.departmentstore.activity.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.adapter.RvKhuyenMai
import com.amory.departmentstore.databinding.ActivityKhuyenMaiBinding
import com.amory.departmentstore.model.Banner
import com.amory.departmentstore.model.BannerModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallBanners
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KhuyenMaiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityKhuyenMaiBinding
    private lateinit var listBanners: List<Banner>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKhuyenMaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listBanners = mutableListOf()
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
        call.enqueue(object : Callback<BannerModel>{
            override fun onResponse(
                call: Call<BannerModel>,
                response: Response<BannerModel>
            ) {
                if (response.isSuccessful){
                    listBanners = response.body()?.data!!
                    val adapter = RvKhuyenMai(listBanners)
                    binding.rvKhuyenmai.adapter = adapter
                    binding.rvKhuyenmai.layoutManager = LinearLayoutManager(this@KhuyenMaiActivity,LinearLayoutManager.VERTICAL,false)
                    binding.rvKhuyenmai.setHasFixedSize(true)
                }
            }

            override fun onFailure(call: Call<BannerModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("Error Banner",t.message.toString())
            }
        })
    }
}