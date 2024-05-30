package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.ViewFragmentAdapter
import com.amory.departmentstore.databinding.ActivityChiTietDatHangBinding
import com.google.android.material.tabs.TabLayoutMediator

class ChiTietDonHangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChiTietDatHangBinding
    private lateinit var customProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietDatHangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickBack()
        showCustomProgressBar()
    }

    private fun onShowViewPager() {
        val adapter = ViewFragmentAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Chờ xác nhận"
                }

                1 -> {
                    tab.text = "Đang xử lý"
                }

                2 -> {
                    tab.text = "Đang giao hàng"
                }

                3 -> {
                    tab.text = "Giao thành công"
                }

                4 -> {
                    tab.text = "Đã hủy"
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

            val showFragment = intent.getIntExtra("showFragment", -1)
            when (showFragment) {
                4 -> {
                    onShowViewPager()
                    binding.viewPager.post {
                        binding.viewPager.setCurrentItem(showFragment, false)
                    }
                }
                0 -> {
                    onShowViewPager()
                    binding.viewPager.post {
                        binding.viewPager.setCurrentItem(showFragment, false)
                    }
                }
                else -> {
                    onShowViewPager()
                }
            }
        }, 3000)
    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}