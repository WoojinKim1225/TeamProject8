package com.example.teamproject8.NEYfile.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.teamproject8.NEYfile.AlarmUI.SavedItem
import com.example.teamproject8.R
import com.example.teamproject8.WJKfile.RoomDB.NavigationDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SavedViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = Room.databaseBuilder(
        application,
        NavigationDatabase::class.java,
        "your-db-name"
    ).build().getItemDao()

    val savedItems: StateFlow<List<SavedItem>> = dao.GetAllItems()
        .map { navList ->
            navList.map { nav ->
                SavedItem(
                    id = nav.id,
//                    departLoc = nav.departLoc,
//                    arrivedLoc = nav.arrivedLoc,
                    departTime = nav.departureTime,
                    arrivedTime = nav.arrivalTime,
                    mapImage = nav.icon
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}
