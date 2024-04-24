package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutChitietitemsBinding
import com.amory.departmentstore.model.Items
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class RvItems(val ds: MutableList<Items>) : RecyclerView.Adapter<RvItems.viewHolder>() {
    inner class viewHolder(private val binding: LayoutChitietitemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Items) {
            val soluong = data.number_of_products.toString()
            val tongTien = data.number_of_products * data.price
            binding.txtName.text = data.name
            binding.txtGiasanpham.text = formatAmount(data.price.toString())
            binding.txtSoluongsanpham1.text = "x $soluong"
            binding.txtSoluongsanpham2.text = "x $soluong"
            binding.txtTongtien.text = formatAmount(tongTien.toString())
            Glide.with(binding.root).load(data.image_url).centerCrop().into(binding.imbHinhanh)
        }
    }

    fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${format.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            LayoutChitietitemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
    }
}