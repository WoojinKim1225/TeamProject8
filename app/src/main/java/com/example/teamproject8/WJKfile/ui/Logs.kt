package com.example.teamproject8.WJKfile.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.teamproject8.WJKfile.RoomDB.Logs.LogsDatabase
import com.example.teamproject8.WJKfile.RoomDB.Logs.LogsEntity
import com.example.teamproject8.WJKfile.RoomDB.Logs.LogsViewModel
import com.example.teamproject8.WJKfile.RoomDB.Logs.LogsViewModelFactory
import com.example.teamproject8.WJKfile.RoomDB.Navigations.NavigationDatabase
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun Favorites() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "favorites",
            tint = Color.Blue,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun LogsWithCalender(navController: NavController, viewModel: LogsViewModel = viewModel(
    factory = LogsViewModelFactory(
        dao = LogsDatabase.getDBInstance(navController.context).getItemDao()
    )
)) {
    val context = LocalContext.current
    val db = LogsDatabase.getDBInstance(context)
    val dao = db.getItemDao()
    val coroutinescope = rememberCoroutineScope()       //imsi
    var item_id = 1


    val today = remember { LocalDateTime.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }

    LaunchedEffect(currentMonth) {
        viewModel.loadLogsForMonth(today.year, today.monthValue)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // 월 선택 UI (상단)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                currentMonth = currentMonth.minusMonths(1)
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "이전 달")
            }

            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy년 M월")),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                currentMonth = currentMonth.plusMonths(1)

                //imsi
                val newLog = LogsEntity(
                    id = item_id,
                    title = "imsi",
                    origin = "imsi",
                    destination = "imsi",
                    arrivalTime = LocalDateTime.now(),
                    isSuccess = true
                )

                coroutinescope.launch {
                    dao.InsertItem(newLog)
                }
                item_id++

            }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "다음 달")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 달력 그리드 (중간)
        CalendarGrid(currentMonth, viewModel)

        Spacer(modifier = Modifier.weight(1f))

        if (viewModel.dateLogs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            val date = viewModel.dateLogs.first().arrivalTime?.toLocalDate()
            date?.let {
                Text(
                    text = "${date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))}의 로그",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.dateLogs) { log ->
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.medium)
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "${log.arrivalTime?.hour} : ${log.arrivalTime?.minute}",
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${log.origin} -> ${log.destination}",
                                fontSize = 12.sp
                            )
                        }
                        if (log.isSuccess) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "check")
                        } else {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "clear")
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(month: YearMonth, viewModel: LogsViewModel) {
    val firstDayOfMonth = month.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7  // 일요일 = 0
    val daysInMonth = month.lengthOfMonth()

    val prevMonth = month.minusMonths(1)
    val prevMonthDays = prevMonth.lengthOfMonth()

    val logsPerDay = viewModel.logs.groupingBy { it.arrivalTime?.toLocalDate() }.eachCount()
    Log.i("로그", viewModel.logs.toString())

    val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7  // 전체 칸 수 (7의 배수로 맞춤)

    Column {
        // 요일 헤더
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = (if (day == "일") Color.Red else if (day == "토") Color.Blue else Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 셀
        for (week in 0 until totalCells / 7) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (dayIndex in 0..6) {
                    val cellIndex = week * 7 + dayIndex
                    val dayText: String
                    val isInMonth: Boolean
                    var date: LocalDate? = null

                    when {
                        cellIndex < firstDayOfWeek -> {
                            // 이전 달 날짜
                            val day = prevMonthDays - (firstDayOfWeek - cellIndex - 1)
                            date = prevMonth.atDay(day)
                            dayText = day.toString()
                            isInMonth = false
                        }
                        cellIndex < firstDayOfWeek + daysInMonth -> {
                            // 이번 달 날짜
                            val day = cellIndex - firstDayOfWeek + 1
                            date = month.atDay(day)
                            dayText = day.toString()
                            isInMonth = true
                        }
                        else -> {
                            // 다음 달 날짜
                            val day = cellIndex - (firstDayOfWeek + daysInMonth) + 1
                            date = month.plusMonths(1).atDay(day)
                            dayText = day.toString()
                            isInMonth = false
                        }
                    }

                    val isHoliday = isHoliday(date) // 공휴일 여부 판단 (아래에 함수 정의)
                    val dayOfWeek = date?.dayOfWeek

                    val baseColor = when {
                        isHoliday || dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
                        dayOfWeek == DayOfWeek.SATURDAY -> Color.Blue
                        else -> Color.Black
                    }

                    val displayColor = if (!isInMonth) {
                        // 이번 달이 아니면 색상을 연하게 처리
                        Color(
                            red = (1f + baseColor.red) / 2f,
                            green = (1f + baseColor.green) / 2f,
                            blue = (1f + baseColor.blue) / 2f,
                            alpha = baseColor.alpha
                        )
                    } else {
                        baseColor
                    }

                    val logCount = logsPerDay[date] ?: 0

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clickable(onClick = {

                                viewModel.loadLogsForDate(date)
                                Log.i("log", viewModel.dateLogs.toString())
                            })
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = dayText,
                                color = displayColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )

                            if (logCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(16.dp)
                                        .background(Color.Red, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = logCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}


private fun isHoliday(date: LocalDate?): Boolean {
    if (date == null) return false

    val month = date.monthValue
    val day = date.dayOfMonth

    // 고정 공휴일 (대한민국 기준)
    val fixedHolidays = listOf(
        Pair(1, 1),    // 신정
        Pair(3, 1),    // 삼일절
        Pair(5, 5),    // 어린이날
        Pair(6, 6),    // 현충일
        Pair(8, 15),   // 광복절
        Pair(10, 3),   // 개천절
        Pair(10, 9),   // 한글날
        Pair(12, 25)   // 성탄절
    )

    return fixedHolidays.contains(Pair(month, day))
}
