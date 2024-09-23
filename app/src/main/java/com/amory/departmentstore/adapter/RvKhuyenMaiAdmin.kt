package com.amory.departmentstore.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutItemKhuyenmaiBinding
import com.amory.departmentstore.model.EventBus.SuaXoaKhuyenMaiEvent
import com.amory.departmentstore.model.Promotion
import com.bumptech.glide.Glide
import org.greenrobot.eventbus.EventBus

class RvKhuyenMaiAdmin(val list: List<Promotion>): RecyclerView.Adapter<RvKhuyenMaiAdmin.viewHolder>() {
    inner class viewHolder(private val binding: LayoutItemKhuyenmaiBinding): RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener {
        fun bind(data: Promotion){
            binding.txtKhuyenmai.text = data.name
            binding.txtThongtinKhuyenmai.text = data.description
            Glide.with(binding.root).load(data.imageUrl).centerCrop().into(binding.imgKhuyenmai)
        }
        init {
            itemView.setOnCreateContextMenuListener(this)
        }
        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add(0, 0, adapterPosition, "Sửa")
            menu?.add(0, 1, adapterPosition, "Xóa")
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
        holder.itemView.setOnLongClickListener {
            val x = it.width / 2f
            val y = it.height / 2f
            it.showContextMenu(x, y)
            EventBus.getDefault().postSticky(SuaXoaKhuyenMaiEvent(list[position]))
            true
        }
    }
}