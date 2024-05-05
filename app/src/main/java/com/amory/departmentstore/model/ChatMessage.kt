package com.amory.departmentstore.model

import java.util.Date

data class ChatMessage(
    var id_gui: String,
    var id_nhan: String,
    var mess: String,
    var datetime: com.google.firebase.Timestamp?,
    var dateObj: Date
)