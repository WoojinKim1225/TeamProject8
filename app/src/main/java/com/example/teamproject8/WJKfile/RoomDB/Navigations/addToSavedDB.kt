package com.example.teamproject8.WJKfile.RoomDB.Navigations

import android.content.Context
import com.example.teamproject8.MYNfile.MapsPackage.Summary
import com.naver.maps.geometry.LatLng
import java.time.LocalDateTime

suspend fun addToSavedDB(
    context: Context,
    summary: Summary,
    pathPoints: List<LatLng>,
    originAddress: String?,
    destinationAddress: String?,
    originLatLng: LatLng,
    destinationLatLng: LatLng,
    selectedDateTime: LocalDateTime?)
{
    val db = NavigationDatabase.getDBInstance(context = context)
    db.getItemDao().InsertItem(NavigationEntity(
        title = "test",
        distance = summary.distance.toDouble(),
        duration = summary.duration,
        pathPoints = pathPoints,
        mode = "test",
        origin = originAddress?.toString() ?: "null",
        destination = destinationAddress?.toString() ?: "null",
        startLatLng = originLatLng,
        endLatLng = destinationLatLng,
        alarmTime = selectedDateTime,
        departureTime = selectedDateTime,
        arrivalTime = selectedDateTime,
        icon = 0,
        route = "test"
    ))
}