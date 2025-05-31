package com.example.teamproject8.NEYfile

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ScheduleRequest {
    fun DBWorkManager(context: Context, day: Int, hour: Int, minute: Int, second: Int) {
        val curruntTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, second)
            set(Calendar.DAY_OF_YEAR, day)
        }

        val delay = targetTime.timeInMillis - curruntTime.timeInMillis


        val workRequest = OneTimeWorkRequestBuilder<DBupdateWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)


    }
}