package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvLoaiSanPhamAdmin
import com.amory.departmentstore.databinding.ActivityAdminLoaiSanPhamBinding
import com.amory.departmentstore.model.EventBus.SuaXoaLoaiEvent
import com.amory.departmentstore.model.LoaiSanPham
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import io.paperdb.Paper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminQLLoaiSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminLoaiSanPhamBinding
    private var loaiSanPham: LoaiSanPham? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoaiSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCLickDanhMuc()
        onClickNavViewAdmin()
        hienThiLoai()
        onClickThem()
    }

    private fun onClickThem() {
        binding.btnThem.setOnClickListener {
            val intent = Intent(this, AdminThemLoaiSanPhamActivity::class.java)
            intent.putExtra("sua", loaiSanPham)
            startActivity(intent)
        }
    }

    private fun hienThiLoai() {
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
                    val adapter = list?.let { RvLoaiSanPhamAdmin(it) }
                    binding.rvAdminLoaisanpham.adapter = adapter
                    binding.rvAdminLoaisanpham.layoutManager = GridLayoutManager(
                        this@AdminQLLoaiSanPhamActivity,
                        4,
                        GridLayoutManager.VERTICAL, false
                    )

                }
            }

            override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(
                    this@AdminQLLoaiSanPhamActivity,
                    "Lấy loại sản phẩm thất bại",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title == "Sửa") {
            SuaLoaiSanPham()
        } else {
            XoaLoaiSanPham()
        }
        return super.onContextItemSelected(item)

    }

    private fun SuaLoaiSanPham() {
        val intent =
            Intent(this@AdminQLLoaiSanPhamActivity, AdminThemLoaiSanPhamActivity::class.java)
        intent.putExtra("sua", loaiSanPham)
        startActivity(intent)
    }

    private fun XoaLoaiSanPham() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.xoaloaisanpham(loaiSanPham!!.id)
        call.enqueue(object : Callback<SanPhamModel> {
            override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                if (response.isSuccessful) {
                    hienThiLoai()
                }
            }

            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun onClickNavViewAdmin() {
        binding.navViewAdmin.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.quanlyloaisanpham -> {
                    val intent = Intent(this, AdminQLLoaiSanPhamActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.quanlysanpham -> {
                    val intent = Intent(this, AdminQLSanPhamActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.dangxuat -> {
                    Paper.book().delete("user")
                    val intent = Intent(this, DangNhapActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.xemdonhang -> {
                    val intent = Intent(this, AdminChiTietDonHangActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.khuyenmai -> {
                    val intent = Intent(this, AdminKhuyeMaiActivity::class.java)
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun eventBus(event: SuaXoaLoaiEvent) {
        loaiSanPham = event.loaiSanPham
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}