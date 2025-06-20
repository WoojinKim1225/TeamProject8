package com.example.teamproject8.WJKfile.RoomDB.Logs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields

class LogsViewModel(private val dao: LogsDao) : ViewModel() {

    var logs by mutableStateOf<List<LogsEntity>>(emptyList())
        private set
    var dateLogs by mutableStateOf<List<LogsEntity>>(emptyList())
        private set

    fun loadLogsForDate(date: LocalDate) {
        viewModelScope.launch {
            dateLogs = dao.getItemsByDate(date)
        }
    }

    fun loadLogsForWeek(year: Int, week: Int) {
        val (start, end) = getStartAndEndOfWeek(year, week)
        viewModelScope.launch {
            logs = dao.getItemsByWeek(start, end)
        }
    }

    private fun getStartAndEndOfWeek(year: Int, week: Int): Pair<LocalDate, LocalDate> {
        val weekFields = WeekFields.of(DayOfWeek.MONDAY, 1)
        val startOfWeek = LocalDate
            .of(year, 1, 1)
            .with(weekFields.weekOfYear(), week.toLong())
            .with(weekFields.dayOfWeek(), 1)
        val endOfWeek = startOfWeek.plusDays(6)
        return startOfWeek to endOfWeek
    }
}