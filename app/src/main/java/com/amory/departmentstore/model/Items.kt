package com.amory.departmentstore.model

data class Items(
    val product_id: Int,
    val number_of_products:Int,
    val price:Long,
    val image_url:String,
    val name:String
)
