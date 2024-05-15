package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Utils.Utils
import com.amory.departmentstore.databinding.LayoutSanphamDuocmuaBinding
import com.amory.departmentstore.model.GioHang
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale


class RvMuaNgay(val ds:MutableList<GioHang>):RecyclerView.Adapter<RvMuaNgay.viewHolder>() {
    private var gioHang = Utils.mangmuahang
    inner class viewHolder(private val binding:LayoutSanphamDuocmuaBinding):RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: GioHang){
            binding.txtTensanphammuangay.text = data.tensanphamgiohang
            val position = adapterPosition
            val gioHangItem = gioHang[position]
            val giaGoc = gioHangItem.giasanphamgiohang.toLong() / (gioHangItem.soluongsanphamgiohang)
            binding.txtGiasanpham.text = formatAmount(giaGoc.toString())
            binding.txtSoluong.text = "x "+ data.soluongsanphamgiohang.toString()
            binding.txtTongsoluongsanpham.text = "Tổng tiền(${data.soluongsanphamgiohang} sản phẩm):"
            binding.txtTongtien.text = formatAmount(data.giasanphamgiohang)
            Glide.with(binding.root).load(data.hinhanhsanphamgiohang).fitCenter().into(binding.imvHinhanhsanphammuangay)
        }
    }
    fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val fomart = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${fomart.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
       val view = LayoutSanphamDuocmuaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
    }
}