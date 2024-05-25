package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickHuyDonHang
import com.amory.departmentstore.R
import com.amory.departmentstore.model.OrderRespone
import com.amory.departmentstore.model.UpdateOrderModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallDonHang
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RvChiTietDonHang(private val parents: MutableList<OrderRespone>?, private val txtTinhTrang: String) : RecyclerView.Adapter<RvChiTietDonHang.ViewHolder>() {

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
        holder.bind(parent,txtTinhTrang)
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