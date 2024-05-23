package com.amory.departmentstore.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.activity.ChatAdminActivity
import com.amory.departmentstore.databinding.LayoutItemUserBinding
import com.amory.departmentstore.model.User

class RvAdminChatUser(val list:ArrayList<User>):RecyclerView.Adapter<RvAdminChatUser.viewHolder>() {
    lateinit var mcontext:Context
    inner class viewHolder(private val binding:LayoutItemUserBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data:User){
            val name = data.firstName + " "+ data.lastName
            binding.txtUsername.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        mcontext = parent.context
        val view = LayoutItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val name = list[position].firstName + " "+ list[position].lastName
            val intent = Intent(mcontext,ChatAdminActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id",list[position].id)
            intent.putExtra("username",name)
            mcontext.startActivity(intent)
        }
    }
}