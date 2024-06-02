package com.amory.departmentstore.activity.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.Interface.OnClickRvVoucher
import com.amory.departmentstore.adapter.RvVouvher
import com.amory.departmentstore.databinding.ActivityVoucherBinding
import com.amory.departmentstore.model.VoucherModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallVouchers
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
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
    }

    private fun setupViews() {
        binding.imvNoVoucher.visibility = View.INVISIBLE
        binding.txtNoVoucher.visibility = View.INVISIBLE
        binding.txtKhongcovoucher.visibility = View.INVISIBLE
        searchVoucher()
        showVoucher()
        binding.imvBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun searchVoucher() {
        binding.btnApdung.setOnClickListener {
            val txtVoucher = binding.txtVoucher.text?.trim().toString()
            val service = RetrofitClient.retrofitInstance.create(APICallVouchers::class.java)
            val call = service.searchVoucher(txtVoucher)
            call.enqueue(object : Callback<VoucherModel> {
                override fun onResponse(call: Call<VoucherModel>, response: Response<VoucherModel>) {
                    if (response.isSuccessful) {
                        val resultList = response.body()?.data
                        if (!resultList.isNullOrEmpty()) {
                            binding.txtKhongcovoucher.visibility = View.INVISIBLE
                            adapter = RvVouvher(resultList, object : OnClickRvVoucher {
                                override fun onClickVoucher(position: Int) {
                                    val intent = Intent().apply {
                                        putExtra("discount_condition", resultList[position].term)
                                        putExtra("discount_type", resultList[position].discountType)
                                        putExtra("discount_value", resultList[position].discountValue)
                                        putExtra("discount_code", resultList[position].code)
                                    }
                                    setResult(Activity.RESULT_OK, intent)
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
                    binding.imvNoVoucher.visibility = View.INVISIBLE
                    binding.txtNoVoucher.visibility = View.INVISIBLE
                    Toast.makeText(this@VoucherActivity, "Thất bại", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showVoucher() {
        val service = RetrofitClient.retrofitInstance.create(APICallVouchers::class.java)
        val call = service.getVoucher()
        call.enqueue(object : Callback<VoucherModel> {
            override fun onResponse(call: Call<VoucherModel>, response: Response<VoucherModel>) {
                if (response.isSuccessful) {
                    binding.imvNoVoucher.visibility = View.INVISIBLE
                    binding.txtNoVoucher.visibility = View.INVISIBLE
                    val resultList = response.body()?.data
                    if (!resultList.isNullOrEmpty()) {
                        adapter = RvVouvher(resultList, object : OnClickRvVoucher {
                            override fun onClickVoucher(position: Int) {
                                val intent = Intent().apply {
                                    putExtra("discount_condition", resultList[position].term)
                                    putExtra("discount_type", resultList[position].discountType)
                                    putExtra("discount_value", resultList[position].discountValue)
                                    putExtra("discount_code", resultList[position].code)
                                }
                                setResult(Activity.RESULT_OK, intent)
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
                binding.imvNoVoucher.visibility = View.VISIBLE
                binding.txtNoVoucher.visibility = View.VISIBLE
            }
        })
    }
}
