package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.LayoutChitietitemsBinding
import com.amory.departmentstore.model.Order
import com.amory.departmentstore.model.SanPhamModel
import com.amory.departmentstore.retrofit.APIBanHang.APICallProducts
import com.amory.departmentstore.retrofit.APIBanHang.RetrofitClient
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class RvItems(private val ds: List<Order>) : RecyclerView.Adapter<RvItems.viewHolder>() {
    inner class viewHolder(private val binding: LayoutChitietitemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: Order) {
            val service = RetrofitClient.retrofitInstance.create(APICallProducts::class.java)
            val callProducts = service.getProductsById(data.product_id)
            callProducts.enqueue(object : retrofit2.Callback<SanPhamModel> {
                override fun onResponse(call: Call<SanPhamModel>, response: Response<SanPhamModel>) {
                    if (response.isSuccessful) {
                        val product = response.body()?.data?.get(0)
                        binding.txtName.text = product?.name
                        Glide.with(binding.root).load(product?.thumbnail).centerCrop().into(binding.imbHinhanh)
                        binding.txtSoluongsanpham1.text = "x ${data.quantity}"
                        binding.txtGiasanpham.text = formatAmount(data.price)
                        binding.txtSoluongsanpham2.text = "${data.quantity} sản phẩm"
                        binding.txtTongtien.text = formatAmount(data.total_money)
                    }
                }
                override fun onFailure(call: Call<SanPhamModel>, t: Throwable) {

                }
            })
        }
    }

    fun formatAmount(amount: Double): String {
        val number = amount.toLong()
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${format.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            LayoutChitietitemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
    }
}
