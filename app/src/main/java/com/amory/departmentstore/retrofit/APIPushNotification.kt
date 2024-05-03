package com.amory.departmentstore.retrofit

import android.telecom.Call
import com.amory.departmentstore.model.NotificationReponse
import com.amory.departmentstore.model.SendNotification
import com.amory.departmentstore.model.UserModel
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIPushNotification {
    @Headers(
        *[
            "Content-Type: application/json; charset=UTF-8",
            "Authorization: key=AAAAqCwybME:APA91bG8C9OgpCPhAZcTTytTutWReeeLO2PocUgeYuQLsFwRC_NMKUHXVDovsjKWSSRxIYm-jyAXcFAiOfCIVA23MAzVjMCykiyblVc3Px3gqyH4SPOqTcfG3C4lZS7BO2ksu5KgV-uB"
        ]    )
    @POST ("fcm/send")
    fun sendNotification(@Body data:SendNotification): retrofit2.Call<NotificationReponse>
}