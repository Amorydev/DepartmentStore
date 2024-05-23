package com.amory.departmentstore.model

data class OrderRequest(
    val user_id: Int?,
    val full_name: String,
    val email: String,
    val phone: String,
    val address: String,
    val note: String,
    val total_money: Float,
    val payment_method: String,
    val items: String
)
