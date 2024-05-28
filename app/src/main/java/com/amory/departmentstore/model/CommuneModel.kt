package com.amory.departmentstore.model

data class CommuneModel(
    val status:String,
    val message:String,
    val results:MutableList<Commune>
)
