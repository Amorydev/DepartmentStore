package com.amory.departmentstore.model

data class Voucher(
    var id:Int,
    var code:String,
    var discountType:String,
    var discountValue:Double
)
