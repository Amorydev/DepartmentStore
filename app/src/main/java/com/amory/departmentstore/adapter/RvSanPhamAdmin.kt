package com.amory.departmentstore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.Constant
import com.amory.departmentstore.model.EventBus.SuaXoaEvent
import com.amory.departmentstore.model.SanPham
import com.amory.departmentstore.viewModel.OnCLickButtonSanPham
import com.amory.departmentstore.viewModel.OnClickRvSanPham
import com.bumptech.glide.Glide
import org.greenrobot.eventbus.EventBus
import java.text.NumberFormat
import java.util.Locale

class RvSanPhamAdmin(private var ds: MutableList<SanPham>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var mcontext: Context

    inner class SanPhamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {
        val tensanpham: TextView = itemView.findViewById(R.id.txt_tensanpham)
        val giasanpham: TextView = itemView.findViewById(R.id.txtgiasanpham)
        val hinhanhsanpham: ImageView = itemView.findViewById(R.id.img_sanpham)


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

    //chuyen sang dinh dang 000.000d
    private fun formatAmount(amount: String): String {
        if (amount.isEmpty()) {
            return ""
        }
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.layout_sanpham_admin, parent, false)
        return SanPhamViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            val sanPhamViewHolder = holder as SanPhamViewHolder
            sanPhamViewHolder.tensanpham.text = ds[position].name
            sanPhamViewHolder.giasanpham.text = formatAmount(ds[position].price)
            Glide.with(mcontext).load(ds[position].image_url).centerCrop()
                .into(sanPhamViewHolder.hinhanhsanpham)

            sanPhamViewHolder.itemView.setOnLongClickListener {
                val x = it.width / 2f
                val y = it.height / 2f
                it.showContextMenu(x, y)
                EventBus.getDefault().postSticky(SuaXoaEvent(ds[position]))
                true
            }

        }
    }

}

