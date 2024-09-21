package com.amory.departmentstore.activity.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.Interface.OnClickSanPhamTheoLoai
import com.amory.departmentstore.Interface.OnLoadMoreListener
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
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
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySanPhamTheoLoaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)
        laySanPhamGao()
        quayLaiTrangChu()
        goToGioHang()
        setGiaoDien()
        onClickDanhMuc()
        onCLickNav()
        OnclickNavHeader()
        onClickSearch()
        onClickChat()
    }

    private fun onClickChat() {
        binding.imvChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
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
        val userCurrent = Paper.book().read<User>("user")
        val menu = binding.navView.menu
        if (userCurrent == null || Utils.user_current == null){
            menu.findItem(R.id.logout).isVisible = false
            menu.findItem(R.id.changePassword).isVisible = false
        }
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cart -> {
                    val user = Paper.book().read<User>("user")
                    if (user != null || Utils.user_current != null) {
                        val intent = Intent(this, GioHangActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DangNhapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    true
                }

                R.id.details_order -> {
                    val user = Paper.book().read<User>("user")
                    if (user != null || Utils.user_current != null) {
                        val intent = Intent(this, ChiTietDonHangActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, DangNhapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    true
                }

                R.id.logout -> {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Đăng xuất")
                    alertDialog.setMessage("Bạn chắc chắn muốn đăng xuất")
                    alertDialog.setNegativeButton("Không") { dialog, which ->
                        dialog.dismiss()
                    }
                    alertDialog.setPositiveButton("Có") { dialog, which ->
                        Paper.book().delete("user")
                        FirebaseAuth.getInstance().signOut()
                        val editor = sharedPreferences.edit()
                        editor.remove("token")
                        editor.apply()
                        val intent = Intent(this, DangNhapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    alertDialog.show()
                    true
                }

                R.id.contact -> {
                    val intent = Intent(this, LienHeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }

                R.id.product -> {
                    binding.layoutDrawer.closeDrawer(binding.navView)
                    true
                }

                R.id.discount -> {
                    val intent = Intent(this, KhuyenMaiActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }
                R.id.changePassword ->{
                    val intent = Intent(this, ChangePasswordActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
            val categoryId = intent.getIntExtra("loai",0)
            if (categoryId != 0) {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("categoryId", categoryId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        binding.edtSearch.setOnClickListener {
            val categoryId = intent.getIntExtra("loai",0)
            if (categoryId != 0) {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("categoryId", categoryId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
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
        val loai = intent.getIntExtra("loai", 0)
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.getSanPhamTheoLoai(loai)
        binding.shimmerframe.visibility = View.VISIBLE
        binding.rvsanphamtheoloaiGao.visibility = View.INVISIBLE
        binding.shimmerframe.startShimmer()
        call.enqueue(object : retrofit2.Callback<SanPhamModel> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if (response.isSuccessful) {
                    val produce = response.body()?.data
                    /*
                      Toast.makeText(this@MainActivity, produce, Toast.LENGTH_SHORT).show()
                    */

                    if (!produce.isNullOrEmpty()) {
                        val randomPhanTu = produce.shuffled()
                        val list = randomPhanTu.take(12)

                        if (list.isNotEmpty()) {

                            adapter = RvSanPhamCacLoai(
                                object : OnClickSanPhamTheoLoai {
                                    override fun onClickSanPhamTheoLoai(position: Int) {
                                        /* Toast.makeText(this@GaoActivity,list[position].name,Toast.LENGTH_SHORT).show()*/
                                        val intent = Intent(
                                            this@SanPhamTheoLoaiActivity,
                                            ChiTietSanPhamActivity::class.java
                                        )
                                        intent.putExtra("name", list[position].name)
                                        intent.putExtra("price", list[position].price)
                                        intent.putExtra("hinhanhsanpham", list[position].thumbnail)
                                        intent.putExtra("motasanpham", list[position].description)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)

                                    }
                                })
                            adapter.updateList(list.toMutableList())
                           Handler().postDelayed({
                               binding.rvsanphamtheoloaiGao.adapter = adapter
                               adapter.notifyDataSetChanged()
                               binding.shimmerframe.stopShimmer()
                               binding.rvsanphamtheoloaiGao.visibility = View.VISIBLE
                           },2000)
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