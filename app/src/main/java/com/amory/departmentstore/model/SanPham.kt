package com.amory.departmentstore.model

import java.io.Serializable

data class SanPham (
    val id:Int,
    val image_url: String,
    val name: String,
    val price: String,
    val category_id:Int,
    val description: String
):Serializable