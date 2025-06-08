package com.example.week15

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun NaverMapWithRouteView(
    modifier: Modifier = Modifier,
    current: LatLng,
    destination: LatLng,
    clientId: String,
    clientSecret: String,
    onSummaryReady: (Summary) -> Unit,
    onPathPointsReady: (List<LatLng>) -> Unit
) {
    val context = LocalContext.current

    val mapView = remember { MapView(context) }
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }
    var pathOverlay by remember { mutableStateOf<PathOverlay?>(null) }

    var pathPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://maps.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val service = remember { retrofit.create(NaverMapApiService::class.java) }

    LaunchedEffect(current, destination) {
        try {
            val startStr = "${current.longitude},${current.latitude}"
            val goalStr = "${destination.longitude},${destination.latitude}"

            val response = service.getRoute(
                clientId = clientId,
                clientSecret = clientSecret,
                start = startStr,
                goal = goalStr
            )

            val coords = mutableListOf<LatLng>()
            val traoptimal = response.route?.traoptimal?.firstOrNull()

            traoptimal?.path?.let { pathList ->
                for (point in pathList) {
                    coords.add(LatLng(point[1], point[0]))
                }
            }
            pathPoints = coords
            onPathPointsReady(coords) // 여기서 콜백 호출

            traoptimal?.summary?.let { summary ->
                onSummaryReady(summary)
            }

        } catch (e: Exception) {
            // error handling (생략)
        }
    }

    LaunchedEffect(naverMap, pathPoints) {
        if (naverMap != null && pathPoints.isNotEmpty()) {
            pathOverlay?.setMap(null)
            pathOverlay = PathOverlay().apply {
                coords = pathPoints
                color = android.graphics.Color.BLUE
                width = 15
                setMap(naverMap)
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = {
                mapView.apply {
                    getMapAsync { map ->
                        naverMap = map

                        Marker().apply {
                            position = current
                            captionText = "현재 위치"
                            setMap(map)
                        }
                        Marker().apply {
                            position = destination
                            captionText = "목적지"
                            setMap(map)
                        }

                        map.moveCamera(CameraUpdate.scrollTo(current))
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color.White.copy(alpha = 0.8f))
                    .padding(8.dp)
            )
        }
    }
}
