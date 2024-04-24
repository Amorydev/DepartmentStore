package com.amory.departmentstore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.ActivityAdminLoaiSanPhamBinding
import com.amory.departmentstore.databinding.ActivityAdminQlsanPhamBinding

class AdminQLSanPhamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminQlsanPhamBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminQlsanPhamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onCLickDanhMuc()
        onClickNavViewAdmin()
        onClickThem()
    }

    private fun onClickThem() {
        binding.btnThem.setOnClickListener {
            val intent = Intent(this,AdminThemSanPhamActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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