package com.amory.departmentstore.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
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
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.OnCLickButtonSanPham
import com.amory.departmentstore.model.OnClickRvLoaiSanPham
import com.amory.departmentstore.model.OnClickRvSanPham
import com.amory.departmentstore.model.OnLoadMoreListener
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import io.paperdb.Paper
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: RvSanPham
    private lateinit var scrollListener: RvLoadMoreScroll
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private var isLoadMore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SlideQuangCao()
        onClickDanhMuc()
        onCLickNav()
        OnclickNavHeader()
        onClickSearch()
        Paper.init(this)

        /*  onTouch()*/
        /*  showSanPham()*/
        /*Toast.makeText(this,Utils.user_current?.email,Toast.LENGTH_SHORT).show()*/

        goToGioHang()
        if (Utils.kiemTraKetNoi(this)) {
            /* Toast.makeText(this, "Có internet", Toast.LENGTH_SHORT).show()*/
            paddingRecy()
            laySanPham()
            layLoaiSanPham()

        } else {
            Toast.makeText(this, "Vui lòng kết nối internet", Toast.LENGTH_SHORT).show()
        }

    }

    private fun onClickSearch() {
        binding.imbSearch.setOnClickListener {
            val intent = Intent(this,SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.edtSearch.setOnClickListener {
            val intent = Intent(this,SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun paddingRecy() {
        val itemDecoration = ItemOffsetDecoration(1)
        binding.rvSanpham.addItemDecoration(itemDecoration)
    }


    private fun OnclickNavHeader() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val btnSignIn: Button = headerView.findViewById(R.id.btnSignIn)
        val btnLogin: Button = headerView.findViewById(R.id.btnLogin)
        btnSignIn.setOnClickListener {
            val intent = Intent(this, DangKiActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
        }
        if (Paper.book().read<String>("user") != null) {
            btnLogin.visibility = View.INVISIBLE
            btnSignIn.visibility = View.INVISIBLE
            val txt_nav = headerView.findViewById<TextView>(R.id.txt_email_nav)
            txt_nav.text = Paper.book().read<String>("email")
        } else {
            btnLogin.visibility = View.VISIBLE
            btnSignIn.visibility = View.VISIBLE
            val txt_nav = headerView.findViewById<TextView>(R.id.txt_email_nav)
            txt_nav.text = Utils.user_current?.email
        }

    }


    private fun onCLickNav() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btnSignIn -> {
                    val intent = Intent(this, DangKiActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.cart -> {
                    val intent = Intent(this, GioHangActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.details_order ->{
                    val intent = Intent(this, ChiTietDatHangActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    Paper.book().delete("user")
                    val intent = Intent(this, DangNhapActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

    private fun onClickDanhMuc() {
        binding.imbTrangchu.setOnClickListener {
            binding.layoutDrawer.openDrawer(binding.navView)
        }
    }


    private fun goToGioHang() {
        binding.imvGiohang.setOnClickListener {
            val intent = Intent(this, GioHangActivity::class.java)
            startActivity(intent)
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
                                when (list[position].category_id) {
                                    1 -> {
                                        /*
                                                                                GoToSanPhamGao()
                                        */
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].category_id)
                                        intent.putExtra("tenloaisanpham", list[position].name)
                                        startActivity(intent)
                                    }

                                    2 -> {
                                        /* GoToSanPhamSnack()*/
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].category_id)
                                        intent.putExtra("tenloaisanpham", list[position].name)

                                        startActivity(intent)
                                    }

                                    3 -> {
                                        /*   GoToSanPhamTraiCay()*/
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].category_id)
                                        intent.putExtra("tenloaisanpham", list[position].name)

                                        startActivity(intent)
                                    }

                                    4 -> {
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].category_id)
                                        intent.putExtra("tenloaisanpham", list[position].name)
                                        startActivity(intent)
                                    }

                                    5 -> {
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].category_id)
                                        intent.putExtra("tenloaisanpham", list[position].name)
                                        startActivity(intent)
                                    }

                                    6 -> {
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].category_id)
                                        intent.putExtra("tenloaisanpham", list[position].name)
                                        startActivity(intent)
                                    }

                                    7 -> {
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].category_id)
                                        intent.putExtra("tenloaisanpham", list[position].name)
                                        startActivity(intent)
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


    private fun laySanPham() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getData()
        call.enqueue(object : Callback<SanPhamModel> {
            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
                Log.e("Amory", "Error occurred: ${t.message}", t)
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

                        val list = produce.shuffled().take(12)

                        if (list.isNotEmpty()) {

                            adapter =
                                RvSanPham(list as MutableList<SanPham>, object : OnClickRvSanPham {
                                    override fun onClickSanPham(position: Int) {
                                        /*Toast.makeText(
                                            applicationContext,
                                            "Mua " + list[position].category_id, Toast.LENGTH_SHORT
                                        ).show()*/
                                        val intent = Intent(
                                            this@MainActivity,
                                            ChiTietSanPhamActivity::class.java
                                        )

                                        intent.putExtra(
                                            "name",
                                            list[position].name
                                        )
                                        intent.putExtra("idsanpham", list[position].id)
                                        intent.putExtra("price", list[position].price)
                                        intent.putExtra("hinhanhsanpham", list[position].image_url)
                                        intent.putExtra("motasanpham", list[position].description)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)

                                    }
                                }, object : OnCLickButtonSanPham {
                                    override fun onCLickButtonSanPham(position: Int) {
                                        val soluong = 1
                                        var flags = false

                                        if (Utils.manggiohang.size > 0) {
                                            for (i in 0 until Utils.manggiohang.size) {
                                                if (Utils.manggiohang[i].tensanphamgiohang == list[position].name) {
                                                    flags = true
                                                    Utils.manggiohang[i].soluongsanphamgiohang += soluong
                                                    val tongGiaTriSanPham =
                                                        list[position].price.toLong() * Utils.manggiohang[i].soluongsanphamgiohang
                                                    Utils.manggiohang[i].giasanphamgiohang =
                                                        tongGiaTriSanPham.toString()
                                                    break
                                                }
                                            }
                                        }

                                        if (!flags) {
                                            val tongGiaTriSanPham =
                                                list[position].price.toLong() * soluong
                                            val gioHang = GioHang(
                                                idsanphamgiohang = list[position].id,
                                                tensanphamgiohang = list[position].name,
                                                giasanphamgiohang = tongGiaTriSanPham.toString(),
                                                hinhanhsanphamgiohang = list[position].image_url,
                                                soluongsanphamgiohang = soluong
                                            )
                                            Utils.manggiohang.add(gioHang)
                                        }

                                        binding.badgeCart.setText(
                                            Utils.manggiohang.getSoluong().toString()
                                        )
                                    }
                                })
                            adapter.notifyDataSetChanged()
                            binding.rvSanpham.adapter = adapter
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
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setText(Utils.manggiohang.getSoluong().toString())
        }
    }

    private fun MutableList<GioHang>.getSoluong(): Int {
        var totalSoluong = 0
        for (gioHang in Utils.manggiohang) {
            totalSoluong += gioHang.soluongsanphamgiohang
        }
        return totalSoluong
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
        layLoaiSanPham()
    }

    override fun onRestart() {
        super.onRestart()
        laySanPham()
        layLoaiSanPham()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isLoadMore) {
            isLoadMore = false
            laySanPham()
        } else {
            super.onBackPressed()
        }
    }


}