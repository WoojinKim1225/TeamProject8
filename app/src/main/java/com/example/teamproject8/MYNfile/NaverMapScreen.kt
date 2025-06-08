package com.example.teamproject8.MYNfile

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.teamproject8.MYNfile.MapsFun.DestinationSelector
import com.example.teamproject8.MYNfile.MapsFun.NaverMapWithRouteView
import com.example.teamproject8.MYNfile.MapsFun.RouteSummaryView
import com.example.teamproject8.MYNfile.MapsPackage.Summary
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NaverMapScreen(
    modifier: Modifier = Modifier,
    clientId: String,
    clientSecret: String
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    val granted = permissionState.permissions.any { it.status.isGranted }
    val currentLocation = rememberCurrentLocation()
    val isLoadingLocation = granted && currentLocation == null

    var destination by remember { mutableStateOf<LatLng?>(null) }
    var destinationAddress by remember { mutableStateOf<String?>(null) }
    var summary by remember { mutableStateOf<Summary?>(null) }
    var pathPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var destinationKey by remember { mutableStateOf(System.currentTimeMillis()) }

    when {
        !granted -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("위치 권한이 필요합니다.")
            }
        }

        isLoadingLocation -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        currentLocation != null -> {
            Box(modifier.fillMaxSize()) {
                if (destination == null) {
                    // 목적지 선택 UI, key를 통해 재초기화 유도
                    key(destinationKey) {
                        DestinationSelector(
                            currentLocation = currentLocation,
                            clientId = clientId,
                            clientSecret = clientSecret
                        ) { selectedLatLng, address ->
                            destination = selectedLatLng
                            destinationAddress = address
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NaverMapWithRouteView(
                            modifier = Modifier.fillMaxSize(),
                            current = currentLocation,
                            destination = destination!!,
                            clientId = clientId,
                            clientSecret = clientSecret,
                            onSummaryReady = { summary = it },
                            onPathPointsReady = { pathPoints = it }
                        )

                        // 뒤로 가기 버튼은 요약 정보가 있을 때만 표시
                        if (summary != null) {
                            IconButton(
                                onClick = {
                                    destination = null
                                    destinationAddress = null
                                    summary = null
                                    pathPoints = emptyList()
                                    destinationKey = System.currentTimeMillis() // ⬅ mapView 재초기화 유도
                                },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                            }
                        }
                    }
                }

                summary?.let { s ->
                    RouteSummaryView(
                        distance = s.distance.toDouble(),
                        duration = s.duration,
                        pathPoints = pathPoints,
                        address = destinationAddress,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun rememberCurrentLocation(): LatLng? {
    val context = LocalContext.current
    val locationState = remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    DisposableEffect(fusedLocationClient) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val loc = locationResult.lastLocation
                if (loc != null) {
                    locationState.value = LatLng(loc.latitude, loc.longitude)
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    return locationState.value
}

