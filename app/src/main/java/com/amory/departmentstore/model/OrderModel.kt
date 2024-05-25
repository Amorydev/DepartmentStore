package com.amory.departmentstore.model

data class OrderModel(
    val status:String,
    val message:String,
    val data: MutableList<OrderRequest>
)
