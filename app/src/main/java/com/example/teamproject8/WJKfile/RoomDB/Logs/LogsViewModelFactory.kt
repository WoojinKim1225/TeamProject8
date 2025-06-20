package com.example.teamproject8.WJKfile.RoomDB.Logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LogsViewModelFactory(private val dao: LogsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}