package com.amory.departmentstore.activity


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.GestureDetector
import android.view.MotionEvent
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
import com.amory.departmentstore.model.OnCLickButtonSanPham
import com.amory.departmentstore.model.OnClickRvLoaiSanPham
import com.amory.departmentstore.model.OnClickRvSanPham
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
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: RvSanPham
    private lateinit var scrollListener: RvLoadMoreScroll
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private var soluongsanpham = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SlideQuangCao()

        /*  onTouch()*/

        /*  showSanPham()*/
        laySoLuongSanPhamActivityChiTiet()

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
                    val adapter = list?.let {
                        RvLoaiSanPham(it, object : OnClickRvLoaiSanPham {
                            override fun onClickLoaiSanPham(position: Int) {
                                /*Toast.makeText(
                                    this@MainActivity,
                                    "Bạn chọn " + list[position].tenloaisanpham,
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                when (list[position].loaisanpham) {
                                    1 -> {
                                        GoToSanPhamGao()
                                    }

                                    2 -> {
                                        GoToSanPhamSnack()
                                    }

                                    3 -> {
                                        GoToSanPhamTraiCay()
                                    }
                                }
                            }
                        })
                    }
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

    private fun GoToSanPhamTraiCay() {
        val intent = Intent(this, TraiCayActivity::class.java)
        startActivity(intent)
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

                            adapter =
                                RvSanPham(list as MutableList<SanPham>, object : OnClickRvSanPham {
                                    override fun onClickSanPham(position: Int) {
                                        /*Toast.makeText(
                                            applicationContext,
                                            "Mua $position", Toast.LENGTH_SHORT
                                        ).show()*/
                                        val intent = Intent(
                                            this@MainActivity,
                                            ChiTietSanPhamActivity::class.java
                                        )

                                        intent.putExtra(
                                            "tensanpham",
                                            list[position].tensanpham.toString()
                                        )
                                        intent.putExtra("giasanpham", list[position].giasanpham)
                                        intent.putExtra("hinhanhsanpham", list[position].hinhanh)
                                        intent.putExtra("motasanpham", list[position].mota)
                                        intent.putExtra("soluongsanphamgiohang", soluongsanpham)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)

                                    }
                                }, object : OnCLickButtonSanPham {
                                    override fun onCLickButtonSanPham(position: Int) {
                                        soluongsanpham += 1
                                        val soluongsanphamchitiet =
                                            intent.getIntExtra("soluongsanphamgiohang", 0)
                                        soluongsanpham += soluongsanphamchitiet
                                        if (soluongsanpham != 0) {
                                            binding.badgeCart.setText(soluongsanpham.toString())
                                        }
                                    }
                                })
                            adapter.notifyDataSetChanged()
                            binding.rvSanpham.adapter = adapter
                            /*Thêm khoảng cách giữa các item adapter*/
                            val itemDecoration = ItemOffsetDecoration(3)
                            binding.rvSanpham.addItemDecoration(itemDecoration)

                            setRVLayoutManager()
                            addEventLoad(produce, list)
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


    private fun laySoLuongSanPhamActivityChiTiet() {
        var soluongsanphamchitiet = intent.getIntExtra("soluongsanpham", 0)
        soluongsanphamchitiet += soluongsanpham
        if (soluongsanphamchitiet != 0) {
            binding.badgeCart.setText(soluongsanphamchitiet.toString())
        }
        /*Toast.makeText(this,soluongsanpham.toString(),Toast.LENGTH_SHORT).show()*/
    }

    private fun addEventLoad(produce: MutableList<SanPham>, list: MutableList<SanPham>) {
        scrollListener = RvLoadMoreScroll(mLayoutManager as GridLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                LoadMoreData(produce.toMutableList(), list)
            }
        })
        binding.rvSanpham.addOnScrollListener(scrollListener)
    }

    private fun GoToSanPhamGao() {
        val intent = Intent(this@MainActivity, GaoActivity::class.java)
        startActivity(intent)
    }

    private fun GoToSanPhamSnack() {
        val intent = Intent(this, SnackActivity::class.java)
        startActivity(intent)
    }

    /*Khi vuoót vào recyclerVIew sanpham*/
    @SuppressLint("ClickableViewAccessibility")
    fun onTouch() {
        binding.rvSanpham.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItemPosition == 0 && dy < 0) {

                    binding.txt.animate().translationY(0f).duration = 300
                    binding.viewFlipper.animate().translationY(0f).duration = 300
                    binding.rvloaisanpham.animate().translationY(0f).duration = 300

                } else {
                    binding.txt.animate().translationY(-binding.txt.height.toFloat()).duration = 300
                    binding.viewFlipper.animate()
                        .translationY(-binding.viewFlipper.height.toFloat()).duration = 600
                    binding.rvloaisanpham.animate()
                        .translationY(-binding.rvloaisanpham.height.toFloat()).duration = 900
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
        }, 1000)
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

    override fun onResume() {
        super.onResume()
        laySanPham()
    }


}