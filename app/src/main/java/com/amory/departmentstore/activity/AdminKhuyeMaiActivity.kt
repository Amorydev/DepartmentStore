package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvKhuyenMaiAdmin
import com.amory.departmentstore.databinding.ActivityAdminKhuyeMaiBinding
import com.amory.departmentstore.model.EventBus.SuaXoaKhuyenMaiEvent
import com.amory.departmentstore.model.KhuyenMai
import com.amory.departmentstore.model.KhuyenMaiModel
import com.amory.departmentstore.retrofit.ApiBanHang
import com.amory.departmentstore.retrofit.RetrofitClient
import io.paperdb.Paper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminKhuyeMaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminKhuyeMaiBinding
    private var listKhuyenMai:KhuyenMai ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminKhuyeMaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCLickDanhMuc()
        onCLickAdd()
        onClickNavViewAdmin()
        showKhuyenMai()
    }

    private fun onCLickAdd() {
        binding.btnThem.setOnClickListener {
            val intent = Intent(this , AdminAddKhuyenMaiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showKhuyenMai() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.laykhuyenmai()
        call.enqueue(object : Callback<KhuyenMaiModel> {
            override fun onResponse(
                call: Call<KhuyenMaiModel>,
                response: Response<KhuyenMaiModel>
            ) {
                if (response.isSuccessful) {
                    val listKhuyenMai = response.body()?.banners!!
                    val adapter = RvKhuyenMaiAdmin(listKhuyenMai)
                    binding.rvKhuyenmai.adapter = adapter
                    binding.rvKhuyenmai.layoutManager = LinearLayoutManager(
                        this@AdminKhuyeMaiActivity,
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.rvKhuyenmai.setHasFixedSize(true)
                }
            }

            override fun onFailure(call: Call<KhuyenMaiModel>, t: Throwable) {
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title?.equals("Sửa")==true){
            SuaKhuyenMai()
        }else{
            XoaKhuyenMai()
        }
        return super.onContextItemSelected(item)
    }

    private fun XoaKhuyenMai() {
        val service = RetrofitClient.retrofitInstance.create(ApiBanHang::class.java)
        val call = service.xoakhuyenmai(listKhuyenMai?.id)
        call.enqueue(object : Callback<KhuyenMaiModel> {
            override fun onResponse(
                call: Call<KhuyenMaiModel>,
                response: Response<KhuyenMaiModel>
            ) {
                if (response.isSuccessful){
                    showKhuyenMai()
                }
            }

            override fun onFailure(call: Call<KhuyenMaiModel>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun SuaKhuyenMai() {
        val intent = Intent(this , AdminAddKhuyenMaiActivity::class.java)
        intent.putExtra("khuyenmai",listKhuyenMai)
        startActivity(intent)
    }

    private fun onCLickDanhMuc() {
        binding.imbDanhmucAdmin.setOnClickListener {
            binding.layoutDrawerAdmin.openDrawer(binding.navViewAdmin)
        }
    }
    @Subscribe(sticky =  true , threadMode = ThreadMode.MAIN)
    fun eventSuaXoaKhuyenMai(event: SuaXoaKhuyenMaiEvent){
        listKhuyenMai = event.khuyenmai
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