package com.amory.departmentstore.model

data class LoaiSanPhamModel(
    val success: Boolean,
    val message: String,
    val result: MutableList<LoaiSanPham>
) {

}