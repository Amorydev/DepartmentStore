package com.amory.departmentstore.model

data class CategoryModel(
    val status:String,
    val message:String,
    val data: MutableList<Category>
)