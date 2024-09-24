package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutItemKhuyenmaiBinding
import com.amory.departmentstore.model.Promotion
import com.bumptech.glide.Glide

class RvPromotion(val list: MutableList<Promotion>):RecyclerView.Adapter<RvPromotion.ViewHolder>() {
    inner class ViewHolder(private val binding: LayoutItemKhuyenmaiBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data:Promotion){
            binding.txtKhuyenmai.text = data.name
            binding.txtThongtinKhuyenmai.text = data.description
            Glide.with(binding.root).load(data.imageUrl).fitCenter().into(binding.imgKhuyenmai)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutItemKhuyenmaiBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}