package com.example.teamproject8.MYNfile.MapsPackage

data class DirectionResponse(
    val route: Route?
)

// route는 여러 경로 타입(traoptimal, tracomfort 등)을 포함할 수 있음
data class Route(
    val traoptimal: List<Traoptimal>? // traoptimal은 배열로 옴
)

// traoptimal 내의 정보
data class Traoptimal(
    val summary: Summary?,          // 요약 정보 (거리, 시간 등)
    val path: List<List<Double>>?,  // 경로 좌표 리스트 [경도, 위도]
    val section: List<Section>?     // 구간 정보 (option 설정 시 유용)
)

// 요약 정보
data class Summary(
    val distance: Int,          // 총 거리 (m)
    val duration: Int,          // 총 소요 시간 (ms)
    val departureTime: String?, // 출발 시각 (옵션)
    val arrivalTime: String?    // 도착 시각 (옵션)
)

// 구간 정보
data class Section(
    val pointCount: Int?,
    val distance: Int?,
    val duration: Int?,
    val roads: List<Road>?
)

// 도로 정보 (필요 시)
data class Road(
    val name: String?,
    val distance: Int?,
    val duration: Int?,
    val trafficSpeed: Int?,
    val trafficState: String?
)