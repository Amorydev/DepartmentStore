package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutVoucherBinding
import com.amory.departmentstore.model.EventBus.SuaXoaMaGiamGiaEvent
import com.amory.departmentstore.model.Voucher
import org.greenrobot.eventbus.EventBus
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class RvVoucherAdmin(val list: MutableList<Voucher>): RecyclerView.Adapter<RvVoucherAdmin.viewHolder>() {
    inner class viewHolder(private val binding: LayoutVoucherBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnCreateContextMenuListener {
        @SuppressLint("SetTextI18n")
        fun bind(data: Voucher) {
            if (data.discountType == "percent") {
                binding.txtGiamgia.text = "Nhập mã \"${data.code}\" để giảm ${data.discountValue} %"
            } else {
                binding.txtGiamgia.text = "Nhập mã \"${data.code}\" để giảm ${formatAmount(data.discountValue)}"
            }
            binding.txtDieukien.text = "Đơn tối thiểu ${formatAmount(data.term)}"
            binding.txtHsd.text = "HSD: ${formatDateTime(data.expirationDate)}"
            if (isAfterOrEqualSpecificDate(data.expirationDate)){
                binding.txtHethan.visibility = View.VISIBLE
            }else{
                binding.txtHethan.visibility = View.INVISIBLE
            }
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
    fun isAfterOrEqualSpecificDate(dateString:String): Boolean {

        val currentDateTime = LocalDateTime.now()

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val compareDateTime = LocalDateTime.parse(dateString, formatter)
        return currentDateTime.isAfter(compareDateTime) || currentDateTime.isEqual(compareDateTime)
    }

    fun formatDateTime(input: String): String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = LocalDateTime.parse(input, formatter.withZone(ZoneOffset.UTC))
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return dateTime.format(outputFormatter)
    }

    fun formatAmount(amount: Double): String {
        val number = amount.toLong()
        val fomart = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${fomart.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvVoucherAdmin.viewHolder {
        val view = LayoutVoucherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnLongClickListener {
            val x = it.width / 2f
            val y = it.height / 2f
            it.showContextMenu(x, y)
            EventBus.getDefault().postSticky(SuaXoaMaGiamGiaEvent(list[position]))
            true
        }
    }
}