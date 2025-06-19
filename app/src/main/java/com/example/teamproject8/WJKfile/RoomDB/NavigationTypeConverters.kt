package com.example.teamproject8.WJKfile.RoomDB

import androidx.room.TypeConverter
import com.naver.maps.geometry.LatLng
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NavigationTypeConverters {

    // 1. LocalDateTime <-> String
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    // 2. LatLng <-> String
    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }

    @TypeConverter
    fun toLatLng(value: String): LatLng {
        val parts = value.split(",")
        return LatLng(parts[0].toDouble(), parts[1].toDouble())
    }

    // 3. List<LatLng> <-> String
    @TypeConverter
    fun fromLatLngList(list: List<LatLng>): String {
        return list.joinToString(";") { "${it.latitude},${it.longitude}" }
    }

    @TypeConverter
    fun toLatLngList(data: String): List<LatLng> {
        if (data.isBlank()) return emptyList()
        return data.split(";").map {
            val (lat, lng) = it.split(",")
            LatLng(lat.toDouble(), lng.toDouble())
        }
    }
}