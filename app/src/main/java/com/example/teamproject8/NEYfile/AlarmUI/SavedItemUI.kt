package com.example.teamproject8.NEYfile.AlarmUI

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.teamproject8.MYNfile.MapsFun.MiniRouteMapView
import com.example.teamproject8.MYNfile.MapsFun.rememberMapViewWithLifecycle
import com.example.teamproject8.MainActivity
import com.example.teamproject8.NEYfile.WorkManager.ScheduleRequest
import com.example.teamproject8.R
import com.example.teamproject8.WJKfile.RoomDB.NavigationDatabase
import com.example.teamproject8.WJKfile.RoomDB.NavigationEntity
import com.example.week13.makeNotification
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun SavedItemUI(item: NavigationEntity, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val appcontext = context.applicationContext

    val titleIcon: Int = R.drawable.baseline_access_alarm_24
    val locScript = item.origin + " ️-> " + item.destination
    val timeScript = "${item.departureTime.toString()} -> ${item.arrivalTime.toString()}"

    val db = NavigationDatabase.getDBInstance(context)
    val dao = db.getItemDao()
    val coroutinescope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    var ischecked by remember { mutableStateOf(item.doWork)}

    Card(
        modifier = Modifier.clickable { expanded = !expanded }
    ) {
        if (expanded) {
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

                Spacer(modifier = Modifier.height(5.dp))
                MiniRouteMapView2(item.pathPoints, modifier = Modifier.fillMaxWidth().height(120.dp))       //minimap 출력
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    timeScript,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val title: String = "${item.origin} -> ${item.destination}"
                    val message: String =
                        "${item.departureTime} 출발 시 ${item.arrivalTime}에 도착 예정입니다."

                    val intent = Intent(appcontext, MainActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        appcontext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    Switch(
                        checked = ischecked,
                        onCheckedChange = { checked ->
                            ischecked = checked
                            item.doWork = checked

                            val currentTime: LocalDateTime = LocalDateTime.now()

                            if (item.departureTime!! < currentTime) {
                                Toast.makeText(
                                    context,
                                    "현재 시간 기준으로 설정이 불가능한 알림 입니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                                ischecked = false
                                item.doWork = false
                            } else {
                                if (ischecked == false) {
                                    coroutinescope.launch {
                                        dao.UpdateItem(item)
                                    }
                                    Toast.makeText(context, "알림이 해제되었습니다", Toast.LENGTH_SHORT)
                                        .show()
                                    ScheduleRequest.CancelWorkManager(
                                        context = context,
                                        tag = item.id.toString()
                                    )       //Workmanager 동작 중지 명령
                                } else {
                                    item.alarmTime = item.departureTime!!.minusMinutes(60)
                                    if (item.alarmTime!! > item.departureTime) {
                                        item.alarmTime = item.departureTime!!.minusMinutes(30)
                                        if (item.alarmTime!! > item.departureTime) {
                                            Toast.makeText(
                                                context,
                                                "현재 시간 기준으로 설정이 불가능한 알림 입니다",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            ischecked = false
                                            item.doWork = false
                                        }
                                    }
                                    coroutinescope.launch {
                                        dao.UpdateItem(item)
                                    }
                                    Toast.makeText(context, "알림이 설정되었습니다", Toast.LENGTH_SHORT)
                                        .show()
                                    ScheduleRequest.CancelWorkManager(
                                        context = context,
                                        tag = item.id.toString()
                                    )       //Workmanager 초기화
                                    makeNotification(
                                        appcontext,
                                        title,
                                        message,
                                        item.id,
                                        pendingIntent
                                    )
                                    ScheduleRequest.DBWorkManager(
                                        appcontext,
                                        item.alarmTime!!,
                                        item.id,
                                        item.id.toString()
                                    )
                                }
                            }
                        }
                    )
                }
            }
        } else {
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
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    timeScript,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }

        }
    }
}

@Composable
fun MiniRouteMapView2(pathPoints: List<LatLng>, modifier: Modifier) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync { map ->
                    naverMap = map
                    map.uiSettings.apply {
                        isZoomControlEnabled = false
                        isScrollGesturesEnabled = false
                        isTiltGesturesEnabled = false
                        isRotateGesturesEnabled = false
                        isLocationButtonEnabled = false
                    }

                    if (pathPoints.isNotEmpty()) {
                        PathOverlay().apply {
                            coords = pathPoints
                            color = Color.BLUE
                            width = 8
                            setMap(map)
                        }

                        val bounds = LatLngBounds.from(pathPoints.first(), pathPoints.last())
                        map.moveCamera(CameraUpdate.fitBounds(bounds, 40))
                    }
                }
            }
        },
        modifier = modifier
    )
}