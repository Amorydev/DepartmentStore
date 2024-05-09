package com.amory.departmentstore.model

import java.io.Serializable

data class KhuyenMai(
    val id:Int,
    var image_url:String,
    var khuyenmai:String,
    var thongtin:String
) : Serializable
