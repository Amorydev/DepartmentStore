package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.ItemRevenueProductsBinding
import com.amory.departmentstore.model.RevenueByProductResponse
import java.text.NumberFormat
import java.util.Locale

class RevenueByProductAdapter(private val list:MutableList<RevenueByProductResponse>):RecyclerView.Adapter<RevenueByProductAdapter.viewHolder>(){
    inner class viewHolder(private val binding:ItemRevenueProductsBinding):RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: RevenueByProductResponse){
            binding.txtName.text = data.product.name
            binding.txtTotalMoney.text = formatAmount(data.totalRevenue.toFloat())
            binding.txtQuantity.text = "SL: ${data.product.soldQuantity}"
        }
    }
    private fun formatAmount(amount: Float): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = ItemRevenueProductsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(list[position])
    }

}
