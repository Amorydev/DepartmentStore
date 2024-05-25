package com.amory.departmentstore.model

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("note") val note: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_money") val totalMoney: Float,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("cart_items") val cartItems: List<Order>
)