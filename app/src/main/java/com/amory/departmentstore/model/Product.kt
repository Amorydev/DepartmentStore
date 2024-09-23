package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val thumbnail: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val categoryId: LoaiSanPham,
    val soldQuantity:Int,
    @SerializedName("productImages") val productImages: ProductImages = ProductImages()
) : Serializable