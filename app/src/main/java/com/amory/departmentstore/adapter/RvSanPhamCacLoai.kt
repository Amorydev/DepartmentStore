package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.OnClickSanPhamTheoLoai
import com.amory.departmentstore.model.SanPham
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class RvSanPhamCacLoai(private var ds_gao:MutableList<SanPham>,private val onClickSanPhamTheoLoai: OnClickSanPhamTheoLoai): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mcontext:Context
    inner class viewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
        val txtTenSanPhamCacLoai = itemView.findViewById<TextView>(R.id.tensanphamtheoloai)
        val txtGiaSanPhamCacLoai = itemView.findViewById<TextView>(R.id.giasanphamtheoloai)
        val imvHinhAnhSanPhamCacLoai = itemView.findViewById<ImageView>(R.id.imvhinhanhloaisanpham)
    }
    inner class LoadingViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    //chuyen sang dinh dang 000.000d
    fun formatAmount(amount: String): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addData(dataView : List<SanPham>){
        this.ds_gao.addAll(dataView)
        notifyDataSetChanged()
    }
    fun addLoadingView(){
        android.os.Handler().post {
            ds_gao.add(SanPham("","","",""))
            notifyItemInserted(ds_gao.size-1)
        }
    }
    fun removeLoadingView(){
        if (ds_gao.size != 0){
            ds_gao.removeAt(ds_gao.size-1)
            notifyItemRemoved(ds_gao.size-1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_sanpham_theoloai, parent, false)
            viewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_loading, parent, false)
            LoadingViewHolder(view)
        }
    }


    override fun getItemCount(): Int {
        return ds_gao.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val sanPhamTheoLoaiViewHolder = holder as RvSanPhamCacLoai.viewHolder
            sanPhamTheoLoaiViewHolder.txtTenSanPhamCacLoai.text = ds_gao[position].tensanpham
            sanPhamTheoLoaiViewHolder.txtGiaSanPhamCacLoai.text = formatAmount(ds_gao[position].giasanpham)
            Glide.with(mcontext).load(ds_gao[position].hinhanh).centerCrop()
                .into(sanPhamTheoLoaiViewHolder.imvHinhAnhSanPhamCacLoai)

        }
        holder.itemView.setOnClickListener {
            onClickSanPhamTheoLoai.onClickSanPhamTheoLoai(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (ds_gao[position].tensanpham.isEmpty()) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
    }
}