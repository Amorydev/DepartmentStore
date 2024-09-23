package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.model.Product
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.amory.departmentstore.R
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.Interface.OnClickRvSanPham
import com.amory.departmentstore.model.LoaiSanPham

class RvSanPham(private val onClickRvSanPham: OnClickRvSanPham) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private lateinit var mcontext: Context
    private var ds: MutableList<Product> = mutableListOf()

    inner class SanPhamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tensanpham: TextView = itemView.findViewById(R.id.txt_tensanpham)
        val giasanpham: TextView = itemView.findViewById(R.id.txtgiasanpham)
        val soluong: TextView = itemView.findViewById(R.id.txt_soluongdaban)
        val hinhanhsanpham: ImageView = itemView.findViewById(R.id.img_sanpham)
    }
    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    //chuyen sang dinh dang 000.000d
    private fun formatAmount(amount: Double): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addData(dateView: List<Product>) {
        this.ds.addAll(dateView)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        //Thêm loading
        Handler().post {
            ds.add(Product(0, "", 1000.0,"","","","", LoaiSanPham(0,"",""),0))
            notifyItemInserted(ds.size - 1)
        }
    }

    fun removeLoadingView() {
        //Xóa loading
        if (ds.size != 0) {
            ds.removeAt(ds.size - 1)
            notifyItemRemoved(ds.size-1)
        }
    }
    fun updateList(newList: List<Product>) {
        val diffCallback = RvSanPhamDiffCallback(ds, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        ds.clear()
        ds.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_sanpham, parent, false)
            SanPhamViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (ds[position].name.isEmpty()) Constant.VIEW_TYPE_LOADING else Constant.VIEW_TYPE_ITEM
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val sanPhamViewHolder = holder as SanPhamViewHolder
            sanPhamViewHolder.tensanpham.text = ds[position].name
            sanPhamViewHolder.giasanpham.text = formatAmount(ds[position].price)
            Glide.with(mcontext).load(ds[position].thumbnail).into(sanPhamViewHolder.hinhanhsanpham)
            sanPhamViewHolder.soluong.text = "Đã bán ${ds[position].soldQuantity.toString()}"
        }
        holder.itemView.setOnClickListener {
            onClickRvSanPham.onClickSanPham(position)
        }
    }

}




