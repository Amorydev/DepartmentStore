package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutSanphamBinding
import com.amory.departmentstore.model.SanPham
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class RvSanPham(val ds: List<SanPham>) : RecyclerView.Adapter<RvSanPham.viewHolder>() {
    inner class viewHolder(private val binding: LayoutSanphamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SanPham) {
            binding.txtTensanpham.text = data.tensanpham
            binding.txtgiasanpham.text = formatAmount(data.giasanpham)
            Glide.with(binding.root).load(data.hinhanh).centerCrop().into(binding.imgSanpham)
        }
    }
    //chuyen sang dinh dang 000.000d
    fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            LayoutSanphamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
    }

    override fun getItemCount(): Int {
        return ds.size
    }
}