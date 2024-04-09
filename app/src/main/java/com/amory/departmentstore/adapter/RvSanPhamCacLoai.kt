package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutSanphamTheoloaiBinding
import com.amory.departmentstore.model.SanPham
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class RvSanPhamCacLoai(private var ds_gao:MutableList<SanPham>): RecyclerView.Adapter<RvSanPhamCacLoai.viewHolder>() {
    inner class viewHolder(private val binding: LayoutSanphamTheoloaiBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data:SanPham){
            binding.tensanphamtheoloai.text = data.tensanpham
            binding.giasanphamtheoloai.text = formatAmount(data.giasanpham)
            Glide.with(binding.root).load(data.hinhanh).centerCrop().into(binding.imvhinhanhloaisanpham)
        }
    }
    //chuyen sang dinh dang 000.000d
    fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = LayoutSanphamTheoloaiBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds_gao.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds_gao[position])
    }
}