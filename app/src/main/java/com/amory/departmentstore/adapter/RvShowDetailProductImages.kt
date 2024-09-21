package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickItemInRvImages
import com.amory.departmentstore.databinding.LayoutRvProductImagesBinding
import com.bumptech.glide.Glide

class RvShowDetailProductImages(
    private val listImages: List<String>,
    private val onClickItemInRvImages: OnClickItemInRvImages
) : RecyclerView.Adapter<RvShowDetailProductImages.ViewHolder>() {

    inner class ViewHolder(private val binding: LayoutRvProductImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            Glide.with(binding.root)
                .load(data)
                .fitCenter()
                .into(binding.imvProductImages)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutRvProductImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listImages[position])
        holder.itemView.setOnClickListener {
            onClickItemInRvImages.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return listImages.size
    }
}
