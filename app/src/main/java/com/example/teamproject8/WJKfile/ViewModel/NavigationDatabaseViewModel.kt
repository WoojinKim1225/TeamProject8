package com.example.teamproject8.WJKfile.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamproject8.WJKfile.RoomDB.Navigations.NavigationDao
import com.example.teamproject8.WJKfile.RoomDB.Navigations.NavigationEntity
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
            val sampleNavigationItems = listOf(
                NavigationEntity(
                    id = 1,
                    title = "test",
                    distance = 0.0,
                    duration = 0,
                    pathPoints = emptyList(),
                    mode = "test",
                    origin = "test",
                    destination = "test",
                    startLatLng = LatLng(0.0,0.0),
                    endLatLng = LatLng(0.0,0.0),
                    alarmTime = LocalDateTime.now(),
                    departureTime = LocalDateTime.now(),
                    arrivalTime = LocalDateTime.now(),
                    icon = 0,
                    route = "test"
                )

            )
            navigationDao.InsertItems(sampleNavigationItems)
        }
    }
}