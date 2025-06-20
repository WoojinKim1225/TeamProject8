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

    fun loadLogsForMonth(year: Int, month: Int) {
        val (start, end) = getStartAndEndOfMonth(year, month)
        viewModelScope.launch {
            dao.GetAllItems().collect { emittedList ->
                logs = emittedList
            }
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

    private fun getStartAndEndOfMonth(year: Int, month: Int): Pair<LocalDate, LocalDate> {
        val startOfMonth = LocalDate.of(year, month, 1)
        val endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth())
        return startOfMonth to endOfMonth
    }
}