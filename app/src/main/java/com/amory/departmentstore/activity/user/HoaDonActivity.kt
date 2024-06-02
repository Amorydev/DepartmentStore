package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.activity.MainActivity
import com.amory.departmentstore.adapter.RvMuaNgay
import com.amory.departmentstore.databinding.ActivityHoaDonBinding
import java.text.NumberFormat
import java.util.Locale

class HoaDonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHoaDonBinding
    private lateinit var adapter: RvMuaNgay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHoaDonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        onClickBack()
    }

    private fun onClickBack() {
        binding.btnTrangChu.setOnClickListener {
            clearPurchaseItems()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnDonMua.setOnClickListener {
            clearPurchaseItems()
            val intent = Intent(this, ChiTietDonHangActivity::class.java)
            intent.putExtra("showFragment",0)
            startActivity(intent)
        }
    }

    private fun initViews() {
        val hoadonName = intent.getStringExtra("hoadon_name")
        val hoadonPhone = intent.getStringExtra("hoadon_phone")
        val hoadonAddress = intent.getStringExtra("hoadon_address")
        val hoadonGia = intent.getDoubleExtra("hoadon_gia", 0.0)
        val hoadonTongtien = intent.getDoubleExtra("hoadon_tongtien", 0.0)
        val hoadonGiamgia = intent.getDoubleExtra("hoadon_giamgia", 0.0)
        setupRecyclerView()
        bindInvoiceDetails(hoadonName, hoadonPhone, hoadonAddress, hoadonGia, hoadonGiamgia, hoadonTongtien)
    }

    private fun setupRecyclerView() {
        val list = Utils.mangmuahang
        adapter = RvMuaNgay(list)
        binding.rvHoaDon.adapter = adapter
        binding.rvHoaDon.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun bindInvoiceDetails(name: String?, phone: String?, address: String?, gia: Double, giamgia: Double, tongtien: Double) {
        binding.txtTen.text = name
        binding.txtSdt.text = phone
        binding.txtDiachi.text = address
        binding.txtGia.text = formatAmount(gia)
        binding.txtKhuyenmai.text = formatAmount(giamgia)
        binding.txtTongtien.text = formatAmount(tongtien)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearPurchaseItems() {
        val gioHangSet = Utils.manggiohang.map { it.idsanphamgiohang }.toSet()
        val muaHangSet = Utils.mangmuahang.map { it.idsanphamgiohang }.toSet()
        val itemsToRemove = muaHangSet.intersect(gioHangSet)
        Utils.manggiohang.removeAll { gioHang -> itemsToRemove.contains(gioHang.idsanphamgiohang) }
        Utils.mangmuahang.clear()
    }

    private fun formatAmount(amount: Double): String {
        val number = amount.toLong()
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${format.format(number)}đ"
    }


}
