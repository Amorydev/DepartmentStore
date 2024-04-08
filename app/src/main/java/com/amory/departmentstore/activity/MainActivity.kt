package com.amory.departmentstore.activity


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView

import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.ItemOffsetDecoration
import com.amory.departmentstore.adapter.RvLoadMoreScroll
import com.amory.departmentstore.adapter.RvLoaiSanPham
import com.amory.departmentstore.adapter.RvSanPham
import com.amory.departmentstore.adapter.Utils
import com.amory.departmentstore.databinding.ActivityMainBinding
import com.amory.departmentstore.model.Constant.VIEW_TYPE_ITEM
import com.amory.departmentstore.model.Constant.VIEW_TYPE_LOADING
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.OnLoadMoreListener
import com.amory.departmentstore.model.SanPham
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
    lateinit var adapter: RvSanPham
    lateinit var scrollListener: RvLoadMoreScroll
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SlideQuangCao()

        /*  showSanPham()*/

        if (Utils.kiemTraKetNoi(this)) {
            /* Toast.makeText(this, "Có internet", Toast.LENGTH_SHORT).show()*/
            laySanPham()
            layLoaiSanPham()
        } else {
            Toast.makeText(this, "Vui lòng kết nối internet", Toast.LENGTH_SHORT).show()

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
                Toast.makeText(this@MainActivity, "Lấy loại sản phẩm thất bại", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun laySanPham() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getData()
        call.enqueue(object : Callback<SanPhamModel> {
            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@MainActivity, "Lấy sản phẩm thất bại", Toast.LENGTH_SHORT)
                    .show()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SanPhamModel>,
                response: Response<SanPhamModel>
            ) {
                if (response.isSuccessful) {
                    val produce = response.body()?.result
                    /*
                      Toast.makeText(this@MainActivity, produce, Toast.LENGTH_SHORT).show()
                    */

                    if (!produce.isNullOrEmpty()) {
                        val randomPhanTu = produce.shuffled()
                        val list = randomPhanTu.take(12)

                        if (list.isNotEmpty()) {

                            adapter = RvSanPham(list as MutableList<SanPham>)
                            adapter.notifyDataSetChanged()
                            binding.rvSanpham.adapter = adapter

                            val itemDecoration = ItemOffsetDecoration(3)
                            binding.rvSanpham.addItemDecoration(itemDecoration)

                            setRVLayoutManager()

                            scrollListener = RvLoadMoreScroll(mLayoutManager as GridLayoutManager)
                            scrollListener.setOnLoadMoreListener(object :
                                OnLoadMoreListener {
                                override fun onLoadMore() {
                                    LoadMoreData(produce.toMutableList(), list)
                                }
                            })
                            binding.rvSanpham.addOnScrollListener(scrollListener)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Không có sản phẩm để hiển thị",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Danh sách sản phẩm trống",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            }
        })
    }

    private fun setRVLayoutManager() {
        mLayoutManager = GridLayoutManager(this, 3)
        binding.rvSanpham.layoutManager = mLayoutManager
        binding.rvSanpham.setHasFixedSize(true)
        binding.rvSanpham.adapter = adapter
        (mLayoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        VIEW_TYPE_ITEM -> 1
                        VIEW_TYPE_LOADING -> 3
                        else -> -1
                    }
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun LoadMoreData(produce: MutableList<SanPham>, current: MutableList<SanPham>) {

        adapter.addLoadingView()
        Handler().postDelayed({
            adapter.removeLoadingView()
            /*Thực hiện lọc kiểm tra phần tử trước đó đã hiển thị hay chưa*/
            val remainingItems = produce.filter {
                /*san pham đã hiênr thị không còn chưa trong produce*/
                !current.contains(it)
            }
            val newlist = remainingItems.take(12)

            current.addAll(newlist)
            adapter.addData(newlist)

            scrollListener.setLoaded()
            binding.rvSanpham.post {
                adapter.notifyDataSetChanged()
            }
        }, 3000)
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
        /*
                Chuyển đổi ảnh
        */
        binding.viewFlipper.flipInterval = 3000
        binding.viewFlipper.isAutoStart = true
        binding.viewFlipper.inAnimation = slide_in
        binding.viewFlipper.outAnimation = slide_out

    }


}