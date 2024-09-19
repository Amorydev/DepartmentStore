package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SanPham(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val categoryId: LoaiSanPham,
    val soldQuantity:Int
) : Serializable