package com.amory.departmentstore.model

data class SanPhamModel(
    val status:String,
    val message:String,
    val data: MutableList<SanPham>
)