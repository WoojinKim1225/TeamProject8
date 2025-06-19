package com.example.teamproject8.WJKfile.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.naver.maps.geometry.LatLng
import java.time.LocalDateTime

@Entity(tableName = "navigation_table")
data class NavigationEntity(
    // 식별 아이디
    @PrimaryKey(autoGenerate = true)
    var id: Int  = 0,

    var title: String,

    // 집, 회사 등 아이콘
    var distance:Double,
    var duration: Int,
    var pathPoints: List<LatLng>,               //미니맵 제작 위한 리스트
    var mode:String,
    var origin: String,
    var destination: String,
    var startLatLng: LatLng,
    var endLatLng: LatLng,

    var alarmTime:LocalDateTime? = null,        //10분 단위 Update 반복을 위한 타이머
    var departureTime: LocalDateTime? = null,   //출발 시간
    var arrivalTime: LocalDateTime? = null,
    var icon: Int,
    var route: String,
    var doWork: Boolean = false

)
