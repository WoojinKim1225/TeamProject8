package com.example.teamproject8.WJKfile.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teamproject8.R
import com.example.teamproject8.WJKfile.RoomDB.NavigationDao
import com.example.teamproject8.WJKfile.RoomDB.NavigationEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
                    id = TODO(),
                    title = TODO(),
                    distance = TODO(),
                    duration = TODO(),
                    pathPoints = TODO(),
                    mode = TODO(),
                    origin = TODO(),
                    destination = TODO(),
                    startLatLng = TODO(),
                    endLatLng = TODO(),
                    alarmTime = TODO(),
                    departureTime = TODO(),
                    arrivalTime = TODO(),
                    icon = TODO(),
                    route = TODO()
                )

            )
            navigationDao.InsertItems(sampleNavigationItems)
        }
    }
}