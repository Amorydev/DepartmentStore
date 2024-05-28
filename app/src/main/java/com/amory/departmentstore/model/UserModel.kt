package com.amory.departmentstore.model


data class UserModel(
    val status:String,
    val message:String,
    val data:MutableList<UserResponse>
)