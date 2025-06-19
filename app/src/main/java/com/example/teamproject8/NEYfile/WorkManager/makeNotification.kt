package com.example.week13

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.teamproject8.MainActivity
import com.example.teamproject8.R

fun makeNotification(context: Context, title: String,message: String, id:Int, pendingIntent:PendingIntent) {
    Log.d("SavedItemUI", "알림 호출됨")

    val channelId = "ETDChannel"
    val channelName = "ETDChannel"
    val notificationId = id

    val notificationChannel =
        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

    val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(notificationChannel)

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_access_alarm_24)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationManager.IMPORTANCE_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(false)
        .setOngoing(true)
        .build()

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "알림 채널 설명"
        }
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(notificationId, notification)
}

