package com.example.teamproject8.NEYfile.WorkManager


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.teamproject8.BuildConfig
import com.example.teamproject8.MYNfile.MapsPackage.GoogleDirectionsApiService
import com.example.teamproject8.MainActivity
import com.example.teamproject8.WJKfile.RoomDB.Logs.LogsDatabase
import com.example.teamproject8.WJKfile.RoomDB.Logs.LogsEntity
import com.example.teamproject8.WJKfile.RoomDB.Navigations.NavigationDatabase
import com.example.teamproject8.WJKfile.transit.getArrivalTransitDirection
import com.example.week13.makeNotification
import com.naver.maps.geometry.LatLng
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DBupdateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {      //DBUPdate 관련 함수
    override suspend fun doWork(): Result {
        var tag: String = ""
        val workContext = applicationContext
        val currentTime = LocalDateTime.now()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            workContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //DB UPdate Worker 연동
        val item_id = inputData.getInt("item_id", -1)
        if (item_id == -1) {
            makeNotification(
                workContext,
                "알림 발생 오류",
                "DB를 가져오는 중에 문제가 생겼습니다.",false,
                item_id,
                pendingIntent
            )

            ScheduleRequest.CancelWorkManager(applicationContext, item_id.toString())
            return Result.failure()
        }

        //item 가져오기
        val db = NavigationDatabase.getDBInstance(applicationContext)
        val dao = db.getItemDao()
        val item = dao.GetItemById(item_id)

        //log DB
        val logDb = LogsDatabase.getDBInstance(applicationContext)
        val logDao = logDb.getItemDao()

        //UPDATE DB
        if (item != null) {
            if (item.alarmTime == null) {
                makeNotification(
                    workContext,
                    "알림 발생 오류",
                    "DB 파일에 문제가 생겼습니다.",
                    false,
                    item_id,
                    pendingIntent
                )
                return Result.failure()
            }
            //tag 만들기
            tag = item.id.toString()

            //item.depatureTime 계산 로직
            val arrivalTimeForCalc =
                item.arrivalTime!!.atZone(ZoneId.of("Asia/Seoul")).toEpochSecond().toString()
            Log.d("worker", arrivalTimeForCalc)
            val newDepartTime = returnNewdepartTime(
                item.startLatLng,
                item.endLatLng,
                BuildConfig.GOOGLE_API_KEY,
                arrivalTimeForCalc
            )

            item.alarmTime = item.alarmTime!!.plusMinutes(5)       //다음 Update 10분 뒤
            dao.UpdateItem(item)


            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val departTimeStr = item.departureTime!!.format(formatter)
            val arrivalTimeStr = item.arrivalTime!!.format(formatter)

            if(newDepartTime < item.departureTime!!.minusMinutes(3)){
                val title: String = "${item.origin} -> ${item.destination}"
                val message: String =
                    " $departTimeStr -> $arrivalTimeStr 으로 수정되었습니다."
//                val message2: String = "WorkManager입니다. 다음 계산은 ${item.alarmTime}에 진행됩니다."
//                makeNotification(applicationContext, title, message2, 100, pendingIntent)
                makeNotification(applicationContext, title, message, false, item_id, pendingIntent)
                item.departureTime = newDepartTime
                dao.UpdateItem(item)
            }

//            val title: String = "${item.origin} -> ${item.destination}"
//            val message2: String = "WorkManager입니다. 다음 계산은 ${item.alarmTime}에 진행됩니다."
//            makeNotification(applicationContext, title, message2, 100, pendingIntent)

            //NEW WorkRequest 발생
            if (item.alarmTime!! < item.departureTime!!.minusMinutes(5)) {
                //ScheduleRequest
                ScheduleRequest.DBWorkManager(workContext, item.alarmTime!!, item_id, tag)
            } else {
                val title: String = "${item.origin} -> ${item.destination}"
                val message: String =
                    "지금 출발해야 합니다"
                makeNotification(applicationContext, title, message, true, item_id, pendingIntent)

                val logItem = LogsEntity(
                    id = item.id,
                    title = "",
                    origin = item.origin,
                    destination = item.destination,
                    arrivalTime = item.arrivalTime,
                    isSuccess = true
                )
                logDao.InsertItem(logItem)

                item.arrivalTime = item.arrivalTime!!.plusDays(7)
                item.departureTime = item.departureTime!!.plusDays(7)
                item.arrivalTime = item.departureTime!!.minusMinutes(60)
                dao.UpdateItem(item)
                ScheduleRequest.DBWorkManager(workContext, item.alarmTime!!, item_id, tag)
            }

            Log.d("WorkManager1", "$item_id Worker work")

        } else {
            makeNotification(workContext, "알림 발생 오류", "DB 파일에 문제가 생겼습니다.", false, item_id, pendingIntent)
            return Result.failure()
        }
        return Result.success()
    }
}

suspend fun returnNewdepartTime(
    current: LatLng,
    destination: LatLng,
    googleApiKey: String,
    arrivalTime: String
): LocalDateTime {
    val origin = "${current.latitude},${current.longitude}"
    val dest = "${destination.latitude},${destination.longitude}"

    val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(GoogleDirectionsApiService::class.java)

    val response = getArrivalTransitDirection(
        service = service, origin = origin, dest, "transit", arrivalTime, googleApiKey)

    val leg = response.routes.firstOrNull()?.legs?.firstOrNull()
    val departure = leg?.departure_time
    if (departure == null) {
        Log.e("work", """
        ❗ departure_time 누락됨
        ▶ origin: $origin
        ▶ destination: $dest
        ▶ arrivalTime: $arrivalTime
        ▶ routes.size: ${response.routes.size}
    """.trimIndent())
        throw IllegalStateException("API 응답에서 departure_time이 존재하지 않습니다.")
    }

    return LocalDateTime.ofInstant(
        Instant.ofEpochSecond(departure!!.value),
        ZoneId.of(departure!!.time_zone)
    )
}
