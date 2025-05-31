package com.example.teamproject8.NEYfile.WorkManager


import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.teamproject8.MainActivity
import com.example.week13.makeNotification

class DBupdateWorker(context: Context, params:WorkerParameters): Worker(context, params) {      //DBUPdate 관련 함수
    override fun doWork(): Result {
        //DB UPdate Work



        //new Notify
        val workContext = applicationContext
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            workContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        makeNotification(applicationContext, "", "", pendingIntent)

        //ScheduleRequest
        ScheduleRequest.DBWorkManager(workContext, 1, 1, 1, 1)

        return Result.success()
    }

//    private fun showNotification(title: String, message: String) {
//        val channelId = "notify_channel"
//        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // 알림 채널 생성 (Android 8 이상)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(applicationContext, channelId)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(R.drawable.baseline_access_alarm_24)
//            .build()
//
//    }

}