package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("user") val user: User
)