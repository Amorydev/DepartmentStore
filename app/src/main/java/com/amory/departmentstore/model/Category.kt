package com.amory.departmentstore.model

import java.io.Serializable

data class Category(
    val id:Int,
    val name:String,
    val imageUrl:String
):Serializable