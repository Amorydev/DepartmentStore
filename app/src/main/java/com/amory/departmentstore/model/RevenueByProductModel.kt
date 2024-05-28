package com.amory.departmentstore.model

data class RevenueByProductModel(
    val status:String,
    val message:String,
    val data:MutableList<RevenueByProductResponse>
)
