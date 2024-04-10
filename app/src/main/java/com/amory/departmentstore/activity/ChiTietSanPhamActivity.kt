package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityChiTietSanPhamBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class ChiTietSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChiTietSanPhamBinding

    private var soluongsanpham = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        XulyChiTiet()
        onClickBack()
        onCLickCongTruSanPham()
        ThemVaoGioHang()
    }

    private fun putData() {
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("soluongsanpham",soluongsanpham)
        startActivity(intent)
    }

    private fun onCLickCongTruSanPham() {
        binding.txtCongSanpham.setOnClickListener {
            soluongsanpham += 1
            CapNhatSoLuongSanPham()

        }
        binding.txtTruSanpham.setOnClickListener {
            soluongsanpham -= 1
            if (soluongsanpham >= 0) {
                CapNhatSoLuongSanPham()
            }
        }
    }

    private fun CapNhatSoLuongSanPham() {
        binding.soluongsanpham.text = soluongsanpham.toString()
    }

    private fun onClickBack() {
        binding.imvBack.setOnClickListener {
            onBackPressed()
            putData()
        }
    }
    private fun ThemVaoGioHang(){
        binding.btnThemvaogiohang.setOnClickListener {
            binding.badgeCart.setText(soluongsanpham.toString())
        }
    }

    private fun XulyChiTiet() {
        val tensanpham = intent.extras?.getString("tensanpham")
        val giasanpham = intent.getStringExtra("giasanpham").toString()
        val hinhanhsanpham = intent.getStringExtra("hinhanhsanpham").toString()
        val motasanpham = intent.getStringExtra("motasanpham").toString()
        binding.txtChitietTensanpham.text = tensanpham
        binding.txtChitietGiasanpham.text = formatAmount(giasanpham)
        binding.txtChitietMotasanpham.text = motasanpham
        Glide.with(applicationContext).load(hinhanhsanpham).centerCrop()
            .into(binding.imvChitietHinhanh)

    }

    //chuyen sang dinh dang 000.000d
    private fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }
}