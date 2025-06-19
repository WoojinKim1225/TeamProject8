package com.example.teamproject8.NEYfile.WorkManager

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.time.LocalDateTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ScheduleRequest {
    fun DBWorkManager(context: Context, day: Int, AlarmTime:LocalDateTime, item_id_value:Int, tag:String) {
        val curruntTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, AlarmTime.hour)
            set(Calendar.MINUTE, AlarmTime.minute)
            set(Calendar.SECOND, AlarmTime.second)
            set(Calendar.DAY_OF_YEAR, day)
        }

        val delay = targetTime.timeInMillis - curruntTime.timeInMillis

        val item_id = workDataOf("item_id" to item_id_value)

        val workRequest = OneTimeWorkRequestBuilder<DBupdateWorker>()
            .addTag(tag)
            .setInputData(item_id)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun CancelWorkManager(context: Context,tag:String){
        val workRequest = OneTimeWorkRequestBuilder<DBupdateWorker>()
            .addTag(tag)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        // 이후 태그로 전체 취소
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }

}