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
        return data.split("|").map {
            val parts = it.split(",")
            LatLng(parts[0].toDouble(), parts[1].toDouble())
        }
    }
}