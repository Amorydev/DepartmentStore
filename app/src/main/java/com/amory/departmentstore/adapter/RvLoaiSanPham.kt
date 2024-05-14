package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutLoaisanphamBinding
import com.amory.departmentstore.model.LoaiSanPham
import com.amory.departmentstore.Interface.OnClickRvLoaiSanPham

import com.bumptech.glide.Glide

class RvLoaiSanPham(val ds: List<LoaiSanPham>, private val onClickRvSanPham: OnClickRvLoaiSanPham) :
    RecyclerView.Adapter<RvLoaiSanPham.viewHolder>() {

    inner class viewHolder(private val binding: LayoutLoaisanphamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LoaiSanPham) {
            binding.tvloaisanpham.text = data.name
            Glide.with(binding.root).load(data.image_url).centerCrop().into(binding.imvhinhanh)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            LayoutLoaisanphamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }


    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
        holder.itemView.setOnClickListener {
            onClickRvSanPham.onClickLoaiSanPham(position)
        }


    }
}