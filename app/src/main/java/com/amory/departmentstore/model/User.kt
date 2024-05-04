package com.amory.departmentstore.model

data class User(
    var id:Int,
    var first_name:String,
    var last_name:String,
    var email:String,
    var password:String,
    var mobiphone:String,
    val uid :String,
    val token:String,
    val role:Int
)