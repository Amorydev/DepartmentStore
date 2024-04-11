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
    private lateinit var tensanpham: String
    private lateinit var giasanpham: String
    private lateinit var hinhanhsanpham: String
    private lateinit var motasanpham: String

    private var soluongsanphamgiohang: Int = 0

    private var soluongsanpham = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        onClickBack()
        XulyChiTiet()
        onCLickCongTruSanPham()
        ThemVaoGioHang()
    }

    private fun putData() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("soluongsanpham", soluongsanphamgiohang)
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

    private fun ThemVaoGioHang() {
        binding.btnThemvaogiohang.setOnClickListener {
            soluongsanphamgiohang += soluongsanpham
            if (soluongsanpham != 0) {
                binding.badgeCart.setText(soluongsanphamgiohang.toString())
            }
        }
    }

    private fun init() {
        tensanpham = intent.getStringExtra("tensanpham").toString()
        giasanpham = intent.getStringExtra("giasanpham").toString()
        hinhanhsanpham = intent.getStringExtra("hinhanhsanpham").toString()
        motasanpham = intent.getStringExtra("motasanpham").toString()
        soluongsanphamgiohang = intent.getIntExtra("soluongsanphamgiohang", 0)
    }

    private fun XulyChiTiet() {
        binding.txtChitietTensanpham.text = tensanpham
        binding.txtChitietGiasanpham.text = formatAmount(giasanpham)
        binding.txtChitietMotasanpham.text = motasanpham
        Glide.with(applicationContext).load(hinhanhsanpham).centerCrop()
            .into(binding.imvChitietHinhanh)
        binding.badgeCart.setText(soluongsanphamgiohang.toString())
    }

    //chuyen sang dinh dang 000.000d
    private fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }
}