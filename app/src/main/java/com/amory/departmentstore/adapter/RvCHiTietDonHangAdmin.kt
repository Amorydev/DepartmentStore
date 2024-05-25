package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.EventBus.DonHangEvent
import com.amory.departmentstore.model.OrderRespone
import org.greenrobot.eventbus.EventBus

class RvCHiTietDonHangAdmin(private val parents: MutableList<OrderRespone>?) : RecyclerView.Adapter<RvCHiTietDonHangAdmin.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_quanly_donhang_admin, parent, false)
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
        private val txtUser:TextView = itemView.findViewById(R.id.txt_user)
        private val txtTinhTrang:TextView = itemView.findViewById(R.id.txt_tinhtrang)
        private val btn:Button = itemView.findViewById(R.id.btn_capNhatTinhTrang)
        @SuppressLint("SetTextI18n")
        fun bind(parent: OrderRespone) {
            txtUser.text = parent.email
            txtTinhTrang.text = parent.status
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RvItems(parent.cartItems)
                setRecycledViewPool(viewPool)
            }
            btn.setOnClickListener {
                EventBus.getDefault().postSticky(DonHangEvent(parent))
            }
        }
    }
}