package com.example.teamproject8.WJKfile.RoomDB.Converters

import androidx.room.TypeConverter
import com.naver.maps.geometry.LatLng

class LatLngListConverter {
    @TypeConverter
    fun fromLatLngList(list: List<LatLng>): String {
        return list.joinToString("|") { "${it.latitude},${it.longitude}" }
    }

    @TypeConverter
    fun toLatLngList(data: String): List<LatLng> {
        if (data.isBlank()) return emptyList()

        return data.split("|")
            .filter { it.isNotBlank() && it.contains(",") }
            .map {
                val parts = it.split(",")
                val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val lng = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                LatLng(lat, lng)
            }
    }
}