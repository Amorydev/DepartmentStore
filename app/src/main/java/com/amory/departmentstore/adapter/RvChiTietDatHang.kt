package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.Donhang


class RvChiTietDatHang(private val parents: MutableList<Donhang>?) : RecyclerView.Adapter<RvChiTietDatHang.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_parent_items, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parents!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent = parents!![position]
        holder.bind(parent)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_parent_items)
        private val txt: TextView = itemView.findViewById(R.id.txt_sodonhang)
        private val trangthai: TextView = itemView.findViewById(R.id.txt_trangthai)

        @SuppressLint("SetTextI18n")
        fun bind(parent: Donhang) {
            txt.text = "Đơn hàng ${parent.id}"
            trangthai.text = trangThaiDonHang(parent.status)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RvItems(parent.item)
                setRecycledViewPool(viewPool)
            }
        }
    }
    private fun trangThaiDonHang(status:Int): String {
        var result = ""
        when(status){
            0 -> {
                result = "Đơn hàng đang được xử lí"
            }
            1 -> {
                result = "Đơn hàng đã bàn giao cho đơn vị vận chuyển"
            }
            2 -> {
                result = "Đơn hàng đang được giao"
            }
            3 -> {
                result = "Đơn hàng giao thành công"
            }
            2 -> {
                result = "Đơn hàng đã hủy"
            }

        }
        return result
    }
}