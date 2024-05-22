package com.amory.departmentstore.model

data class Order(
    val product_id:Int,
    val price:Double,
    val number_of_products:Int,
    val total_money:Double
)
