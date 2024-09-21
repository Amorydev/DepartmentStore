package com.amory.departmentstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.Interface.OnClickItemInRvImages
import com.amory.departmentstore.R
import com.amory.departmentstore.databinding.LayoutRvImagesBinding
import com.bumptech.glide.Glide

class RvShowImagesFromProduct(
    private val listImages: List<String>,
    private val onClickItemInRvImages: OnClickItemInRvImages
) : RecyclerView.Adapter<RvShowImagesFromProduct.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(private val binding: LayoutRvImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: String) {
            Glide.with(binding.root)
                .load(data)
                .fitCenter()
                .into(binding.imvProductImage)

            if (adapterPosition == selectedPosition) {
                binding.cardView.setBackgroundResource(R.drawable.red_boder)
            } else {
                binding.cardView.setBackgroundResource(0)
            }
        }
    }
    fun updateSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutRvImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listImages[position])

        holder.itemView.setOnClickListener {
            onClickItemInRvImages.onClick(position)
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int {
        return listImages.size
    }
}

