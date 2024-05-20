package com.amory.departmentstore.model

import java.io.Serializable

data class LoaiSanPham(
    val id:Int,
    val name:String,
    val imageUrl:String
):Serializable