package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.Interface.OnClickRvVoucher
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvVouvher
import com.amory.departmentstore.databinding.ActivityVoucherBinding
import com.amory.departmentstore.model.Voucher
import com.amory.departmentstore.model.VoucherModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.google.android.play.integrity.internal.m
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoucherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoucherBinding
    private lateinit var adapter: RvVouvher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        showVoucher()
    }

    private fun setupViews() {
        binding.txtKhongcovoucher.visibility = View.INVISIBLE
        binding.btnApdung.setOnClickListener {
            searchVoucher()
        }

        binding.imvBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun searchVoucher() {
        val txtVoucher = binding.txtVoucher.text?.trim().toString()
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.timkiemvoucher(txtVoucher)

        call.enqueue(object : Callback<VoucherModel> {
            override fun onResponse(call: Call<VoucherModel>, response: Response<VoucherModel>) {
                if (response.isSuccessful) {
                    val resultList = response.body()?.result
                    if (!resultList.isNullOrEmpty()) {
                        binding.txtKhongcovoucher.visibility = View.INVISIBLE
                        adapter = RvVouvher(resultList, object : OnClickRvVoucher{
                            override fun onClickVoucher(position: Int) {
                                val intent = Intent(this@VoucherActivity,ThanhToanActivity::class.java)
                                intent.putExtra("discount_type",resultList[position].discount_type)
                                intent.putExtra("discount_value",resultList[position].discount_value)
                                startActivity(intent)
                                finish()
                            }
                        })
                        binding.rvVoucher.adapter = adapter
                        binding.rvVoucher.setHasFixedSize(true)
                        binding.rvVoucher.layoutManager = LinearLayoutManager(
                            this@VoucherActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )

                    } else {
                        binding.txtKhongcovoucher.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("voucher", t.message.toString())
                Toast.makeText(this@VoucherActivity, "Thất bại", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showVoucher() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getVoucher()
        call.enqueue(object : Callback<VoucherModel> {
            override fun onResponse(call: Call<VoucherModel>, response: Response<VoucherModel>) {
                if (response.isSuccessful) {
                    val resultList = response.body()?.result
                    if (!resultList.isNullOrEmpty()) {
                        adapter = RvVouvher(resultList, object : OnClickRvVoucher{
                            override fun onClickVoucher(position: Int) {
                                val intent = Intent(this@VoucherActivity,ThanhToanActivity::class.java)
                                intent.putExtra("discount_type",resultList[position].discount_type)
                                intent.putExtra("discount_value",resultList[position].discount_value)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }
                        })
                        binding.rvVoucher.adapter = adapter
                        binding.rvVoucher.setHasFixedSize(true)
                        binding.rvVoucher.layoutManager = LinearLayoutManager(
                            this@VoucherActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
            }

            override fun onFailure(call: Call<VoucherModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("voucher", t.message.toString())
                Toast.makeText(this@VoucherActivity, "Thất bại", Toast.LENGTH_SHORT).show()
            }
        })
    }
}