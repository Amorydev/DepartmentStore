package com.amory.departmentstore.activity.user

import com.amory.departmentstore.adapter.RvShowImagesFromProduct
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickItemInRvImages
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.adapter.RvShowDetailProductImages
import com.amory.departmentstore.databinding.ActivityChiTietSanPhamBinding
import com.amory.departmentstore.manager.DetailProductImages
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.User
import io.paperdb.Paper
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class ChiTietSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChiTietSanPhamBinding
    private lateinit var nameProduct: String
    private var priceProduct: Double = 0.0
    private lateinit var imageProduct: String
    private lateinit var descriptionProduct: String
    private var idProduct: Int = 0
    private var qualityProduct = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        onClickBack()
        handleDescription()
        onClickPlusOrMinus()
        addToCarts()
        goToCart()
        onClickBuyNow()
        synchronizeRv()
    }


    private fun showDetailImages(id: Int, onResult: (List<String>) -> Unit) {
        val listImages = mutableListOf<String>()
        DetailProductImages.getDetailProductImages(id, { productImages ->

            for (i in productImages[0].productImages.indices) {
                listImages.add(productImages[0].productImages[i].imageUrl1)
                listImages.add(productImages[0].productImages[i].imageUrl2)
                listImages.add(productImages[0].productImages[i].imageUrl3)
            }
            Log.d("productImages", "showDetailImages: $listImages")
            onResult(listImages)
        }, { error ->
            Log.d("error", "showDetailImages: $error")
            onResult(emptyList())
        })
    }


    private fun onClickBuyNow() {
        binding.btnMuangay.setOnClickListener {
            val user = Paper.book().read<User>("user")
            var soluong = 0
            var tongGiaTriSanPham = 0.0
            if (user != null && Utils.user_current != null) {
                soluong = qualityProduct
                tongGiaTriSanPham = priceProduct.toDouble() * soluong
                val gioHang = GioHang(
                    idsanphamgiohang = idProduct,
                    tensanphamgiohang = nameProduct,
                    giasanphamgiohang = tongGiaTriSanPham,
                    hinhanhsanphamgiohang = imageProduct,
                    soluongsanphamgiohang = soluong
                )
                Utils.mangmuahang.add(gioHang)
                val intent = Intent(this, ThanhToanActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, DangNhapActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun goToCart() {
        binding.imgGiohangchitiet.setOnClickListener {
            val intent = Intent(this, GioHangActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun onClickPlusOrMinus() {
        binding.txtCongSanpham.setOnClickListener {
            qualityProduct += 1
            updateQuanlityProduct()

        }
        binding.txtTruSanpham.setOnClickListener {
            if (qualityProduct > 0) {
                qualityProduct -= 1
            }
            updateQuanlityProduct()
        }
    }

    private fun updateQuanlityProduct() {
        binding.soluongsanpham.text = qualityProduct.toString()
    }
    private fun synchronizeRv() {
        binding.rvImagesProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val selectedPosition = layoutManager.findFirstVisibleItemPosition()

                val rvImagesAdapter = binding.rvImages.adapter as RvShowImagesFromProduct
                rvImagesAdapter.updateSelectedPosition(selectedPosition)

                binding.rvImages.scrollToPosition(selectedPosition)
            }
        })
    }


    private fun onClickBack() {
        binding.imvBack.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun addToCarts() {

        binding.layoutThemvaogiohang.setOnClickListener {
            val user = Paper.book().read<User>("user")
            if (user != null && Utils.user_current != null) {
                if (Utils.manggiohang.size > 0) {
                    val quality = qualityProduct
                    var flags = false
                    for (i in 0 until Utils.manggiohang.size) {
                        if (Utils.manggiohang[i].idsanphamgiohang == idProduct) {
                            Utils.manggiohang[i].soluongsanphamgiohang += quality
                            val totalMoneyProduct =
                                priceProduct * Utils.manggiohang[i].soluongsanphamgiohang
                            Utils.manggiohang[i].giasanphamgiohang = totalMoneyProduct
                            flags = true
                            break
                        }
                    }
                    if (!flags) {
                        val tongGiaTriSanPham = priceProduct * quality
                        val gioHang = GioHang(
                            idsanphamgiohang = idProduct,
                            tensanphamgiohang = nameProduct,
                            giasanphamgiohang = tongGiaTriSanPham,
                            hinhanhsanphamgiohang = imageProduct,
                            soluongsanphamgiohang = quality
                        )
                        Utils.manggiohang.add(gioHang)
                    }
                } else {
                    val soluong = qualityProduct
                    val tongGiaTriSanPham = priceProduct * soluong
                    val gioHang = GioHang(
                        idsanphamgiohang = idProduct,
                        tensanphamgiohang = nameProduct,
                        giasanphamgiohang = tongGiaTriSanPham,
                        hinhanhsanphamgiohang = imageProduct,
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
        nameProduct = intent.getStringExtra("name").toString()
        priceProduct = intent.getDoubleExtra("price", 0.0)
        imageProduct = intent.getStringExtra("hinhanhsanpham").toString()
        descriptionProduct = intent.getStringExtra("motasanpham").toString()
        idProduct = intent.getIntExtra("idsanpham", 0)
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong())
        } else {
            binding.badgeCart.setNumber(0)
        }
        showDetailImages(idProduct) { listImages ->
            Log.d("listImages", "init: $listImages")
            if (listImages.isNotEmpty()) {
                val adapter = RvShowImagesFromProduct(listImages, object : OnClickItemInRvImages {
                    override fun onClick(position: Int) {
                        binding.rvImagesProducts.scrollToPosition(position)
                    }
                })
                binding.rvImages.adapter = adapter
                binding.rvImages.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                renderProductImages(listImages)
            }
        }
    }

    private fun renderProductImages(listImages: List<String>) {
        val adapter = RvShowDetailProductImages(listImages, object : OnClickItemInRvImages {
            override fun onClick(position: Int) {
            }
        })

        with(binding.rvImagesProducts) {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            LinearSnapHelper().attachToRecyclerView(this)

            onFlingListener = object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                    return abs(velocityX) <= abs(velocityY)
                }
            }
        }
    }

    private fun handleDescription() {
        binding.txtChitietTensanpham.text = nameProduct
        binding.txtChitietGiasanpham.text = formatAmount(priceProduct)
        renderDataToTable()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun renderDataToTable() {
        val description = descriptionProduct.trimIndent()
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
}