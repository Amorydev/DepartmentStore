package com.amory.departmentstore.model

data class DistrictModel(
    val status:String,
    val message:String,
    val results:MutableList<District>
)
