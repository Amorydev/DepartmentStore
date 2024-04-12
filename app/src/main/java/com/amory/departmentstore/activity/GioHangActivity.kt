package com.amory.departmentstore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amory.departmentstore.R
import com.amory.departmentstore.adapter.RvSanPhamTrongGioHang
import com.amory.departmentstore.databinding.ActivityGioHangBinding
import com.amory.departmentstore.databinding.LayoutLoaisanphamBinding
import com.amory.departmentstore.model.SanPham

class GioHangActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGioHangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGioHangBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val list: MutableList<SanPham> = mutableListOf()
        list.add(
            SanPham(0,
                "https://cdn.tgdd.vn/Products/Images/2513/323829/bhx/nep-than-pmt-tui-1kg-clone-202403221751543836.jpg",
                "3 túi nếp than PMT 1kg",
                "",
                ""
            )
        )
        list.add(
            SanPham(0,
                "https://cdn.tgdd.vn/Products/Images/2513/323829/bhx/nep-than-pmt-tui-1kg-clone-202403221751543836.jpg",
                "3 túi nếp than PMT 1kg",
                "",
                ""
            )
        )
        list.add(
            SanPham(0,
                "https://cdn.tgdd.vn/Products/Images/2513/323829/bhx/nep-than-pmt-tui-1kg-clone-202403221751543836.jpg",
                "3 túi nếp than PMT 1kg",
                "",
                ""
            )
        )
        list.add(
            SanPham(0,
                "https://cdn.tgdd.vn/Products/Images/2513/323829/bhx/nep-than-pmt-tui-1kg-clone-202403221751543836.jpg",
                "3 túi nếp than PMT 1kg",
                "",
                ""
            )
        )


        val adapter = RvSanPhamTrongGioHang(list)
        binding.rvSanphamTronggiohang.adapter = adapter
        binding.rvSanphamTronggiohang.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}