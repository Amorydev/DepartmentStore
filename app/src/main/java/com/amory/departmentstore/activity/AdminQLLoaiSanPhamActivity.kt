package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityAdminLoaiSanPhamBinding

class AdminQLLoaiSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminLoaiSanPhamBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoaiSanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCLickDanhMuc()
        onClickNavViewAdmin()

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