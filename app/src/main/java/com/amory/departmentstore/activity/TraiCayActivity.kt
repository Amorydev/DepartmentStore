package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvSanPhamCacLoai
import com.amory.departmentstore.databinding.ActivityTraiCayBinding
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class TraiCayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTraiCayBinding
    private val loai:Int = 3
    private val page:Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTraiCayBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        laySanPhamTraiCay()
        
    }

    private fun laySanPhamTraiCay() {
            val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
            val call = service.getSanPhamTheoLoai(page, loai)
            call.enqueue(object : retrofit2.Callback<SanPhamModel>{
                override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                    if(response.isSuccessful){
                        val produce_snack = response.body()?.result
                        val adapter = produce_snack?.let { RvSanPhamCacLoai(it) }
                        binding.rvsanphamtheoloaiTraicay.adapter = adapter
                        binding.rvsanphamtheoloaiTraicay.setHasFixedSize(true)
                        binding.rvsanphamtheoloaiTraicay.layoutManager = GridLayoutManager(
                            this@TraiCayActivity,
                            3,
                            GridLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
                override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }
}