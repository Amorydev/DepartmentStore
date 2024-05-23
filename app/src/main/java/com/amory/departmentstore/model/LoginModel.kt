package com.amory.departmentstore.model

data class LoginModel(
    val message:String,
    val access_token:String,
    val refresh_token:String
)