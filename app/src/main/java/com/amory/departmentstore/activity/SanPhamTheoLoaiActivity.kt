package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.ItemOffsetDecoration
import com.amory.departmentstore.adapter.RvLoadMoreScroll
import com.amory.departmentstore.adapter.RvSanPhamCacLoai
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivitySanPhamTheoLoaiBinding
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.viewModel.OnCLickButtonSanPham

import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import com.amory.departmentstore.viewModel.OnClickSanPhamTheoLoai
import com.amory.departmentstore.viewModel.OnLoadMoreListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Response

class SanPhamTheoLoaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySanPhamTheoLoaiBinding
    lateinit var adapter: RvSanPhamCacLoai
    private lateinit var scrollListener: RvLoadMoreScroll
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySanPhamTheoLoaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        laySanPhamGao()
        quayLaiTrangChu()
        goToGioHang()
        setGiaoDien()
        onClickDanhMuc()
        onCLickNav()
        OnclickNavHeader()
        onClickSearch()
    }
    private fun OnclickNavHeader() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)

        if (Paper.book().read<String>("user") != null) {
            val txt_nav = headerView.findViewById<TextView>(R.id.txt_email_nav)
            txt_nav.text = Paper.book().read<String>("email")
        } else {
            val txt_nav = headerView.findViewById<TextView>(R.id.txt_email_nav)
            txt_nav.text = Utils.user_current?.email
        }

    }


    private fun onCLickNav() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cart -> {
                    val intent = Intent(this, GioHangActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.details_order -> {
                    val intent = Intent(this, ChiTietDatHangActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.logout -> {
                    Paper.book().delete("user")
                    FirebaseAuth.getInstance().signOut()
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


    private fun onClickSearch() {
        binding.imbSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.edtSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun setGiaoDien() {
        var tenloaisanpham = intent.getStringExtra("tenloaisanpham")
        binding.txtLoaisanpham.text = tenloaisanpham
        tenloaisanpham = tenloaisanpham?.lowercase()
        binding.edtSearch.hint = "Tìm kiếm $tenloaisanpham"
    }

    private fun quayLaiTrangChu() {
        binding.imvBack.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.txtLoaisanpham.setOnClickListener {
            onBackPressed()
            finish()
        }
    }


    private fun laySanPhamGao() {
        val loai = intent.getIntExtra("loai", 1)
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.getSanPhamTheoLoai(loai)
        call.enqueue(object : retrofit2.Callback<SanPhamModel> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if (response.isSuccessful) {
                    val produce = response.body()?.result
                    /*
                      Toast.makeText(this@MainActivity, produce, Toast.LENGTH_SHORT).show()
                    */

                    if (!produce.isNullOrEmpty()) {
                        val randomPhanTu = produce.shuffled()
                        val list = randomPhanTu.take(12)

                        if (list.isNotEmpty()) {

                            adapter = RvSanPhamCacLoai(
                                list.toMutableList(),
                                object : OnClickSanPhamTheoLoai {
                                    override fun onClickSanPhamTheoLoai(position: Int) {
                                        /* Toast.makeText(this@GaoActivity,list[position].name,Toast.LENGTH_SHORT).show()*/
                                        val intent = Intent(
                                            this@SanPhamTheoLoaiActivity,
                                            ChiTietSanPhamActivity::class.java
                                        )
                                        intent.putExtra("name", list[position].name)
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

                                        binding.badgeCart.setNumber(
                                            Utils.manggiohang.getSoluong(),true
                                        )
                                    }
                                })
                            binding.rvsanphamtheoloaiGao.adapter = adapter
                            adapter.notifyDataSetChanged()
                            val itemDecoration = ItemOffsetDecoration(3)
                            binding.rvsanphamtheoloaiGao.addItemDecoration(itemDecoration)
                            setRVLayoutManager()
                            addEventLoad(produce, list as MutableList<SanPham>)
                            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong(),true)

                        } else {
                            Toast.makeText(
                                this@SanPhamTheoLoaiActivity,
                                "Không có sản phẩm để hiển thị",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SanPhamTheoLoaiActivity,
                            "Danh sách sản phẩm trống",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            }

            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }
        }

        )
    }
    private fun MutableList<GioHang>.getSoluong(): Int {
        var totalSoluong = 0
        for (gioHang in Utils.manggiohang) {
            totalSoluong += gioHang.soluongsanphamgiohang
        }
        return totalSoluong
    }

    private fun goToGioHang() {
        binding.imvGiohang.setOnClickListener {
            val intent = Intent(this, GioHangActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun addEventLoad(produce: MutableList<SanPham>, list: MutableList<SanPham>) {
        scrollListener = RvLoadMoreScroll(mLayoutManager as GridLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                LoadMoreData(produce, list)
            }
        })
        binding.rvsanphamtheoloaiGao.addOnScrollListener(scrollListener)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun LoadMoreData(produce: MutableList<SanPham>, current: MutableList<SanPham>) {

        adapter.addLoadingView()
        Handler().postDelayed({
            adapter.removeLoadingView()
            val remainingItems = produce.filter {
                /*san pham đã hiênr thị không còn chưa trong produce*/
                !current.contains(it)
            }
            val newList = remainingItems.take(12)
            laySanPhamGao()
            current.addAll(newList)
            adapter.addData(newList)
            scrollListener.setLoaded()
            binding.rvsanphamtheoloaiGao.post {
                adapter.notifyDataSetChanged()
            }
        }, 3000)
    }

    private fun setRVLayoutManager() {
        mLayoutManager = GridLayoutManager(this, 3)
        binding.rvsanphamtheoloaiGao.layoutManager = mLayoutManager
        binding.rvsanphamtheoloaiGao.setHasFixedSize(true)
        binding.rvsanphamtheoloaiGao.adapter = adapter
        (mLayoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        Constant.VIEW_TYPE_ITEM -> 1
                        Constant.VIEW_TYPE_LOADING -> 3
                        else -> -1
                    }
                }
            }
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong(),true)
        }else{
            binding.badgeCart.setNumber(0)
        }
    }

}