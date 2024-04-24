package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityAdminBinding
import io.paperdb.Paper

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this)
        onCLickDanhMuc()
        onClickNavViewAdmin()
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

    private fun onCLickDanhMuc() {
        binding.imbDanhmucAdmin.setOnClickListener {
            binding.layoutDrawerAdmin.openDrawer(binding.navViewAdmin)
        }
    }
}