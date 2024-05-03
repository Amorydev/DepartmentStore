package com.amory.departmentstore.model

class NotificationReponse(
    val multicast_id:Long,
    val success:Int,
    val failure:Int,
    val canonical_ids:Int,
) {
}