package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvSanPhamCacLoai
import com.amory.departmentstore.databinding.ActivitySnackBinding
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class SnackActivity : AppCompatActivity() {
    val loai:Int = 2
    val page:Int = 1
    private lateinit var binding:ActivitySnackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        laySanPhamSnack()
    }

    private fun laySanPhamSnack() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getSanPhamTheoLoai(page, loai)
        call.enqueue(object : retrofit2.Callback<SanPhamModel>{
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if(response.isSuccessful){
                    val produce_snack = response.body()?.result
                    val adapter = produce_snack?.let { RvSanPhamCacLoai(it) }
                    binding.rvsanphamtheoloaiSnack.adapter = adapter
                    binding.rvsanphamtheoloaiSnack.setHasFixedSize(true)
                    binding.rvsanphamtheoloaiSnack.layoutManager = GridLayoutManager(
                        this@SnackActivity,
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