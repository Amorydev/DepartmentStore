package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvLoaiSanPham
import com.amory.departmentstore.databinding.ActivityAdminThemSanPhamBinding
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.OnClickRvLoaiSanPham
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminThemSanPhamActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdminThemSanPhamBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminThemSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onclickSpiner()
    }

    private fun onclickSpiner() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getLoaisanPham()
        call.enqueue(object : Callback<LoaiSanPhamModel> {
            override fun onResponse(
                call: Call<LoaiSanPhamModel>,
                response: Response<LoaiSanPhamModel>
            ) {
                if (response.isSuccessful){
                    val list = mutableListOf<String>()
                    for (i in 0 until response.body()?.result?.size!!){
                        list.add(response.body()!!.result[i].name)
                    }
                    val adapter = ArrayAdapter(this@AdminThemSanPhamActivity,android.R.layout.simple_spinner_item,list as ArrayList)
                    binding.spinner.adapter = adapter
                }
            }

            override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}