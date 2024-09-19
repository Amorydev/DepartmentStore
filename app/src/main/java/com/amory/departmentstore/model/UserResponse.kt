package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("active") val active: Boolean,
    @SerializedName("violated") val violated: Boolean
)
