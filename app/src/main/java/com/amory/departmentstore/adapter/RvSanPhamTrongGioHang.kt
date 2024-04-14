package com.amory.departmentstore.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.LayoutChitietGiohangBinding
import com.amory.departmentstore.model.EventBus.TinhTongEvent
import com.amory.departmentstore.model.GioHang
import com.amory.departmentstore.model.SanPham

import com.bumptech.glide.Glide
import org.greenrobot.eventbus.EventBus
import java.text.NumberFormat
import java.util.Locale

class RvSanPhamTrongGioHang(private var ds: MutableList<GioHang>) :
    RecyclerView.Adapter<RvSanPhamTrongGioHang.viewHolder>() {
    inner class viewHolder(private var binding: LayoutChitietGiohangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var gioHang = Utils.manggiohang
        fun bind(data: GioHang) {
            binding.txtTensanphamtronggiohang.text = data.tensanphamgiohang
            binding.soluongsanpham.text = data.soluongsanphamgiohang.toString()
            binding.txtTonggiasanpham.text = formatAmount(data.giasanphamgiohang)
            Glide.with(binding.root).load(data.hinhanhsanphamgiohang)
                .into(binding.imvHinhanhsanphamtrongiohang)

            val position = adapterPosition
            val gioHangItem = gioHang[position]
            binding.txtCongSanpham.setOnClickListener {
                val soluongmoi = gioHangItem.soluongsanphamgiohang + 1
                gioHangItem.soluongsanphamgiohang = soluongmoi

                val tongGiaTriSanPham =
                    gioHangItem.giasanphamgiohang.toLong() / (soluongmoi - 1) * soluongmoi
                gioHangItem.giasanphamgiohang = tongGiaTriSanPham.toString()

                binding.soluongsanpham.text = gioHangItem.soluongsanphamgiohang.toString()
                binding.txtTonggiasanpham.text = formatAmount(gioHangItem.giasanphamgiohang)
                EventBus.getDefault().postSticky(TinhTongEvent())
            }
            binding.txtTruSanpham.setOnClickListener {

                val soluongmoi = gioHangItem.soluongsanphamgiohang - 1
                if (soluongmoi >= 1) {
                    gioHangItem.soluongsanphamgiohang = soluongmoi
                    val tongGiaTriSanPham =
                        gioHangItem.giasanphamgiohang.toLong() / (soluongmoi + 1) * soluongmoi
                    gioHangItem.giasanphamgiohang = tongGiaTriSanPham.toString()
                    binding.soluongsanpham.text = gioHangItem.soluongsanphamgiohang.toString()
                    binding.txtTonggiasanpham.text = formatAmount(gioHangItem.giasanphamgiohang)
                    EventBus.getDefault().postSticky(TinhTongEvent())
                }
            }
            binding.checkboxSanpham.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    Utils.mangmuahang.add(gioHangItem)
                    EventBus.getDefault().postSticky(TinhTongEvent())
                }else{
                    val iterator = Utils.mangmuahang.iterator()
                    while (iterator.hasNext()) {
                        val item = iterator.next()
                        if (item.idsanphamgiohang == gioHangItem.idsanphamgiohang) {
                            iterator.remove()
                        }
                    }
                    EventBus.getDefault().postSticky(TinhTongEvent())
                }
            }
        }

    }


    //dinh dang thanh gia 999.999d
    fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val fomart = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${fomart.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding =
            LayoutChitietGiohangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(ds[position])
    }
}