package com.amory.departmentstore.model

data class PromotionModel(
    val status:String,
    val message:String,
    val data: MutableList<Promotion>
)