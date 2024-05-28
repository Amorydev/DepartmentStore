package com.amory.departmentstore.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickBlockUser
import com.amory.departmentstore.databinding.ItemUsersBinding
import com.amory.departmentstore.model.UserResponse

class RvQuanLyUser(val dsUser:MutableList<UserResponse>,private val onClickBlockUser: OnClickBlockUser):RecyclerView.Adapter<RvQuanLyUser.viewHolder>() {
    inner class viewHolder(private val binding: ItemUsersBinding):RecyclerView.ViewHolder(binding.root)
    {
        @SuppressLint("SetTextI18n")
        fun bind(data:UserResponse){
            binding.txtUserId.text = data.id.toString()
            binding.txtUserName.text = data.firstName + " "+ data.lastName
            binding.txtUserEmail.text = data.email
            binding.txtTrangthai.text = if (data.active) "Đang hoạt động" else "Đã bị khóa"

            binding.imvBlock.setOnClickListener {
                onClickBlockUser.onClickBlockUser(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = ItemUsersBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return dsUser.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(dsUser[position])
    }

}