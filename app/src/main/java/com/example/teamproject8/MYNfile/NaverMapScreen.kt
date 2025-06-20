package com.example.teamproject8.MYNfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Looper
import android.util.Log
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.teamproject8.WJKfile.RoomDB.Navigations.addToSavedDB
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NaverMapScreen(
    modifier: Modifier = Modifier,
    clientId: String,       // clientId: Naver Maps API Client ID
    clientSecret: String,   // clientSecret: Naver Maps API Client Secret
    searchId: String,       // searchId: Naver Search API Client ID
    searchSecret: String,   // searchSecret: Naver Search API Client Secret
    googleApiKey: String    // googleApiKey: Google API Key
) {
    // 권한 요청
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    // 처음 실행될 때 권한 요청
    LaunchedEffect(Unit) { permissionState.launchMultiplePermissionRequest() }

    val granted = permissionState.permissions.any { it.status.isGranted }
    val currentLocation = rememberCurrentLocation() // 현재 위치
    val isLoadingLocation = granted && currentLocation == null  // 위치 로딩 중인지 여부

    // 날짜, 시간, 목적지 관련 상태 변수
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDestinationSelector by remember { mutableStateOf(false) }

    // 출발지 및 목적지 LatLng 및 주소
    var origin by remember { mutableStateOf<LatLng?>(null) }        // 예: (37.543888, 127.075955)
    var destination by remember { mutableStateOf<LatLng?>(null) }   // 예: (37.555026, 126.970668)
    var originAddress by remember { mutableStateOf<String?>(null) } // 예: 건국대학교
    var destinationAddress by remember { mutableStateOf<String?>(null) }    // 예: 서울역 (고속철도)

    // 각각 경로 요약 정보, 경로 좌표(LatLng) 리스트
    var summary by remember { mutableStateOf<Summary?>(null) }
    var pathPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context) // 생명주기 연동된 mapView
    var naverMap by remember { mutableStateOf<NaverMap?>(null) }

    // WJK: 추가
    val scope = rememberCoroutineScope()

    // 화면 종료 시, 상태 초기화
    DisposableEffect(Unit) {
        onDispose {
            showDatePicker = false
            showTimePicker = false
            showDestinationSelector = false
            selectedDateTime = null
        }
    }

    // 날짜 입력
    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            val today = LocalDate.now()
            DatePickerDialog(
                context,
                { _, y, m, d ->
                    val date = LocalDate.of(y, m + 1, d)
                    selectedDateTime = date.atTime(LocalTime.of(0, 0))
                    showDatePicker = false
                    showTimePicker = true
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            ).show()
        }
    }

    // 시간 입력
    LaunchedEffect(showTimePicker) {
        if (showTimePicker && selectedDateTime != null) {
            TimePickerDialog(
                context,
                { _, h, m ->
                    selectedDateTime = selectedDateTime!!.withHour(h).withMinute(m)
                    showTimePicker = false
                    showDestinationSelector = true
                },
                9,
                0,
                true
            ).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !granted -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("위치 권한이 필요합니다.") }
            // 로딩 중일때, 로딩 인디케이터 표시
            isLoadingLocation -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            currentLocation != null -> {
                when {
                    // 날짜 및 시간을 입력 완료 후, 출발지 및 목적지 입력으로 넘어감
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
                    // 길찾기 후 경로 출력
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
                        // 좌측 상단 뒤로가기 버튼 -> 메인 화면 이동
                        IconButton(
                            onClick = {
                                origin = null; destination = null; summary = null; pathPoints = emptyList()
                                selectedDateTime = null; showDestinationSelector = false
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
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

                                    // 현재 위치 마커
                                    Marker().apply {
                                        position = currentLocation
                                        captionText = "현재 위치"
                                        setMap(map)
                                    }
                                    map.moveCamera(CameraUpdate.scrollTo(currentLocation))
                                }
                            }
                        }, modifier = Modifier.fillMaxSize())

                        // + 버튼 표시(기본 지도화면일 때만)
                        if (!showDatePicker && !showTimePicker && !showDestinationSelector && origin == null && destination == null && summary == null) {
                            FloatingActionButton(
                                onClick = {
                                    showDatePicker = true
                                    origin = null; destination = null; summary = null
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "날짜/시간 선택")
                            }
                        }
                    }
                }
                // 길찾기 후 경로 요약 정보 Card 출력
                summary?.let { s ->
                    Log.d("MyTag", s.toString())
                    Log.d("MyTag2", pathPoints.toString())
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

                    IconButton(
                        onClick = {
                            scope.launch {
                                try {
                                    addToSavedDB(
                                        context = context,
                                        summary = s,
                                        pathPoints = pathPoints,
                                        originAddress = originAddress,
                                        destinationAddress = destinationAddress,
                                        originLatLng = origin!!, // origin과 destination이 null이 아님을 확인
                                        destinationLatLng = destination!!,
                                        selectedDateTime = selectedDateTime // selectedDateTime도 null이 아님을 확인하거나 nullable 처리
                                    )
                                    // (선택 사항) 저장 완료 후 사용자에게 알림 (예: Toast 메시지)
                                    // Toast.makeText(context, "경로가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                                    Log.d("NaverMapScreen", "Route saved to DB successfully")
                                } catch (e: Exception) {
                                    // (선택 사항) 오류 처리 (예: Logcat에 오류 기록, 사용자에게 오류 알림)
                                    Log.e("NaverMapScreen", "Error saving route to DB", e)
                                    // Toast.makeText(context, "경로 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                origin = null; destination = null; summary = null; pathPoints = emptyList()
                                selectedDateTime = null; showDestinationSelector = false
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "추가")
                    }
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