package com.amory.departmentstore.model

data class RevenueByCategoriesModel(
    val status:String,
    val message:String,
    val data:MutableList<RevenueByCategoriesResponse>
)
