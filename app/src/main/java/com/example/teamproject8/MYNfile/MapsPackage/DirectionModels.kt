package com.example.teamproject8.MYNfile.MapsPackage

import com.naver.maps.geometry.LatLng
import java.time.LocalDateTime

data class GoogleDirectionsResponse(
    val status: String,
    val routes: List<GoogleRoute>
)

data class GoogleRoute(
    val legs: List<Leg>,
    val overview_polyline: Polyline
)

data class Leg(
    val distance: ValueText,
    val duration: ValueText,
    val end_address: String,
    val departure_time: TimeText?,
    val arrival_time: TimeText?
)

data class TimeText(
    val value: Long,
    val text: String,
    val time_zone: String
)

data class Polyline(
    val points: String
)

data class ValueText(
    val value: Int,
    val text: String
)

data class PlaceResult(
    val name: String,
    val location: LatLng,
    val address: String?,
    val roadAddress: String?
)

data class Summary(
    val distance: Int,
    val duration: Int,
    val departureTime: LocalDateTime? = null,
    val arrivalTime: LocalDateTime? = null
)