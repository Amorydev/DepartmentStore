package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutChitietGiohangBinding
import com.amory.departmentstore.model.SanPham
import com.bumptech.glide.Glide

class RvSanPhamTrongGioHang(private var ds : MutableList<SanPham>):RecyclerView.Adapter<RvSanPhamTrongGioHang.viewHolder>() {
    inner class viewHolder(private var binding : LayoutChitietGiohangBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SanPham){
            binding.txtTensanphamtronggiohang.text = data.tensanpham
            Glide.with(binding.root).load(data.hinhanh).into(binding.imvHinhanhsanphamtrongiohang)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = LayoutChitietGiohangBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
    }
}