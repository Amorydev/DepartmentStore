package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutLoaisanphamBinding
import com.amory.departmentstore.model.LoaiSanPham
import com.amory.departmentstore.model.SanPhamModel
import com.bumptech.glide.Glide

class RvLoaiSanPham(val ds:List<LoaiSanPham>):RecyclerView.Adapter<RvLoaiSanPham.viewHolder>() {

    inner class viewHolder(private val binding:LayoutLoaisanphamBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data:LoaiSanPham){
            binding.tvloaisanpham.text = data.tenloaisanpham
            Glide.with(binding.root).load(data.hinhanh).centerCrop().into(binding.imvhinhanh)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = LayoutLoaisanphamBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
    }
}