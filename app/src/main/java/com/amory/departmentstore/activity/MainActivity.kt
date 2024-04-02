package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.ItemOffsetDecoration
import com.amory.departmentstore.adapter.RvLoaiSanPham
import com.amory.departmentstore.adapter.RvSanPham
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityMainBinding
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.bumptech.glide.Glide
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SlideQuangCao()
        /*     showSanPham()*/
        if (Utils.kiemTraKetNoi(this)) {
            /* Toast.makeText(this, "Có internet", Toast.LENGTH_SHORT).show()*/
            laySanPham()
            layLoaiSanPham()
        } else {
            Toast.makeText(this, "Không internet", Toast.LENGTH_SHORT).show()

        }
    }

    private fun layLoaiSanPham() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getLoaisanPham()
        call.enqueue(object : Callback<LoaiSanPhamModel> {
            override fun onResponse(
                call: Call<LoaiSanPhamModel>,
                response: Response<LoaiSanPhamModel>
            ) {
                if (response.isSuccessful) {
                    val list = response.body()?.result
                    /*Toast.makeText(this@MainActivity, list, Toast.LENGTH_SHORT).show()*/
                    val adapter = list?.let { RvLoaiSanPham(it) }
                    binding.rvloaisanpham.adapter = adapter
                    binding.rvloaisanpham.layoutManager = LinearLayoutManager(
                        this@MainActivity,
                        RecyclerView.HORIZONTAL, false
                    )
                }
            }

            override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun laySanPham() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getData()
        call.enqueue(object : Callback<SanPhamModel> {
            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<SanPhamModel>,
                response: Response<SanPhamModel>
            ) {
                if (response.isSuccessful) {
                    val produce = response.body()?.result
                    /* Toast.makeText(this@MainActivity, produce, Toast.LENGTH_SHORT).show()*/
                    val adapter = produce?.let { RvSanPham(it) }
                    binding.rvSanpham.adapter = adapter
                    val itemDecoration = ItemOffsetDecoration(3)
                    binding.rvSanpham.addItemDecoration(itemDecoration)

                    binding.rvSanpham.layoutManager = GridLayoutManager(
                        this@MainActivity,
                        3
                    )

                }

            }
        })
    }
    //format gia 89.000d
    fun formatCurrency(number: Int): String {
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    private fun SlideQuangCao() {
        val listquangcao: MutableList<String> = mutableListOf()
        listquangcao.add("https://cdn.tgdd.vn/bachhoaxanh/banners/5599/thanh-ly-giam-soc-22032024894.jpg")
        listquangcao.add("https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_85,s_1058x135/https://cdn.tgdd.vn/bachhoaxanh/banners/5599/san-sale-gia-soc-cung-bhx-12032024133716.jpg")
        listquangcao.add("https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_85,s_1058x135/https://cdn.tgdd.vn/bachhoaxanh/banners/5599/hoa-my-pham-giam-soc-den-50-21032024183619.jpg")
        listquangcao.add("https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_85,s_1058x135/https://cdn.tgdd.vn/bachhoaxanh/banners/5599/sua-cac-loai-3012202311948.jpg")

        for (i in 0 until listquangcao.size) {
            val imageView = ImageView(applicationContext)
            Glide.with(applicationContext).load(listquangcao[i]).into(imageView)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            binding.viewFlipper.addView(imageView)
        }
        val slide_in = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_in_right)
        val slide_out = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_out_right)
        /*Chuyển đổi ảnh*/
        binding.viewFlipper.flipInterval = 3000
        binding.viewFlipper.isAutoStart = true
        binding.viewFlipper.inAnimation = slide_in
        binding.viewFlipper.outAnimation = slide_out


    }
}