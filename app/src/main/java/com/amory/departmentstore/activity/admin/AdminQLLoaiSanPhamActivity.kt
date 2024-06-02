package com.amory.departmentstore.activity.admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.activity.user.DangNhapActivity
import com.amory.departmentstore.activity.user.DoanhSoActivity
import com.amory.departmentstore.adapter.RvLoaiSanPhamAdmin
import com.amory.departmentstore.databinding.ActivityAdminLoaiSanPhamBinding
import com.amory.departmentstore.model.EventBus.SuaXoaLoaiEvent
import com.amory.departmentstore.model.LoaiSanPham
import com.amory.departmentstore.model.LoaiSanPhamModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallCategories
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
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
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoaiSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = this.getSharedPreferences("SAVE_TOKEN", Context.MODE_PRIVATE)
        onCLickDanhMuc()
        onClickNavViewAdmin()
        hienThiLoai()
        onClickThem()
    }

    private fun onClickThem() {
        binding.btnThem.setOnClickListener {
            val intent = Intent(this, AdminThemLoaiSanPhamActivity::class.java)
           /* intent.putExtra("sua", loaiSanPham)*/
            startActivity(intent)
        }
    }

    private fun hienThiLoai() {
        val service = RetrofitClient.retrofitInstance.create(APICallCategories::class.java)
        val call = service.getLoaisanPham()
        call.enqueue(object : Callback<LoaiSanPhamModel> {
            override fun onResponse(
                call: Call<LoaiSanPhamModel>,
                response: Response<LoaiSanPhamModel>
            ) {
                if (response.isSuccessful) {

                    val list = response.body()?.data
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
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Bạn có chắc chắn muốn xóa")
        dialog.setPositiveButton("Có") { dialog, which ->
            val service = RetrofitClient.retrofitInstance.create(APICallCategories::class.java)
            val call = service.xoaLoaiSanPham(loaiSanPham!!.id)
            call.enqueue(object : Callback<LoaiSanPhamModel> {
                override fun onResponse(
                    call: Call<LoaiSanPhamModel>,
                    response: Response<LoaiSanPhamModel>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AdminQLLoaiSanPhamActivity,
                            "Xóa thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        loaiSanPham = null
                        hienThiLoai()
                    }
                }

                override fun onFailure(call: Call<LoaiSanPhamModel>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
        dialog.setNegativeButton("Không") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
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
                R.id.quanlyvoucher ->{
                    val intent = Intent(this, QuanLyMaGiamGiaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.quanlyuser ->{

                    val intent = Intent(this, QuanLyUserActivity::class.java)
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

}