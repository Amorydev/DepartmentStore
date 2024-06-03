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

class RvChiTietDonHangAdmin(private val orders: List<OrderRespone>?) : RecyclerView.Adapter<RvChiTietDonHangAdmin.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_quanly_donhang_admin, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = orders?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        orders?.get(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_parent_items)
        private val txtUser: TextView = itemView.findViewById(R.id.txt_user)
        private val txtMaDonHang: TextView = itemView.findViewById(R.id.txt_madonhang)
        private val txtName: TextView = itemView.findViewById(R.id.txt_name_donhang)
        private val txtPhone: TextView = itemView.findViewById(R.id.txt_phone_donhang)
        private val txtAddress: TextView = itemView.findViewById(R.id.txt_address_donhang)
        private val txtTotal: TextView = itemView.findViewById(R.id.txt_totalMoney_donhang)
        private val txtMethod: TextView = itemView.findViewById(R.id.txt_paymentMethod_donhang)
        private val txtTinhTrang: TextView = itemView.findViewById(R.id.txt_tinhtrang)
        private val imv: ImageView = itemView.findViewById(R.id.imv_capnhattinhtrang)

        @SuppressLint("SetTextI18n")
        fun bind(order: OrderRespone) {
            txtMaDonHang.text = order.id.toString()
            txtUser.text = order.email
            txtName.text = order.fullName
            txtPhone.text = order.phone
            txtAddress.text = order.address
            txtMethod.text = if (order.paymentMethod == "online") "Đã thanh toán qua VNPay" else "Chưa thanh toán"
            txtTinhTrang.text = getOrderStatus(order.status)
            txtTotal.text = formatAmount(order.totalMoney)

            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = RvItems(order.cartItems)
                setRecycledViewPool(viewPool)
            }

            imv.setOnClickListener {
                EventBus.getDefault().postSticky(DonHangEvent(order))
            }
        }

        private fun getOrderStatus(status: String): String {
            return when (status) {
                "pending" -> "Chờ xác nhận"
                "processing" -> "Đang xử lý"
                "shipped" -> "Đang giao hàng"
                "delivered" -> "Giao thành công"
                else -> "Đã hủy"
            }
        }

        private fun formatAmount(amount: Float): String {
            val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
            return "${formatter.format(amount.toLong())}đ"
        }
    }
}
