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

@Composable
fun RouteSummaryView(
    distance: Double, // meter 단위
    duration: Int, // millisecond 단위
    pathPoints: List<LatLng>,
    address: String?,
    modifier: Modifier = Modifier
) {
    val km = distance / 1000.0
    val durationSecTotal = duration / 1000
    val durationMin = durationSecTotal / 60
    val durationSec = durationSecTotal % 60

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
                Text("예상 시간: ${durationMin}분 ${durationSec}초")
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