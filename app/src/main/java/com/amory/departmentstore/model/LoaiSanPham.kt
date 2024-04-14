package com.amory.departmentstore.model

data class LoaiSanPham(
    val id:Int,
    val name:String,
    val image_url:String,
    val category_id:Int
) {
}