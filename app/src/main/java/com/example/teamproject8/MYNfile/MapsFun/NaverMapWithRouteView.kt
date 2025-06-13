package com.example.teamproject8.MYNfile.MapsFun

import android.R.attr.apiKey
import android.util.Log
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
import com.example.teamproject8.MYNfile.MapsPackage.GoogleDirectionsApiService
import com.example.teamproject8.MYNfile.MapsPackage.Summary
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun NaverMapWithRouteView(
    modifier: Modifier = Modifier,
    current: LatLng,
    destination: LatLng,
    googleApiKey: String,
    mode: String,
    departureTime: String,
    onSummaryReady: (Summary) -> Unit,
    onPathPointsReady: (List<LatLng>) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }
    var pathOverlay by remember { mutableStateOf<PathOverlay?>(null) }

    var pathPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(current, destination, mode, departureTime, googleApiKey) {
        try {
            val origin = "${current.latitude},${current.longitude}"
            val dest = "${destination.latitude},${destination.longitude}"

            val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(GoogleDirectionsApiService::class.java)

            val response = withContext(Dispatchers.IO) {
                service.getDirections(
                    origin = origin,
                    destination = dest,
                    mode = "transit",    // mode,
                    departureTime = departureTime,
                    apiKey = googleApiKey
                )
            }

            val route = response.routes.firstOrNull()
            if (route != null) {
                val decodedPath = decodePolyline(route.overview_polyline.points)
                pathPoints = decodedPath
                onPathPointsReady(decodedPath)

                val leg = route.legs.firstOrNull()
                leg?.let {
                    val summary = Summary(
                        distance = it.distance.value,
                        duration = it.duration.value,
                        departureTime = it.departure_time?.let { time ->
                            LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(time.value),
                                ZoneId.of(time.time_zone)
                            )
                        },
                        arrivalTime = it.arrival_time?.let { time ->
                            LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(time.value),
                                ZoneId.of(time.time_zone)
                            )
                        }
                    )
                    onSummaryReady(summary)
                }
            } else {
                errorMessage = "경로를 찾을 수 없습니다."
            }

        } catch (e: Exception) {
            errorMessage = "오류 발생: ${e.message}"
            Log.e("GoogleRoute", "API Error", e)
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

// Google Polyline decoding
fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }

    return poly
}