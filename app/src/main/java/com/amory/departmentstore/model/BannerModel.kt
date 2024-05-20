package com.amory.departmentstore.model

data class BannerModel(
    val status:String,
    val message:String,
    val data: MutableList<Banner>
)