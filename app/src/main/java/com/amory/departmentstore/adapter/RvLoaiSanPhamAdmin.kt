package com.amory.departmentstore.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutLoaisanphamAdminBinding
import com.amory.departmentstore.model.EventBus.SuaXoaLoaiEvent
import com.amory.departmentstore.model.LoaiSanPham
import com.bumptech.glide.Glide
import org.greenrobot.eventbus.EventBus

class RvLoaiSanPhamAdmin(val ds:List<LoaiSanPham>):RecyclerView.Adapter<RvLoaiSanPhamAdmin.viewHolder>() {

    inner class viewHolder(private val binding:LayoutLoaisanphamAdminBinding):RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener {
        fun bind(data:LoaiSanPham){
            binding.tvloaisanpham.text = data.name
            Glide.with(binding.root).load(data.imageUrl).centerCrop().into(binding.imvhinhanh)
        }
        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add(0,0,adapterPosition,"Sửa")
            menu?.add(0,1,adapterPosition, "Xóa")
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = LayoutLoaisanphamAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }


    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
        holder.itemView.setOnLongClickListener{
            val x = it.width / 2f
            val y = it.height / 2f
            it.showContextMenu(x, y)
            EventBus.getDefault().postSticky(SuaXoaLoaiEvent(ds[position]))
            true
        }
    }
}