package com.amory.departmentstore.model

import java.io.Serializable

data class LoaiSanPham(
    val id:Int,
    val name:String,
    val image_url:String,
    val category_id:Int
):Serializable