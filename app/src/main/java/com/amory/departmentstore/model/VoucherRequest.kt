package com.amory.departmentstore.model

data class VoucherRequest(
    var code:String,
    var discountType:String,
    var discountValue:String,
    val term:String,
    val expirationDate:String
)