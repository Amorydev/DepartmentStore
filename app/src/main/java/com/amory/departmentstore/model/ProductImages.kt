package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName

data class ProductImages(
    val createdAt: String = "",
    val updatedAt: String = "",
    @SerializedName("image_url_1") val imageUrl1: String = "",
    @SerializedName("image_url_2") val imageUrl2: String = "",
    @SerializedName("image_url_3") val imageUrl3: String = ""
)