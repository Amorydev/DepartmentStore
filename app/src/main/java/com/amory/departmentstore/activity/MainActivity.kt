package com.amory.departmentstore.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.ItemOffsetDecoration
import com.amory.departmentstore.adapter.RvLoadMoreScroll
import com.amory.departmentstore.adapter.RvLoaiSanPham
import com.amory.departmentstore.adapter.RvSanPham
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.ActivityMainBinding
import com.amory.departmentstore.model.Constant.VIEW_TYPE_ITEM
import com.amory.departmentstore.model.Constant.VIEW_TYPE_LOADING
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.Banner
import com.amory.departmentstore.model.BannerModel
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.amory.departmentstore.Interface.OnClickRvLoaiSanPham
import com.amory.departmentstore.Interface.OnClickRvSanPham
import com.amory.departmentstore.Interface.OnLoadMoreListener
import com.amory.departmentstore.model.User
import com.amory.departmentstore.retrofit.APIBanHang.APICallBanners
import com.amory.departmentstore.retrofit.APIBanHang.APICallCategories
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var listBanners: MutableList<Banner>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this)

        /*  onTouch()*/
        /*  showSanPham()*/
        /*Log.d("paper", Paper.book().path)*/

        if (Utils.kiemTraKetNoi(this)) {
            /* Toast.makeText(this, "Có internet", Toast.LENGTH_SHORT).show()*/
            listBanners = mutableListOf()
            RetrofitClient.init(this)
            sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)
            /* Toast.makeText(this@MainActivity,Utils.user_current.toString(),Toast.LENGTH_SHORT).show()*/
            paddingRv()
            laySanPham()
            layLoaiSanPham()
            onClickDanhMuc()
            OnclickNavHeader()
            onClickSearch()
            SlideQuangCao()
            goToGioHang()
            gotoChat()
            onCLickNav()
        } else {
            Toast.makeText(this, "Vui lòng kết nối internet", Toast.LENGTH_SHORT).show()
        }

    }


    private fun gotoChat() {
        binding.btnChat.setOnClickListener {
            val user = Paper.book().read<User>("user")
            if (user != null || Utils.user_current != null) {
                val intent = Intent(this, ChatActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                val intent = Intent(this, DangNhapActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
            }
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

    private fun paddingRv() {
        val itemDecoration = ItemOffsetDecoration(1)
        binding.rvSanpham.addItemDecoration(itemDecoration)
    }


    private fun OnclickNavHeader() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val btnDangKy = headerView.findViewById<Button>(R.id.btnDangky)
        val btnDangNhap = headerView.findViewById<Button>(R.id.btnDangnhap)
        val imageProfile = headerView.findViewById<ImageView>(R.id.profile_image)
        val txt_nav = headerView.findViewById<TextView>(R.id.txt_email_nav)
        val user = Paper.book().read<User>("user")
        if (user != null) {
            txt_nav.text = Paper.book().read<String>("email")
            btnDangNhap.visibility = View.INVISIBLE
            btnDangKy.visibility = View.INVISIBLE
        } else {
            txt_nav.text = Utils.user_current?.email
            btnDangNhap.visibility = View.INVISIBLE
            btnDangKy.visibility = View.INVISIBLE
        }

        if (user == null && Utils.user_current == null) {
            btnDangNhap.visibility = View.VISIBLE
            btnDangKy.visibility = View.VISIBLE
            imageProfile.visibility = View.INVISIBLE
            txt_nav.visibility = View.INVISIBLE
        }
        btnDangKy.setOnClickListener {
            val intent = Intent(this, DangKiActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        btnDangNhap.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
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


    private fun goToGioHang() {
        binding.imvGiohang.setOnClickListener {
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
        }
    }


    private fun layLoaiSanPham() {
        val service = RetrofitClient.retrofitInstance.create(APICallCategories::class.java)
        val call = service.getLoaisanPham()
        binding.shimmerframe.visibility = View.VISIBLE
        binding.shimmerframe.startShimmer()
        call.enqueue(object : Callback<LoaiSanPhamModel> {
            override fun onResponse(
                call: Call<LoaiSanPhamModel>,
                response: Response<LoaiSanPhamModel>
            ) {
                if (response.isSuccessful) {

                    val list = response.body()?.data
                    /*Toast.makeText(this@MainActivity, list, Toast.LENGTH_SHORT).show()*/
                    val adapter = list?.let {
                        RvLoaiSanPham(it, object : OnClickRvLoaiSanPham {
                            override fun onClickLoaiSanPham(position: Int) {
                                /*Toast.makeText(
                                    this@MainActivity,
                                    "Bạn chọn " + list[position].tenloaisanpham,
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                when (list[position].id) {
                                    1 -> {
                                        /*
                                                                                GoToSanPhamGao()
                                        */
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].id)
                                        intent.putExtra("tenloaisanpham", list[position].name)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }
                                    else -> {
                                        val intent = Intent(
                                            this@MainActivity,
                                            SanPhamTheoLoaiActivity::class.java
                                        )
                                        intent.putExtra("loai", list[position].id)
                                        intent.putExtra("tenloaisanpham", list[position].name)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }

                                }
                            }
                        })
                    }
                    Handler().postDelayed({
                        binding.shimmerframe.visibility = View.GONE
                        binding.shimmerframe.stopShimmer()
                        binding.layoutContrains.visibility = View.VISIBLE
                    }, 1000)
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
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.getData()
        binding.shimmerframe.visibility = View.VISIBLE
        binding.layoutContrains.visibility = View.INVISIBLE
        binding.shimmerframe.startShimmer()
        call.enqueue(object : Callback<SanPhamModel> {
            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
                /*Log.e("Amory", "Error occurred: ${t.message}", t)*/
                Toast.makeText(this@MainActivity, "Lấy sản phẩm thất bại", Toast.LENGTH_SHORT)
                    .show()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SanPhamModel>,
                response: Response<SanPhamModel>
            ) {
                if (response.isSuccessful) {
                    val produce = response.body()?.data
                    /*
                      Toast.makeText(this@MainActivity, produce, Toast.LENGTH_SHORT).show()
                    */

                    if (!produce.isNullOrEmpty()) {

                        val list = produce.shuffled().take(12)

                        if (list.isNotEmpty()) {

                            adapter = RvSanPham( object : OnClickRvSanPham {
                                    override fun onClickSanPham(position: Int) {
                                        /*Toast.makeText(
                                            applicationContext,
                                            "Mua " + list[position].category_id, Toast.LENGTH_SHORT
                                        ).show()*/
                                       startChiTietSanPham(list[position])
                                    }
                                })
                            adapter.updateList(list as MutableList<SanPham>)
                            Handler().postDelayed({
                                binding.shimmerframe.visibility = View.GONE
                                binding.shimmerframe.stopShimmer()
                                binding.layoutContrains.visibility = View.VISIBLE
                            }, 1000)
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
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong(), true)
        } else {
            binding.badgeCart.setNumber(0)
        }
    }

    private fun startChiTietSanPham(sanPham: SanPham) {
        val intent = Intent(
            this@MainActivity, ChiTietSanPhamActivity::class.java
        )
        intent.putExtra("name", sanPham.name)
        intent.putExtra("idsanpham", sanPham.id)
        intent.putExtra("price", sanPham.price)
        intent.putExtra("hinhanhsanpham", sanPham.imageUrl)
        intent.putExtra("motasanpham", sanPham.description)
        startActivity(intent)
        /*overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)*/
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
                /*san pham đã hiên thị không còn chưa trong produce*/
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
        val imageList = ArrayList<SlideModel>()
        /*imageList.add(SlideModel("https://cdn.tgdd.vn/bachhoaxanh/banners/5599/thanh-ly-giam-soc-22032024894.jpg"))
        imageList.add(SlideModel("https://cdn.tgdd.vn/bachhoaxanh/banners/5599/san-sale-gia-soc-cung-bhx-12032024133716.jpg"))
        imageList.add(SlideModel("https://cdn.tgdd.vn/bachhoaxanh/banners/5599/sua-cac-loai-3012202311948.jpg"))
        binding.imageSlider.setImageList(imageList,ScaleTypes.FIT)*/
        val service = RetrofitClient.retrofitInstance.create(APICallBanners::class.java)
        val call = service.layKhuyenMai()
        binding.shimmerframe.visibility = View.VISIBLE
        binding.shimmerframe.startShimmer()
        call.enqueue(object : Callback<BannerModel> {
            override fun onResponse(
                call: Call<BannerModel>,
                response: Response<BannerModel>
            ) {
                if (response.isSuccessful) {
                    listBanners = response.body()?.data!!
                    for (element in listBanners) {
                        val image_url = element.imageUrl
                        imageList.add(SlideModel("$image_url"))
                    }
                    Handler().postDelayed({
                        binding.shimmerframe.visibility = View.INVISIBLE
                        binding.shimmerframe.stopShimmer()
                        binding.layoutContrains.visibility = View.VISIBLE
                        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                        /*
                                                Toast.makeText(this@MainActivity,listKhuyenMai.toString(),Toast.LENGTH_LONG).show()
                        */
                        binding.imageSlider.setItemClickListener(
                            object : ItemClickListener {
                                override fun doubleClick(position: Int) {
                                }

                                override fun onItemSelected(position: Int) {
                                    val intent =
                                        Intent(this@MainActivity, KhuyenMaiActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                            }
                        )
                    }, 1000)

                }
            }

            override fun onFailure(call: Call<BannerModel>, t: Throwable) {
                t.printStackTrace()
                /*Log.d("banners", t.message.toString())*/
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isLoadMore) {
            isLoadMore = false
        } else {
            super.onBackPressed()
        }
    }


    override fun onResume() {
        super.onResume()
        if (Utils.manggiohang.getSoluong() != 0) {
            binding.badgeCart.setNumber(Utils.manggiohang.getSoluong(), true)
        } else {
            binding.badgeCart.setNumber(0)
        }
    }

    override fun onStart() {
        super.onStart()
        RetrofitClient.init(this)
    }

}