package com.example.teamproject8.NEYfile.AlarmUI

import android.app.PendingIntent
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamproject8.MYNfile.MapsFun.MiniRouteMapView
import com.example.teamproject8.MainActivity
import com.example.teamproject8.NEYfile.WorkManager.ScheduleRequest
import com.example.teamproject8.WJKfile.RoomDB.NavigationEntity
import com.example.week13.makeNotification

@Composable
fun SavedItemUI(item: NavigationEntity, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val appcontext = context.applicationContext

    val titleIcon: Int = item.icon

    val locScript = item.origin + " ️-> " + item.destination
    val timeScript = "${item.departureTime.toString()} -> ${item.arrivalTime.toString()}"
    Card {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(titleIcon),
                    contentDescription = "",
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    locScript,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            MiniRouteMapView(item.parhPoints)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                timeScript,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val title: String = "${item.origin} -> ${item.destination}"
                val message: String = "${item.departureTime} 출발 시 ${item.arrivalTime}에 도착 예정입니다."


                Button(onClick = {
                    val intent = Intent(appcontext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        appcontext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    item.alarmTime = item.departureTime!!.minusMinutes(60)

                    makeNotification(appcontext, title, message, item.id , pendingIntent)
                    ScheduleRequest.DBWorkManager(appcontext, item.alarmTime!!, item.id, item.destination)
                },
                    modifier = Modifier.padding(5.dp)) {
                    Text("Enabled")
                }


            }

        }
    }
}

@Preview
@Composable
private fun SavedItemUI_prev() {

}