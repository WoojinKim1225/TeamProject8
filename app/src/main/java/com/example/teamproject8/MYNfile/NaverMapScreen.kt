package com.example.teamproject8.MYNfile

import android.Manifest
import android.R.attr.name
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.teamproject8.MYNfile.MapsFun.DestinationSelector
import com.example.teamproject8.MYNfile.MapsFun.NaverMapWithRouteView
import com.example.teamproject8.MYNfile.MapsFun.RouteSummaryView
import com.example.teamproject8.MYNfile.MapsFun.rememberMapViewWithLifecycle
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
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NaverMapScreen(
    modifier: Modifier = Modifier,
    clientId: String,
    clientSecret: String,
    searchId: String,
    searchSecret: String,
    googleApiKey: String
) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) { permissionState.launchMultiplePermissionRequest() }

    val granted = permissionState.permissions.any { it.status.isGranted }
    val currentLocation = rememberCurrentLocation()
    val isLoadingLocation = granted && currentLocation == null

    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDestinationSelector by remember { mutableStateOf(false) }

    var origin by remember { mutableStateOf<LatLng?>(null) }
    var destination by remember { mutableStateOf<LatLng?>(null) }
    var originAddress by remember { mutableStateOf<String?>(null) }
    var destinationAddress by remember { mutableStateOf<String?>(null) }

    var summary by remember { mutableStateOf<Summary?>(null) }
    var pathPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    if (showDatePicker) {
        val today = LocalDate.now()
        DatePickerDialog(context, { _, y, m, d ->
            val date = LocalDate.of(y, m + 1, d)
            selectedDateTime = date.atTime(LocalTime.of(0, 0))
            showDatePicker = false
            showTimePicker = true
        }, today.year, today.monthValue - 1, today.dayOfMonth).show()
    }

    if (showTimePicker && selectedDateTime != null) {
        TimePickerDialog(context, { _, h, m ->
            selectedDateTime = selectedDateTime!!.withHour(h).withMinute(m)
            showTimePicker = false
            showDestinationSelector = true
        }, 9, 0, true).show()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !granted -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("위치 권한이 필요합니다.") }
            isLoadingLocation -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            currentLocation != null -> {
                when {
                    showDestinationSelector -> {
                        DestinationSelector(
                            currentLocation = currentLocation,
                            clientId = clientId,
                            clientSecret = clientSecret,
                            searchId = searchId,
                            searchSecret = searchSecret
                        ) { originLatLng, originName, destLatLng, destName ->
                            origin = originLatLng
                            originAddress = originName
                            destination = destLatLng
                            destinationAddress = destName
                            showDestinationSelector = false
                        }
                    }

                    origin != null && destination != null && selectedDateTime != null -> {
                        val departureTime = selectedDateTime!!.atZone(ZoneId.of("Asia/Seoul")).toEpochSecond().toString()
                        NaverMapWithRouteView(
                            modifier = Modifier.fillMaxSize(),
                            current = origin!!,
                            destination = destination!!,
                            googleApiKey = googleApiKey,
                            mode = "transit",
                            departureTime = departureTime,
                            onSummaryReady = { summary = it },
                            onPathPointsReady = { pathPoints = it }
                        )
                        IconButton(
                            onClick = {
                                origin = null; destination = null; summary = null; pathPoints = emptyList()
                                selectedDateTime = null; showDestinationSelector = false
                            },
                            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                        }
                    }

                    else -> {
                        // 기본 지도 + 현재 위치 마커 표시
                        AndroidView(factory = {
                            mapView.apply {
                                getMapAsync { map ->
                                    naverMap = map

                                    Marker().apply {
                                        position = currentLocation
                                        captionText = "현재 위치"
                                        setMap(map)
                                    }
                                    map.moveCamera(CameraUpdate.scrollTo(currentLocation))
                                }
                            }
                        }, modifier = Modifier.fillMaxSize())

                        // ➕ 버튼은 기본 지도화면일 때만 표시
                        if (!showDatePicker && !showTimePicker && !showDestinationSelector && origin == null && destination == null && summary == null) {
                            FloatingActionButton(
                                onClick = {
                                    showDatePicker = true
                                    origin = null; destination = null; summary = null
                                },
                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "날짜/시간 선택")
                            }
                        }
                    }
                }

                summary?.let { s ->
                    RouteSummaryView(
                        distance = s.distance.toDouble(),
                        duration = s.duration,
                        pathPoints = pathPoints,
                        mode = "transit",
                        startName = originAddress ?: "출발지",
                        endName = destinationAddress ?: "목적지",
                        startLatLng = origin!!,
                        endLatLng = destination!!,
                        departureTime = s.departureTime,
                        arrivalTime = s.arrivalTime,
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