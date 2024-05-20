package com.amory.departmentstore.model

data class LoaiSanPhamModel(
    val status:String,
    val message:String,
    val data: MutableList<LoaiSanPham>
)