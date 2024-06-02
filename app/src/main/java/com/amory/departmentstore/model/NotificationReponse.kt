package com.amory.departmentstore.model

data class NotificationReponse(
    val multicast_id:Long,
    val success: Int,
    val failure: Int
)