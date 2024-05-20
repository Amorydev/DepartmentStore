package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)