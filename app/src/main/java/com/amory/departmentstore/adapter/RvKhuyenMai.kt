package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutItemKhuyenmaiBinding
import com.amory.departmentstore.model.KhuyenMai
import com.bumptech.glide.Glide

class RvKhuyenMai(val list: List<KhuyenMai>):RecyclerView.Adapter<RvKhuyenMai.viewHolder>() {
    inner class viewHolder(private val binding: LayoutItemKhuyenmaiBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data:KhuyenMai){
            binding.txtKhuyenmai.text = data.name
            binding.txtThongtinKhuyenmai.text = data.description
            Glide.with(binding.root).load(data.imageUrl).fitCenter().into(binding.imgKhuyenmai)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutItemKhuyenmaiBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(list[position])
    }
}