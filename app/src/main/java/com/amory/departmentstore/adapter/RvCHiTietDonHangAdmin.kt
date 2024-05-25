package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.Donhang
import com.amory.departmentstore.model.Order
import com.amory.departmentstore.model.OrderRequest
import org.greenrobot.eventbus.EventBus

class RvCHiTietDonHangAdmin(private val parents: MutableList<OrderRequest>?) : RecyclerView.Adapter<RvCHiTietDonHangAdmin.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_parent_items, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parents!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent = parents!![position]
        holder.bind(parent)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_parent_items)
        @SuppressLint("SetTextI18n")
        fun bind(parent: OrderRequest) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RvItems(parent.cartItems)
                setRecycledViewPool(viewPool)
            }
        }
    }
}