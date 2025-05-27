package com.example.teamproject8.NEYfile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.teamproject8.R
import kotlinx.coroutines.coroutineScope

class SchedulerWorker(context: Context, params:WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "notify_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 생성 (Android 8 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

    }

}