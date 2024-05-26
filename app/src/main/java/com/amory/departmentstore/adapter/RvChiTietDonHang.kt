package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.activity.ThongTinDonHangActivity
import com.amory.departmentstore.model.EventBus.ThongTinDonHangEvent
import com.amory.departmentstore.model.OrderRespone
import org.greenrobot.eventbus.EventBus


class RvChiTietDonHang(private val parents: MutableList<OrderRespone>?, private val txtTinhTrang: String) : RecyclerView.Adapter<RvChiTietDonHang.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()
    private lateinit var mContext:Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_parent_items, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parents!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent = parents!![position]
        holder.bind(parent,txtTinhTrang)
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext,ThongTinDonHangActivity::class.java)
            mContext.startActivity(intent)
            EventBus.getDefault().postSticky(ThongTinDonHangEvent(parents[position]))
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_parent_items)
        private val txt: TextView = itemView.findViewById(R.id.txt_hienthi)

        @SuppressLint("SetTextI18n")
        fun bind(parent: OrderRespone, txtTinhTrang:String) {
            txt.text = txtTinhTrang
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RvItems(parent.cartItems)
                setRecycledViewPool(viewPool)
            }
        }

    }
}