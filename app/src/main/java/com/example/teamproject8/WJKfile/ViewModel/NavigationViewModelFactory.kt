package com.example.teamproject8.WJKfile.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teamproject8.WJKfile.RoomDB.Navigations.NavigationDao

class NavigationViewModelFactory (private val navigationDao: NavigationDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NavigationDatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NavigationDatabaseViewModel(navigationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}