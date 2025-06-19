package com.example.teamproject8.NEYfile.WorkManager


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.teamproject8.MainActivity
import com.example.teamproject8.WJKfile.RoomDB.NavigationDatabase
import com.example.week13.makeNotification

class DBupdateWorker(context: Context, params:WorkerParameters): CoroutineWorker(context, params) {      //DBUPdate 관련 함수
    override suspend fun doWork(): Result {
        //DB UPdate Work
        val item_id = inputData.getInt("item_id", -1)
        if(item_id == -1){
            return Result.failure()
        }

        //item 가져오기
        val db = NavigationDatabase.getDBInstance(applicationContext)
        val dao = db.getItemDao()

        val item = dao.GetItemById(item_id)

        if (item != null) {
            //depatureTime 계산 로직


            item.alarmTime = item.alarmTime!!.plusMinutes(10)       //다음 Update 10분 뒤

            dao.UpdateItem(item)
        }else {
            return Result.failure()
        }

        //tag 만들기
        val tag:String = item.id.toString()

        //new Notify
        val title: String = "${item.origin} -> ${item.destination}"
        val message: String = "${item.departureTime} 출발 시 ${item.arrivalTime}에 도착 예정입니다."

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
        makeNotification(applicationContext, "", "", item_id, pendingIntent)

        if(item.alarmTime!! < item.departureTime) {
            //ScheduleRequest
            ScheduleRequest.DBWorkManager(workContext, item.alarmTime!!, item_id, tag)
        }
        return Result.success()
    }
}
