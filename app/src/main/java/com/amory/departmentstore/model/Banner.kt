package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Banner(
    @SerializedName("id") val id:Int,
    @SerializedName("imageUrl") val imageUrl:String,
    @SerializedName("name") val name:String,
    @SerializedName("description") val description:String
) : Serializable
