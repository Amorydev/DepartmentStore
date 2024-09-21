package com.amory.departmentstore.model

import java.io.Serializable

data class ProductImages(
    val status:String,
    val message:String,
    val data: List<DetailProduct>
): Serializable
