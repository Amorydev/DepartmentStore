package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickRvVoucher
import com.amory.departmentstore.databinding.LayoutVoucherBinding
import com.amory.departmentstore.model.Voucher
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class RvVouvher(val ds:MutableList<Voucher>, private val onClickVoucher : OnClickRvVoucher): RecyclerView.Adapter<RvVouvher.viewHolder>() {
    inner class viewHolder(private val binding: LayoutVoucherBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Voucher){
            if (data.discountType == "percent"){
                binding.txtGiamgia.text = "Nhập mã \"${data.code}\" để giảm ${data.discountValue} %"
            }else{
                binding.txtGiamgia.text = "Nhập mã \"${data.code}\" để giảm ${formatAmount(data.discountValue)}"
            }
            binding.txtDieukien.text = "Đơn tối thiểu ${formatAmount(data.term)}"
            binding.txtHsd.text = "HSD: ${formatDateTime(data.expirationDate)}"
            binding.txtHethan.visibility = View.INVISIBLE
        }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutVoucherBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
        holder.itemView.setOnClickListener {
            onClickVoucher.onClickVoucher(position)
        }
    }
}