@file:Suppress("DEPRECATED_ANNOTATION")

package com.amory.departmentstore.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonPropertyOrder(*["product_id", "price", "quantity", "total_money"])
data class Order(
    @SerializedName("product_id") val product_id: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("total_money") val total_money: Double
): Parcelable
