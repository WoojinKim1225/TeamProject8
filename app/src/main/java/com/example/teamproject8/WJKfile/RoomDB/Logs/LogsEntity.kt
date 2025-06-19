package com.example.teamproject8.WJKfile.RoomDB.Logs

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.naver.maps.geometry.LatLng
import java.time.LocalDateTime

@Entity(tableName = "navigation_table")
data class LogsEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int  = 0,
    var title: String,
    var origin: String,
    var destination: String,
    var isSuccess: Boolean
)