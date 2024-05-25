package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amory.departmentstore.adapter.ViewFragmentAdapter
import com.amory.departmentstore.databinding.ActivityChiTietDatHangBinding
import com.google.android.material.tabs.TabLayoutMediator

class ChiTietDonHangActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChiTietDatHangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietDatHangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickBack()
        onShowViewPager()
    }

    private fun onShowViewPager() {
        val adapter = ViewFragmentAdapter(this)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager){
            tab, position ->
            when(position){
                0-> {tab.text = "Chờ xác nhận"}
                1-> {tab.text = "Đang xử lý"}
                2-> {tab.text = "Đang giao hàng"}
                3-> {tab.text = "Giao thành công"}
                4-> {tab.text = "Đã hủy"}
            }
        }.attach()

    }

    private fun onClickBack() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }


    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}