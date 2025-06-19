package com.example.teamproject8.WJKfile.RoomDB.Converters

import androidx.room.TypeConverter
import com.naver.maps.geometry.LatLng

class LatLngConverter {
    @TypeConverter
    fun fromLatLng(latLng: LatLng?): String? {
        return latLng?.let { "${it.latitude},${it.longitude}" }
    }

    @TypeConverter
    fun toLatLng(latLngString: String?): LatLng? {
        return latLngString?.split(',')?.let {
            if (it.size == 2) {
                try {
                    LatLng(it[0].toDouble(), it[1].toDouble())
                } catch (e: NumberFormatException) {
                    null // Handle malformed string
                }
            } else {
                null
            }
        }
    }
}