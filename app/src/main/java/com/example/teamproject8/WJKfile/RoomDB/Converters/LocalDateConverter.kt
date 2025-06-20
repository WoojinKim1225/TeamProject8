package com.example.teamproject8.WJKfile.RoomDB.Converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.ZoneOffset

class LocalDateConverter {
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}