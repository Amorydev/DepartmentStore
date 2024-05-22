package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.R
import com.amory.departmentstore.model.ChatMessage
import com.amory.departmentstore.model.Constant
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RvChatAdapter(val list: ArrayList<ChatMessage>, private val sendId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class NhanMesViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val txt_message = itemview.findViewById<TextView>(R.id.txt_message)
        val txt_date = itemview.findViewById<TextView>(R.id.txt_date)
    }

    inner class GuiMesViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val txt_message = itemview.findViewById<TextView>(R.id.txt_message)
        val txt_date = itemview.findViewById<TextView>(R.id.txt_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constant.VIEW_TYPE_SEND) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_chat_item_gui, parent, false)
            GuiMesViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_chat_item_nhan, parent, false)
            NhanMesViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = list[position]
        when (holder) {
            is GuiMesViewHolder -> {
                holder.txt_message.text = message.mess
                holder.txt_date.text = formatDateTime(message.datetime)
            }

            is NhanMesViewHolder -> {
                holder.txt_message.text = message.mess
                holder.txt_date.text = formatDateTime(message.datetime)
            }
        }
    }

    private fun formatDateTime(datetime: Timestamp?): String {
        if (datetime != null) {
            val instant = Instant.ofEpochSecond(datetime.seconds, datetime.nanoseconds.toLong())
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        return ""
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].id_gui == sendId) Constant.VIEW_TYPE_SEND else Constant.VIEW_TYPE_RECIVE
    }

}