package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName

data class User(
    var id: Int,
    var first_name: String,
    var last_name: String,
    var email: String,
    var password: String,
    val role:Role
)