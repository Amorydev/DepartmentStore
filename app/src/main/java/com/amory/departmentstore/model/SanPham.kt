package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SanPham (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Float,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("category_id") val categoryId: LoaiSanPham
):Serializable