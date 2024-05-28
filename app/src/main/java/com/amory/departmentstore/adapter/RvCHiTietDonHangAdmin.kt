package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.EventBus.DonHangEvent
import com.amory.departmentstore.model.OrderRespone
import org.greenrobot.eventbus.EventBus
import java.text.NumberFormat
import java.util.Locale

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
        private val txtMaDonHang:TextView = itemView.findViewById(R.id.txt_madonhang)
        private val txtName:TextView = itemView.findViewById(R.id.txt_name_donhang)
        private val txtPhone:TextView = itemView.findViewById(R.id.txt_phone_donhang)
        private val txtAddress:TextView = itemView.findViewById(R.id.txt_address_donhang)
        private val txtTotal:TextView = itemView.findViewById(R.id.txt_totalMoney_donhang)
        private val txtMethod:TextView = itemView.findViewById(R.id.txt_paymentMethod_donhang)
        private val txtTinhTrang:TextView = itemView.findViewById(R.id.txt_tinhtrang)
        private val imv:ImageView = itemView.findViewById(R.id.imv_capnhattinhtrang)
        @SuppressLint("SetTextI18n")
        fun bind(parent: OrderRespone) {
            txtMaDonHang.text = parent.id.toString()
            txtUser.text = parent.email
            txtName.text = parent.fullName
            txtPhone.text = parent.phone
            txtAddress.text = parent.address
            txtMethod.text = if (parent.paymentMethod.equals("online")) "Đã thanh toán qua ZaloPay" else "Chưa thanh toán"
            txtTinhTrang.text = if(parent.status == "Pending") {
                "Chờ xác nhận"
            }else if(parent.status.equals("Processing")){
                "Đang xử lý"
            }else if(parent.status.equals("Shipped")){
                "Đang giao hàng"
            }else if(parent.status.equals("Delivered")){
                "Giao thành công"
            }else{
                "Đã hủy"
            }
            txtTotal.text = fomartAmount(parent.totalMoney)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RvItems(parent.cartItems)
                setRecycledViewPool(viewPool)
            }
            imv.setOnClickListener {
                EventBus.getDefault().postSticky(DonHangEvent(parent))
            }
        }
    }
    private fun fomartAmount(amount:Float):String{
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }
}