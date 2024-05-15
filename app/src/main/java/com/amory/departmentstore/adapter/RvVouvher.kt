package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickRvVoucher
import com.amory.departmentstore.databinding.LayoutVoucherBinding
import com.amory.departmentstore.model.Voucher
import java.text.NumberFormat
import java.util.Locale

class RvVouvher(val ds:MutableList<Voucher>, private val onClickVoucher : OnClickRvVoucher): RecyclerView.Adapter<RvVouvher.viewHolder>() {
    inner class viewHolder(private val binding: LayoutVoucherBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Voucher){
            if (data.discount_type == "percent"){
                binding.txtGiamgia.text = "Giảm giá ${data.discount_value} %"
            }else{
                binding.txtGiamgia.text = "Giảm giá ${formatAmount(data.discount_value.toLong().toString())}"
            }
        }
    }
    fun formatAmount(amount: String): String {
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