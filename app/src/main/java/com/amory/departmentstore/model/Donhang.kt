package com.amory.departmentstore.model

data class Donhang(
    val id:Int,
    val full_name:String,
    val phone:String,
    val address:String,
    val item:MutableList<Items>
)
