package com.amory.departmentstore.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.amory.departmentstore.R
import com.amory.departmentstore.activity.MainActivity
import com.amory.departmentstore.activity.user.ChatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessagingReciver:FirebaseMessagingService() {

    @SuppressLint("RemoteViewLayout")
    private fun customNotification(title:String, body:String): RemoteViews {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.txt_title,title)
        remoteViews.setTextViewText(R.id.txt_body,body)
        return remoteViews
    }
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null){
            showNotification(message.notification?.title,message.notification?.body)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun showNotification(title: String?, body: String?) {
        val chanel_id = "amory"
        val intent = Intent(this,ChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val builder : NotificationCompat.Builder =  NotificationCompat.Builder(applicationContext,chanel_id)
            .setSmallIcon(R.drawable.notifications)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        builder.setContent(customNotification(title!!, body!!))

        val notificationManager:NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChanel = NotificationChannel(chanel_id,"app",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChanel)
        }
        notificationManager.notify(0,builder.build())
    }


}