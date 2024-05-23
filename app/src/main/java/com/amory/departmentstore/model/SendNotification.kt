package com.amory.departmentstore.model

data class SendNotification(
     var token:String,
     var notification:Map<String,String>
)
