package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.Interface.OnCLickButtonSanPham
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.Interface.OnClickSanPhamTheoLoai
import com.amory.departmentstore.model.LoaiSanPham
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class RvSanPhamCacLoai(private var ds:MutableList<SanPham>, private val onClickSanPhamTheoLoai: OnClickSanPhamTheoLoai, private val onCLickButtonSanPham: OnCLickButtonSanPham): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mcontext:Context
    inner class viewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
        val txtTenSanPhamCacLoai = itemView.findViewById<TextView>(R.id.txt_tensanpham)!!
        val txtGiaSanPhamCacLoai = itemView.findViewById<TextView>(R.id.txtgiasanpham)!!
        val imvHinhAnhSanPhamCacLoai = itemView.findViewById<ImageView>(R.id.img_sanpham)!!
    }
    inner class LoadingViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    //chuyen sang dinh dang 000.000d
    private fun formatAmount(amount: Float): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addData(dataView : List<SanPham>){
        this.ds.addAll(dataView)
        notifyDataSetChanged()
    }
    fun addLoadingView(){
        android.os.Handler().post {
            ds.add(SanPham(0,"",100f,"","","","", LoaiSanPham(0,"","")))
            notifyItemInserted(ds.size-1)
        }
    }
    fun removeLoadingView(){
        if (ds.size != 0){
            ds.removeAt(ds.size-1)
            notifyItemRemoved(ds.size-1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_sanpham, parent, false)
            viewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_loading, parent, false)
            LoadingViewHolder(view)
        }
    }


    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val sanPhamTheoLoaiViewHolder = holder as RvSanPhamCacLoai.viewHolder
            sanPhamTheoLoaiViewHolder.txtTenSanPhamCacLoai.text = ds[position].name
            sanPhamTheoLoaiViewHolder.txtGiaSanPhamCacLoai.text = formatAmount(ds[position].price)
            Glide.with(mcontext).load(ds[position].imageUrl).centerCrop()
                .into(sanPhamTheoLoaiViewHolder.imvHinhAnhSanPhamCacLoai)
        }
        holder.itemView.setOnClickListener {
            onClickSanPhamTheoLoai.onClickSanPhamTheoLoai(position)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (ds[position].name.isEmpty()) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
    }
}