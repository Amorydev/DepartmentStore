package com.amory.departmentstore.activity.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.Interface.OnClickRvVoucher
import com.amory.departmentstore.adapter.RvVouvher
import com.amory.departmentstore.databinding.ActivityVoucherBinding
import com.amory.departmentstore.manager.VoucherManager.searchVoucher
import com.amory.departmentstore.model.Voucher
import com.amory.departmentstore.model.VoucherModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallVouchers
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.viewModel.VoucherViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoucherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVoucherBinding
    private lateinit var adapter: RvVouvher
    private val viewModel: VoucherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        setUpViewModel()
        setUpObserver()
        onClickListener()
    }

    private fun setUpViewModel() {
        viewModel.getVoucher()
    }

    private fun onClickListener() {
        val txtVoucher = binding.txtVoucher.text?.trim().toString()
        binding.btnApdung.setOnClickListener { viewModel.searchVoucher(txtVoucher) }
        binding.imvBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setUpObserver() {
        viewModel.listVoucher.observe(this) { listVoucher ->
            if (listVoucher.isNotEmpty()) {
                renderRvVoucher(listVoucher)
            } else {
                binding.imvNoVoucher.visibility = View.VISIBLE
                binding.txtNoVoucher.visibility = View.VISIBLE
            }
        }
        viewModel.searchVoucherResult.observe(this) { searchResult ->
            if (searchResult.isNotEmpty()) {
                renderRvVoucher(searchResult)
            } else {
                binding.imvNoVoucher.visibility = View.VISIBLE
                binding.txtNoVoucher.visibility = View.VISIBLE
            }
        }
    }

    private fun renderRvVoucher(listVoucher: MutableList<Voucher>?) {
        binding.imvNoVoucher.visibility = View.INVISIBLE
        binding.txtNoVoucher.visibility = View.INVISIBLE
        adapter = RvVouvher(listVoucher!!, object : OnClickRvVoucher {
            override fun onClickVoucher(position: Int) {
                val intent = Intent().apply {
                    putExtra("discount_condition", listVoucher[position].term)
                    putExtra("discount_type", listVoucher[position].discountType)
                    putExtra("discount_value", listVoucher[position].discountValue)
                    putExtra("discount_code", listVoucher[position].code)
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

    private fun setupViews() {
        binding.imvNoVoucher.visibility = View.INVISIBLE
        binding.txtNoVoucher.visibility = View.INVISIBLE
        binding.txtKhongcovoucher.visibility = View.INVISIBLE

    }

}
