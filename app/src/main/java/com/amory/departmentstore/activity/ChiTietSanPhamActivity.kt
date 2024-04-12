package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityChiTietSanPhamBinding
import com.amory.departmentstore.model.GioHang
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
/*
    private fun putData() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("soluongsanpham", soluongsanphamgiohang)
        startActivity(intent)
    }*/

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
        }
    }

    private fun ThemVaoGioHang() {
        binding.btnThemvaogiohang.setOnClickListener {
            if (Utils.manggiohang.size > 0) {
                val soluong = soluongsanpham
                var flags:Boolean = false
                for (i in 0 until Utils.manggiohang.size){
                    if (Utils.manggiohang[i].tensanphamgiohang == tensanpham){
                        Utils.manggiohang[i].soluongsanphamgiohang += soluong
                        val tongGiaTriSanPham = giasanpham.toLong() * Utils.manggiohang[i].soluongsanphamgiohang
                        Utils.manggiohang[i].giasanphamgiohang = tongGiaTriSanPham.toString()
                        flags = true
                        break
                    }
                }
                if (!flags){
                    val tongGiaTriSanPham = giasanpham.toLong() * soluong
                    val gioHang = GioHang(
                        tensanphamgiohang = tensanpham,
                        giasanphamgiohang = tongGiaTriSanPham.toString(),
                        hinhanhsanphamgiohang = hinhanhsanpham,
                        soluongsanphamgiohang = soluong
                    )
                    Utils.manggiohang.add(gioHang)
                }
            } else {
                val soluong = soluongsanpham
                val tongGiaTriSanPham = giasanpham.toLong() * soluong
                val gioHang = GioHang(
                    tensanphamgiohang = tensanpham,
                    giasanphamgiohang = tongGiaTriSanPham.toString(),
                    hinhanhsanphamgiohang = hinhanhsanpham,
                    soluongsanphamgiohang = soluong
                )
                Utils.manggiohang.add(gioHang)

            }
            binding.badgeCart.setText(Utils.manggiohang.getSoluong().toString())
        }
    }
    fun MutableList<GioHang>.getSoluong(): Int {
        var totalSoluong = 0
        for (gioHang in this) {
            totalSoluong += gioHang.soluongsanphamgiohang
        }
        return totalSoluong
    }
    private fun init() {
        tensanpham = intent.getStringExtra("tensanpham").toString()
        giasanpham = intent.getStringExtra("giasanpham").toString()
        hinhanhsanpham = intent.getStringExtra("hinhanhsanpham").toString()
        motasanpham = intent.getStringExtra("motasanpham").toString()
        binding.badgeCart.setText(Utils.manggiohang.getSoluong().toString())
    }

    private fun XulyChiTiet() {
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