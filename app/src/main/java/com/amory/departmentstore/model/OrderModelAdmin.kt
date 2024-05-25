package com.amory.departmentstore.model

data class OrderModelAdmin (
    val status:String,
    val message:String,
    val data: MutableList<OrderRespone>
    )