package com.amory.departmentstore.activity.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.activity.user.DangNhapActivity
import com.amory.departmentstore.adapter.RvSanPhamAdmin
import com.amory.departmentstore.databinding.ActivityAdminQlsanPhamBinding
import com.amory.departmentstore.model.EventBus.SuaXoaEvent
import com.amory.departmentstore.model.CategoryModel
import com.amory.departmentstore.model.Product
import com.amory.departmentstore.model.ProductResponse
import com.amory.departmentstore.retrofit.APIBanHang.APICallCategories
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import io.paperdb.Paper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminQLSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminQlsanPhamBinding
    var list = mutableListOf<Product>()
    private var listProduct: Product? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminQlsanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)
        onCLickDanhMuc()
        onClickNavViewAdmin()
        showSpnLoai()
        onClickThem()
        hienThiSanPham()
    }



    private fun onClickThem() {
        binding.btnThem.setOnClickListener {
            val intent = Intent(this, AdminThemSanPhamActivity::class.java)
            /*intent.putExtra("sua",listSanPham)*/
            startActivity(intent)
        }
    }

    private fun onClickNavViewAdmin() {
        binding.navViewAdmin.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.quanlyloaisanpham ->{
                    val intent = Intent(this, AdminQLLoaiSanPhamActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.quanlysanpham ->{
                    val intent = Intent(this, AdminQLSanPhamActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.trangchu ->{
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.dangxuat ->
                {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setTitle("Đăng xuất")
                    alertDialog.setMessage("Bạn chắc chắn muốn đăng xuất")
                    alertDialog.setNegativeButton("Không"){
                            dialog, which ->
                        dialog.dismiss()
                    }
                    alertDialog.setPositiveButton("Có"){
                            dialog, which ->
                        Paper.book().delete("user")
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
                R.id.xemdonhang ->
                {
                    val intent = Intent(this, AdminChiTietDonHangActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.khuyenmai ->
                {
                    val intent = Intent(this, AdminKhuyeMaiActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.thongke ->{

                    val intent = Intent(this, DoanhSoActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.quanlyuser ->{

                    val intent = Intent(this, QuanLyUserActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.quanlyvoucher ->{
                    val intent = Intent(this, QuanLyMaGiamGiaActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    true
                }
            }

        }
    }
    private fun onCLickDanhMuc() {
        binding.imbDanhmucAdmin.setOnClickListener {
            binding.layoutDrawerAdmin.openDrawer(binding.navViewAdmin)
        }
    }
    private fun hienThiSanPham() {
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.getData()
        call.enqueue(object : Callback<ProductResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val produce = response.body()?.data
                    /*
                      Toast.makeText(this@MainActivity, produce, Toast.LENGTH_SHORT).show()
                    */
                    if (!produce.isNullOrEmpty()) {
                        if (produce.isNotEmpty()) {
                            list = produce
                            val adapter = RvSanPhamAdmin(list)
                            binding.rvSuasanpham.adapter = adapter
                            binding.rvSuasanpham.layoutManager = GridLayoutManager(this@AdminQLSanPhamActivity,3,
                                GridLayoutManager.VERTICAL,false)
                            binding.rvSuasanpham.setHasFixedSize(true)
                            registerForContextMenu(binding.rvSuasanpham)
                        } else {
                            Toast.makeText(
                                this@AdminQLSanPhamActivity,
                                "Không có sản phẩm để hiển thị",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AdminQLSanPhamActivity,
                            "Danh sách sản phẩm trống",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                t.printStackTrace()
                Log.e("Amory", "Error occurred: ${t.message}", t)
                Toast.makeText(this@AdminQLSanPhamActivity, "Lấy sản phẩm thất bại", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title?.equals("Sửa") == true){
            SuaSanPham()
        }else{
            XoaSanPham()
        }
        return super.onContextItemSelected(item)
    }

    private fun XoaSanPham() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Bạn có chắc chắn muốn xóa")
        dialog.setPositiveButton("Có") { dialog, which ->
            val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
            val call = service.xoaSanPham(listProduct!!.id)
            call.enqueue(object : Callback<ProductResponse> {
                override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                    if (response.isSuccessful){
                        hienThiSanPham()
                    }
                }
                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
        dialog.setNegativeButton("Không") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun SuaSanPham() {
        val intent = Intent(this@AdminQLSanPhamActivity, AdminThemSanPhamActivity::class.java)
        intent.putExtra("sua",listProduct)
        startActivity(intent)
    }
    private fun showSpnLoai(){
        val serviceCategories = RetrofitClient.retrofitInstance.create(APICallCategories::class.java)
        val callCategories = serviceCategories.getLoaisanPham()
        callCategories.enqueue(object : Callback<CategoryModel>{
            override fun onResponse(
                call: Call<CategoryModel>,
                response: Response<CategoryModel>
            ) {
                if (response.isSuccessful){
                    var  listCategories = mutableListOf<String>()
                    listCategories.add("Tất cả")
                    listCategories = response.body()?.data?.map { it.name } as MutableList<String>
                    val adapter = ArrayAdapter(this@AdminQLSanPhamActivity,android.R.layout.simple_spinner_item,listCategories)
                    binding.spnLoai.adapter = adapter
                    binding.spnLoai.onItemSelectedListener =
                        object :AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedCategoryName = listCategories[position]
                                val selectedIndex = response.body()?.data?.indexOfFirst { it.name == selectedCategoryName }
                                val categoryId = selectedIndex?.let { idx ->
                                    if (idx != -1) response.body()?.data?.get(idx)?.id ?: 0 else 0
                                } ?: 0
                                XuLyLocSanPham(categoryId)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                }

            }

            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun XuLyLocSanPham(categoryId:Int) {
        val keyEDT = binding.edtSearch.text.trim().toString()
        binding.imbSearch.setOnClickListener {
            val serviceSearch = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
            val callSearch = serviceSearch.timkiem(categoryId,keyEDT)
            callSearch.enqueue(object : Callback<ProductResponse>{
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    if (response.isSuccessful){
                        list.clear()
                        for (product in response.body()?.data!!){
                            list.add(product)
                        }
                        val adapter = RvSanPhamAdmin(list)
                        binding.rvSuasanpham.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun eventSuaXoa(event: SuaXoaEvent){
        listProduct = event.sanpham
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        hienThiSanPham()
    }
}