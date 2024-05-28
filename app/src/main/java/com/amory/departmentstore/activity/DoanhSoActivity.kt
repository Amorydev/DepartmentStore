package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.ViewPagerDoanhThuAdapter
import com.amory.departmentstore.databinding.ActivityDoanhSoBinding
import com.google.android.material.tabs.TabLayoutMediator

class DoanhSoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoanhSoBinding
    private lateinit var customProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoanhSoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickBack()
        showCustomProgressBar()
    }

    private fun showFragment() {
        val adapter = ViewPagerDoanhThuAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Theo thời gian"
                }

                1 -> {
                    tab.text = "Theo danh mục"
                }

                2 -> {
                    tab.text = "Theo sản phẩm"
                }
            }
        }.attach()
    }
    @SuppressLint("InflateParams")
    private fun showCustomProgressBar() {
        customProgressDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_progressbar, null)
        customProgressDialog.setContentView(view)
        customProgressDialog.setCancelable(false)
        customProgressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            customProgressDialog.dismiss()
            showFragment()
        }, 3000)
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

}