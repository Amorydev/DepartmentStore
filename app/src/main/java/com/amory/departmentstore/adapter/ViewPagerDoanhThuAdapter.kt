package com.amory.departmentstore.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amory.departmentstore.fragment.CancelledFragment
import com.amory.departmentstore.fragment.DeliveredFragment
import com.amory.departmentstore.fragment.DoanhThuTheoDanhMucFragment
import com.amory.departmentstore.fragment.DoanhThuTheoSanPhamFragment
import com.amory.departmentstore.fragment.DoanhThuTheoThoiGianFragment
import com.amory.departmentstore.fragment.PendingFragment
import com.amory.departmentstore.fragment.ProcessingFragment
import com.amory.departmentstore.fragment.ShippedFragment

class ViewPagerDoanhThuAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DoanhThuTheoThoiGianFragment()
            1 -> DoanhThuTheoDanhMucFragment()
            2 -> DoanhThuTheoSanPhamFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}