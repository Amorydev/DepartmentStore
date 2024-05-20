package com.amory.departmentstore.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvSanPhamAdmin
import com.amory.departmentstore.databinding.ActivityAdminQlsanPhamBinding
import com.amory.departmentstore.model.EventBus.SuaXoaEvent
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.RetrofitClient
import io.paperdb.Paper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminQLSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminQlsanPhamBinding
    var list = mutableListOf<SanPham>()
    private var listSanPham: SanPham? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminQlsanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCLickDanhMuc()
        onClickNavViewAdmin()
        onClickThem()
        hienThiSanPham()
    }

    private fun onClickThem() {
        binding.btnThem.setOnClickListener {
            val intent = Intent(this,AdminThemSanPhamActivity::class.java)
            intent.putExtra("sua",listSanPham)
            startActivity(intent)
        }
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
                R.id.dangxuat ->
                {
                    Paper.book().delete("user")
                    val intent = Intent(this, DangNhapActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.xemdonhang ->
                {
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
    private fun hienThiSanPham() {
        val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
        val call = service.getData()
        call.enqueue(object : Callback<SanPhamModel> {
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
                        if (produce.isNotEmpty()) {
                            list = produce
                            val adapter = RvSanPhamAdmin(list)
                            adapter.notifyDataSetChanged()
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
            override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
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
            val call = service.xoaSanPham(listSanPham!!.id)
            call.enqueue(object : Callback<SanPhamModel> {
                override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                    if (response.isSuccessful){
                        hienThiSanPham()
                    }
                }
                override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {
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
        val intent = Intent(this@AdminQLSanPhamActivity,AdminThemSanPhamActivity::class.java)
        intent.putExtra("sua",listSanPham)
        startActivity(intent)
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun eventSuaXoa(event: SuaXoaEvent){
        listSanPham = event.sanpham
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
}