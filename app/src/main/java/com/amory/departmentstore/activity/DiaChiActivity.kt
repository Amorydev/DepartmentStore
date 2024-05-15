package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.amory.departmentstore.databinding.ActivityDiaChiBinding
import com.amory.departmentstore.model.Province
import com.amory.departmentstore.retrofit.APIDiaChi
import com.amory.departmentstore.retrofit.RetrofitDiaChi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class DiaChiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDiaChiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaChiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ShowSpinerTinh()
    }

    private fun ShowSpinerTinh() {
        val service = RetrofitDiaChi.retrofitInstance.create(APIDiaChi::class.java)
        val call = service.getTinh()
        call.enqueue(object : Callback<List<Province>>{
            override fun onResponse(
                call: Call<List<Province>>,
                response: Response<List<Province>>
            ) {
                if (response.isSuccessful){
                    val list = response.body()
                    Toast.makeText(this@DiaChiActivity,list.toString(),Toast.LENGTH_LONG).show()
                    val adapter = ArrayAdapter(this@DiaChiActivity,android.R.layout.simple_spinner_item,list as ArrayList)
                    binding.spinerTinh.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Province>>, t: Throwable) {
                t.printStackTrace()
                Log.d("diachi",t.message.toString())
            }
        })
    }
}