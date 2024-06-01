package com.amory.departmentstore.model

data class VoucherModel(
    val status:String,
    val message:String,
    val data:MutableList<Voucher>
)
