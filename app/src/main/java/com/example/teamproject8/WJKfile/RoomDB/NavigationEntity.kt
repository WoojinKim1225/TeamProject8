package com.example.teamproject8.WJKfile.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "navigation_table")
data class NavigationEntity(
    // 식별 아이디
    @PrimaryKey(autoGenerate = true)
    var id: Int  = 0,

    var title: String,

    // 집, 회사 등 아이콘
    var icon: Int,
    var route: String,
    var departureTime: Int,
    var arrivalTime: Int
)
