package com.amory.departmentstore.model

data class ProductResponse(
    val status:String,
    val message:String,
    val data: MutableList<Product>
)