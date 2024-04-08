package com.amory.departmentstore.model

data class SanPhamModel(
    val success: Boolean,
    val message: String,
    val result: MutableList<SanPham>
) {


}
