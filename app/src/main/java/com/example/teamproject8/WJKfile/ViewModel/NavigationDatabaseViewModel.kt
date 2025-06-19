package com.example.teamproject8.WJKfile.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamproject8.R
import com.example.teamproject8.WJKfile.RoomDB.NavigationDao
import com.example.teamproject8.WJKfile.RoomDB.NavigationEntity
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NavigationDatabaseViewModel(private val navigationDao: NavigationDao) : ViewModel() {
    val navigationItems:StateFlow<List<NavigationEntity>> = navigationDao.GetAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSampleNavigationItems() {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val addedMinutes = now.plusMinutes(15)

            val sampleNavigationItems = listOf(
                NavigationEntity(
                    title = "Going To School",
                    icon = R.drawable.baseline_location_pin_24,
                    route = "Home",
                    departureTime = now,
                    arrivalTime = addedMinutes,
                    origin = "Home",
                    destination = "School",
                    distance = 0.0,
                    duration = 0,
                    pathPoints = emptyList(),
                    mode = "test",
                    startLatLng = LatLng(0.0,0.0),
                    endLatLng = LatLng(1.0,1.0),
                    alarmTime = LocalDateTime.now(),
                )

            )
            navigationDao.InsertItems(sampleNavigationItems)
        }
    }
}