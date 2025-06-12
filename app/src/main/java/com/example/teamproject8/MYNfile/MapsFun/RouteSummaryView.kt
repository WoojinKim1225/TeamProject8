package com.example.teamproject8.MYNfile.MapsFun

import android.content.Context
import android.graphics.Color
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
fun RouteSummaryView(
    distance: Double,   // 총거리
    duration: Int,      // 길찾기 소요 시간
    pathPoints: List<LatLng>,   // 경로(위도 경도의 List 형태)
    mode: String,       // 교통수단(대중교통, 자동차, 자전거, 걷기)
    startName: String,  // 출발지
    endName: String,    // 도착지
    startLatLng: LatLng,    // 시작 좌표(위도, 경도)
    endLatLng: LatLng,      // 도착 좌표(위도, 경도)
    departureTime: LocalDateTime? = null,   // 출발 시간
    arrivalTime: LocalDateTime? = null,     // 도착 시간
    modifier: Modifier = Modifier
) {
    val km = distance / 1000.0                  // 총거리(km)
    val durationHour = duration / 3600          // 소요 시간(시)
    val durationMin = (duration % 3600) / 60    // 소요 시간(분)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

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
                Spacer(modifier = Modifier.height(12.dp))

                // 출발지 및 도착지 이름
                Text("출발지: ${startName.ifBlank { "현재 위치" }}")
                Text("도착지: ${endName}")
                Spacer(modifier = Modifier.height(8.dp))

                // 시작/도착 좌표
                Text("시작 좌표: ${"%.6f, %.6f".format(startLatLng.latitude, startLatLng.longitude)}")
                Text("도착 좌표: ${"%.6f, %.6f".format(endLatLng.latitude, endLatLng.longitude)}")
                Spacer(modifier = Modifier.height(8.dp))

                // 교통 수단 및 거리 및 시간
                when (mode) {
                    "transit" -> Text("교통 수단: 대중교통")
                    "driving" -> Text("교통 수단: 자동차")
                    "bicycling" -> Text("교통 수단: 자전거")
                    "walking" -> Text("교통 수단: 걷기")
                }
                Text("거리: %.2f km".format(km))
                Text("예상 소요 시간: ${durationHour}시간 ${durationMin}분")
                Spacer(modifier = Modifier.height(8.dp))

                // 출발 시간
                departureTime?.let {
                    Text("출발 시간: ${it.format(formatter)}")
                } ?: run {
                    val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                    Text("출발 시간: ${now.format(formatter)}")
                }

                // 도착 시간
                arrivalTime?.let {
                    Text("도착 시간: ${it.format(formatter)}")
                } ?: run {
                    val arrival = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusSeconds(duration.toLong())
                    Text("도착 시간: ${arrival.format(formatter)}")
                }
            }
            Spacer(modifier = Modifier.width(12.dp))

            // 우측에 있는 미니맵 Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f)
            ) {
                // 경로가 있는 미니맵 출력(인자로 경로 넘겨줌)
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