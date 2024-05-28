package com.amory.departmentstore.model

data class RevenueByYearModel(
    val status:Int,
    val message:String,
    val data:MutableList<RevenueByYearResponse>
)
