package com.example.teamproject8.WJKfile.RoomDB.Logs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields

class LogsViewModel(private val dao: LogsDao) : ViewModel() {

    var logs by mutableStateOf<List<LogsEntity>>(emptyList())
        private set
    var dateLogs by mutableStateOf<List<LogsEntity>>(emptyList())
        private set

    fun loadLogsForDate(date: LocalDate) {
        val startDateTime = date.atStartOfDay()
        val endDateTime = date.plusDays(1).atStartOfDay()
        viewModelScope.launch {
            dateLogs = dao.getItemsByBetween(startDateTime, endDateTime)
        }
    }

    fun loadLogsForMonth(year: Int, month: Int) {
        val (start, end) = getStartAndEndOfMonth(year, month)
        viewModelScope.launch {
            logs = dao.getItemsByBetween(start, end)
        }
    }

    private fun getStartAndEndOfMonth(year: Int, month: Int): Pair<LocalDateTime, LocalDateTime> {
        val startOfMonth = LocalDate.of(year, month, 1).atStartOfDay()
        val endOfMonth = LocalDate.of(year, month, 1).atStartOfDay().plusMonths(1)
        return startOfMonth to endOfMonth
    }
}