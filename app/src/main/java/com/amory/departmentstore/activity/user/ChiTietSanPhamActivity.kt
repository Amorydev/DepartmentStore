package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityChiTietSanPhamBinding
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.User
import com.bumptech.glide.Glide
import com.google.android.play.integrity.internal.i
import com.google.firebase.logger.Logger
import com.google.rpc.context.AttributeContext.Resource
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
        xuLyChiTiet()
        onCLickCongTruSanPham()
        ThemVaoGioHang()
        goToGioHang()
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

    private fun goToGioHang() {
        binding.imgGiohangchitiet.setOnClickListener {
            val intent = Intent(this, GioHangActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun onCLickCongTruSanPham() {
        binding.txtCongSanpham.setOnClickListener {
            soluongsanpham += 1
            capNhatSoLuongSanPham()

        }
        binding.txtTruSanpham.setOnClickListener {
            if (soluongsanpham > 0) {
                soluongsanpham -= 1
            }
            capNhatSoLuongSanPham()
        }
    }

    private fun capNhatSoLuongSanPham() {
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
                            val tongGiaTriSanPham =
                                giasanpham * Utils.manggiohang[i].soluongsanphamgiohang
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

    private fun xuLyChiTiet() {
        binding.txtChitietTensanpham.text = tensanpham
        binding.txtChitietGiasanpham.text = formatAmount(giasanpham)
        Glide.with(applicationContext).load(hinhanhsanpham).into(binding.imvChitietHinhanh)
        renderDataToTable()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun renderDataToTable() {
        val description = motasanpham.trimIndent()
        Log.d("description", "renderDataToTable: $description")
        val productPair = mutableListOf<Pair<String, String>>()
        val additionalText = mutableListOf<String>()
        if (description.isEmpty()) {
            return
        }
        if (description.contains(":")) {
            val lines = description.lines()
            Log.d("description", "renderDataToTable (:): $lines")
            var currentKey: String? = null
            val currentValue = StringBuilder()

            for (line in lines) {
                val trimmedLine = line.trim()
                if (trimmedLine.isEmpty()) continue

                if (':' in trimmedLine) {
                    if (currentKey != null) {
                        productPair.add(Pair(currentKey, currentValue.toString().trim()))
                        currentValue.setLength(0) // Reset current value
                    }

                    val parts = trimmedLine.split(':', limit = 2)
                    if (parts.size == 2) {
                        currentKey = parts[0].trim()
                        currentValue.append(parts[1].trim())
                    }
                } else {
                    if (currentKey != null) {
                        if (currentValue.isNotEmpty()) {
                            currentValue.append("\n")
                        }
                        currentValue.append(trimmedLine)
                    } else {
                        additionalText.add(trimmedLine)
                    }
                }
            }

            if (currentKey != null) {
                productPair.add(Pair(currentKey, currentValue.toString().trim()))
            }
        } else {
            val lines = description.lines()
            Log.d("description", "renderDataToTable (): $lines")

            if (lines[0].length > 50) {
                additionalText.add(lines[0])
                for (i in 1 until lines.size step 2) {
                    if (i + 1 < lines.size) {
                        productPair.add(Pair(lines[i], lines[i + 1]))
                    }
                }
            } else {
                for (i in lines.indices step 2) {
                    if (i + 1 < lines.size) {
                        productPair.add(Pair(lines[i], lines[i + 1]))
                    }
                }
            }

        }

        for ((key, value) in productPair) {
            val tableRow = TableRow(this@ChiTietSanPhamActivity).apply {
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val keyTxt = TextView(this).apply {
                text = key
                setPadding(12, 8, 8, 8)
                background = getDrawable(R.drawable.boder_background_table)
                layoutParams =
                    TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f).apply {
                        bottomMargin = 2
                        topMargin = 2
                        textSize = 15f

                    }
                setTextColor(resources.getColor(R.color.black))
                typeface = ResourcesCompat.getFont(context, R.font.asap)
            }

            val valueTxt = TextView(this).apply {
                text = value
                setPadding(12, 8, 8, 8)
                background = getDrawable(R.drawable.cell_border)
                layoutParams =
                    TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f).apply {
                        bottomMargin = 2
                        topMargin = 2
                        rightMargin = 2
                        textSize = 15f
                    }
                setTextColor(resources.getColor(R.color.black))
                typeface = ResourcesCompat.getFont(context, R.font.asap)
            }

            tableRow.apply {
                addView(keyTxt)
                addView(valueTxt)
                background = getDrawable(R.drawable.boder_background_table)
            }
            binding.tableLayout.addView(tableRow)
        }

        // Hiển thị thuộc tính bổ sung bên dưới bảng
        if (additionalText.isNotEmpty()) {
            val extraTextView = findViewById<TextView>(R.id.longTextView)
            extraTextView.text = additionalText.joinToString("\n")
            extraTextView.visibility = View.VISIBLE
        }
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