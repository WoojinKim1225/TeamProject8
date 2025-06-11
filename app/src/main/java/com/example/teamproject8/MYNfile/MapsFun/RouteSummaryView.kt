package com.example.teamproject8.MYNfile.MapsFun

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.PathOverlay
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RouteSummaryView(   // 길찾기 후 Card 출력
    distance: Double,
    duration: Int,
    pathPoints: List<LatLng>,
    address: String?,
    arrivalTime: LocalDateTime? = null,
    modifier: Modifier = Modifier
) {
    val km = distance / 1000.0  // 거리
    val durationHour = duration / 3600  // 시
    val durationMin = (duration % 3600) / 60    // 분

    // 현재 시간 가져오는 함수
    fun formatTimes(duration: Int): Pair<String, String> {
        val zoneId = ZoneId.of("Asia/Seoul")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val nowZoned = ZonedDateTime.now(zoneId)    // 현재 시간
        val arrivalZoned = nowZoned.plusSeconds(duration.toLong())  // 예상 도착 시간

        val nowStr = nowZoned.format(timeFormatter)
        val arrivalStr = arrivalZoned.format(timeFormatter)

        return nowStr to arrivalStr
    }

    val (currentTimeStr, arrivalTimeStr) = formatTimes(duration)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("경로 요약 정보", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                address?.let {
                    Text("목적지 주소: $it")
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text("거리: %.2f km".format(km))
                Text("예상 시간: ${durationHour}시간 ${durationMin}분")

                Spacer(modifier = Modifier.height(8.dp))
                Text("출발 시간: $currentTimeStr")
                Text("예상 도착 시간: $arrivalTimeStr")

//                arrivalTime?.let {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text("예상 도착 시간: ${it.format(timeFormatter)}")
//                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f)
            ) {
                MiniRouteMapView(pathPoints = pathPoints)
            }
        }
    }
}

// 미니맵 UI 컴포저블(Compose에서 네이버 지도 작게 출력)
@Composable
fun MiniRouteMapView(pathPoints: List<LatLng>) {
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
        modifier = Modifier.fillMaxSize()
    )
}

// MapView 생명주기 관리
@Composable
fun rememberMapViewWithLifecycle(context: Context): MapView {
    val mapView = remember { MapView(context) }
    DisposableEffect(Unit) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }
    return mapView
}