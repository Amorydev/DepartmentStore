package com.amory.departmentstore.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.amory.departmentstore.fragment.CancelledFragment
import com.amory.departmentstore.fragment.DeliveredFragment
import com.amory.departmentstore.fragment.PendingFragment
import com.amory.departmentstore.fragment.ProcessingFragment
import com.amory.departmentstore.fragment.ShippedFragment

class ViewFragmentAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {

        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingFragment()
            1 -> ProcessingFragment()
            2 ->DeliveredFragment()
            3-> ShippedFragment()
            4-> CancelledFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
