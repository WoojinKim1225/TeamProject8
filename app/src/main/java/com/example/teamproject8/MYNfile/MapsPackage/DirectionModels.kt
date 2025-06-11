package com.example.teamproject8.MYNfile.MapsPackage

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
    val end_address: String
)

data class Polyline(
    val points: String
)

data class ValueText(
    val value: Int,
    val text: String
)

data class Summary(
    val distance: Int,
    val duration: Int,
    val departureTime: String? = null,
    val arrivalTime: LocalDateTime? = null
)