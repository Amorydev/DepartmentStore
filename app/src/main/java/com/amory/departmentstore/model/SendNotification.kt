package com.amory.departmentstore.model

data class SendNotification(
     var to:String,
     var notification:Map<String,String>
)
