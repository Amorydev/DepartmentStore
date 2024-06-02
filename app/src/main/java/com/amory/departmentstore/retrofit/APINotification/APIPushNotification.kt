package com.amory.departmentstore.retrofit.APINotification


import com.amory.departmentstore.model.NotificationReponse
import com.amory.departmentstore.model.SendNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIPushNotification {
    @Headers(
        "Content-Type: application/json; charset=UTF-8",
        "Authorization: key=AAAAqCwybME:APA91bHhmPOGsILNGNNwqAFu_gahUGPHsGxVvlevMMWHdRtB3cpsD8TL6wsIpITLgukp86yLZWgBG4YiZpYTrNpnDGTGKQTX9aJn8zIHEMsVjpJHGG1hr_vekNrXedtGxr-I08jJiPgu"
    )
    @POST("fcm/send")
    fun sendNotification(@Body data: SendNotification): Call<NotificationReponse>
}