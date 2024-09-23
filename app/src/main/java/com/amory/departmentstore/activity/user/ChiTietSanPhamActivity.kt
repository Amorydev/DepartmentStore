package com.amory.departmentstore.activity.user

import com.amory.departmentstore.adapter.RvShowImagesFromProduct
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickItemInRvImages
import com.amory.departmentstore.R
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.adapter.RvShowDetailProductImages
import com.amory.departmentstore.databinding.ActivityChiTietSanPhamBinding
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.Product
import com.amory.departmentstore.model.User
import com.amory.departmentstore.viewModel.DescriptionProductViewModel
import io.paperdb.Paper
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

class ChiTietSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChiTietSanPhamBinding
    private var idProduct: Int = 0
    private lateinit var productNow: Product

    private val descriptionViewModel: DescriptionProductViewModel by viewModels<DescriptionProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChiTietSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        descriptionViewModel.fetchDataProduct(idProduct)
        synchronizeRv()
        onClickListener()
        setUpObserver()
    }

    private fun setUpObserver() {
        descriptionViewModel.product.observe(this) { product ->
            if (product != null) {
                binding.txtChitietTensanpham.text = product.name
                binding.txtChitietGiasanpham.text = formatAmount(product.price)
                descriptionViewModel.handleDataToTable(product.description)
                productNow = product
            }
        }
        descriptionViewModel.productImages.observe(this) { productImages ->
            if (productImages != null) {
                renderDescriptionImages(
                    listOf(
                        productImages.imageUrl1,
                        productImages.imageUrl2,
                        productImages.imageUrl3
                    )
                )
            }
        }
        descriptionViewModel.productPairs.observe(this) { productPairs ->
            renderDataToTable(productPairs)
        }
        descriptionViewModel.additionalText.observe(this) { additionalText ->
            if (additionalText!!.isNotEmpty()) {
                binding.longTextView.text = additionalText.joinToString("\n")
                binding.longTextView.visibility = View.VISIBLE
            }
        }

        descriptionViewModel.qualityProduct.observe(this) { qualityProduct ->
            binding.soluongsanpham.text = qualityProduct.toString()
        }

        descriptionViewModel.cartItems.observe(this) { cartItems ->
            val quality = cartItems.sumOf { it.soluongsanphamgiohang }
            binding.badgeCart.setNumber(quality)
        }

    }

    private fun onClickListener() {
        binding.imvBack.setOnClickListener { this.onBackPressedDispatcher.onBackPressed() }
        binding.txtCongSanpham.setOnClickListener { descriptionViewModel.plusProduct() }
        binding.txtTruSanpham.setOnClickListener { descriptionViewModel.minusProduct() }
        binding.btnMuangay.setOnClickListener { buyNow() }
        binding.imgGiohangchitiet.setOnClickListener { goToCart() }
        binding.layoutThemvaogiohang.setOnClickListener { addToCarts() }
    }

    private fun buyNow() {
        val user = Paper.book().read<User>("user")
        if (user != null && Utils.user_current != null) {
            descriptionViewModel.buyNowProduct(productNow)
            goToPay()
        } else {
            goToLogin()
        }
    }

    private fun goToPay(){
        val intent = Intent(this, ThanhToanActivity::class.java)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, DangNhapActivity::class.java)
        startActivity(intent)
    }

    private fun goToCart() {
        val intent = Intent(this, GioHangActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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

    private fun addToCarts() {
        val user = Paper.book().read<User>("user")
        if (user != null && Utils.user_current != null) {
            descriptionViewModel.addProductToCart(productNow)
        } else {
            goToLogin()
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
        idProduct = intent.getIntExtra("idsanpham", 0)
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong())
        } else {
            binding.badgeCart.setNumber(0)
        }
    }

    private fun renderDescriptionImages(listImages: List<String>) {
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun renderDataToTable(productPair: List<Pair<String, String>>) {
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
                typeface = ResourcesCompat.getFont(context, R.font.roboto)
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
                typeface = ResourcesCompat.getFont(context, R.font.roboto)
            }

            tableRow.apply {
                addView(keyTxt)
                addView(valueTxt)
                background = getDrawable(R.drawable.boder_background_table)
            }
            binding.tableLayout.addView(tableRow)
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