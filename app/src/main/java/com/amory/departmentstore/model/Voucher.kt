package com.amory.departmentstore.model

import java.io.Serializable

data class Voucher(
    var id:Int,
    var code:String,
    var discountType:String,
    var discountValue:Double,
    val term:Double,
    val expirationDate:String
):Serializable
