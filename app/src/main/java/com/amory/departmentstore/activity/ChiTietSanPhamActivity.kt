package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityChiTietSanPhamBinding
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.User
import com.bumptech.glide.Glide
import io.paperdb.Paper
import java.text.NumberFormat
import java.util.Locale

class ChiTietSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChiTietSanPhamBinding
    private lateinit var tensanpham: String
    private var giasanpham: Double = 0.0
    private lateinit var hinhanhsanpham: String
    private lateinit var motasanpham: String
    private var idsanpham: Int = 0
    private var soluongsanpham = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        onClickBack()
        XulyChiTiet()
        onCLickCongTruSanPham()
        ThemVaoGioHang()
        GoToGioHang()
        onCLickMuaNgay()
        /*Toast.makeText(this,idsanpham.toString(),Toast.LENGTH_SHORT).show()*/
    }

    private fun onCLickMuaNgay() {
        binding.btnMuangay.setOnClickListener {
            val user = Paper.book().read<User>("user")
            var soluong = 0
            var tongGiaTriSanPham = 0.0
            if (user != null && Utils.user_current != null) {
                soluong = soluongsanpham
                tongGiaTriSanPham = giasanpham.toDouble() * soluong
                val gioHang = GioHang(
                    idsanphamgiohang = idsanpham,
                    tensanphamgiohang = tensanpham,
                    giasanphamgiohang = tongGiaTriSanPham,
                    hinhanhsanphamgiohang = hinhanhsanpham,
                    soluongsanphamgiohang = soluong
                )
                Utils.mangmuahang.add(gioHang)
                val intent = Intent(this, ThanhToanActivity::class.java)
                startActivity(intent)
               /* overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)*/
            } else {
                val intent = Intent(this, DangNhapActivity::class.java)
                /*intent.putExtra("fromActivity",true)
                intent.putExtra("fromActivity_id",idsanpham)
                intent.putExtra("fromActivity_name",tensanpham)
                intent.putExtra("fromActivity_price",giasanpham)
                intent.putExtra("fromActivity_imageUrl",hinhanhsanpham)
                intent.putExtra("fromActivity_description",motasanpham)*/
                startActivity(intent)
            }
        }
    }

    private fun GoToGioHang() {
        binding.imgGiohangchitiet.setOnClickListener {
            val intent = Intent(this, GioHangActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

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
            if (soluongsanpham > 0) {
                soluongsanpham -= 1
            }
            CapNhatSoLuongSanPham()
        }
    }

    private fun CapNhatSoLuongSanPham() {
        binding.soluongsanpham.text = soluongsanpham.toString()
    }

    private fun onClickBack() {
        binding.imvBack.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun ThemVaoGioHang() {

        binding.layoutThemvaogiohang.setOnClickListener {
            val user = Paper.book().read<User>("user")
            if (user != null && Utils.user_current != null) {
                if (Utils.manggiohang.size > 0) {
                    val soluong = soluongsanpham
                    var flags = false
                    for (i in 0 until Utils.manggiohang.size) {
                        if (Utils.manggiohang[i].idsanphamgiohang == idsanpham) {
                            Utils.manggiohang[i].soluongsanphamgiohang += soluong
                            val tongGiaTriSanPham = giasanpham * Utils.manggiohang[i].soluongsanphamgiohang
                            Utils.manggiohang[i].giasanphamgiohang = tongGiaTriSanPham
                            flags = true
                            break
                        }
                    }
                    if (!flags) {
                        val tongGiaTriSanPham = giasanpham * soluong
                        val gioHang = GioHang(
                            idsanphamgiohang = idsanpham,
                            tensanphamgiohang = tensanpham,
                            giasanphamgiohang = tongGiaTriSanPham,
                            hinhanhsanphamgiohang = hinhanhsanpham,
                            soluongsanphamgiohang = soluong
                        )
                        Utils.manggiohang.add(gioHang)
                    }
                } else {
                    val soluong = soluongsanpham
                    val tongGiaTriSanPham = giasanpham * soluong
                    val gioHang = GioHang(
                        idsanphamgiohang = idsanpham,
                        tensanphamgiohang = tensanpham,
                        giasanphamgiohang = tongGiaTriSanPham,
                        hinhanhsanphamgiohang = hinhanhsanpham,
                        soluongsanphamgiohang = soluong
                    )
                    Utils.manggiohang.add(gioHang)

                }
                if (Utils.manggiohang.getSoluong() != 0) {
                    binding.badgeCart.setNumber(Utils.manggiohang.getSoluong())
                }
            } else {
                val intent = Intent(this, DangNhapActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun MutableList<GioHang>.getSoluong(): Int {
        var totalSoluong = 0
        for (gioHang in this) {
            totalSoluong += gioHang.soluongsanphamgiohang
        }
        return totalSoluong
    }

    private fun init() {
        Paper.init(this)
        tensanpham = intent.getStringExtra("name").toString()
        giasanpham = intent.getDoubleExtra("price", 0.0)
        hinhanhsanpham = intent.getStringExtra("hinhanhsanpham").toString()
        motasanpham = intent.getStringExtra("motasanpham").toString()
        idsanpham = intent.getIntExtra("idsanpham", 0)
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong())
        } else {
            binding.badgeCart.setNumber(0)
        }
    }

    private fun XulyChiTiet() {
        binding.txtChitietTensanpham.text = tensanpham
        binding.txtChitietGiasanpham.text = formatAmount(giasanpham)
        binding.txtChitietMotasanpham.text = motasanpham
        Glide.with(applicationContext).load(hinhanhsanpham).into(binding.imvChitietHinhanh)
    }

    //chuyen sang dinh dang 000.000d
    private fun formatAmount(amount: Double): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    override fun onResume() {
        super.onResume()
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong())
        } else {
            binding.badgeCart.setNumber(0)
        }
    }

   /* override fun finish() {
        super.finish()
        this.overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        *//*this.overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right)*//*
    }*/
}