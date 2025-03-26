package com.example.tabletdashboard.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tabletdashboard.ui.theme.TabletDashboardTheme
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarMonthWidget(
    modifier: Modifier = Modifier,
    rowSpan: Int,
    colSpan: Int,
    weekStartDay: DayOfWeek = DayOfWeek.MONDAY // Changeable start day
) {
    var today by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3600_000) // Update every hour
            today = LocalDate.now()
        }
    }

    val yearMonth = YearMonth.now()
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek
    val startOffset = (firstDayOfMonth.value - weekStartDay.value + 7) % 7
    val totalDays = daysInMonth + startOffset
    val weeks = (totalDays / 7) + if (totalDays % 7 > 0) 1 else 0

    var showHeader by remember { mutableStateOf(false) }
    LaunchedEffect(showHeader) {
        if (showHeader) {
            delay(3000)
            showHeader = false
        }
    }
    val interactionSource = remember { MutableInteractionSource() }


    Box(Modifier
        .fillMaxSize()
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        .clickable(enabled = true, onClick = { showHeader = !showHeader }, interactionSource = interactionSource, indication = null)
        .border(1.dp, MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedVisibility(showHeader) {
                Text(
                    text = yearMonth.month.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    ) + " " + yearMonth.year,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            // Weekday Header Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val daysOfWeek = DayOfWeek.entries.toList().drop(weekStartDay.ordinal) +
                        DayOfWeek.entries.toList().take(weekStartDay.ordinal)

                Text(
                    text = "CW", // Calendar week header
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp) // Space for week numbers
                )

                daysOfWeek.forEach { day ->
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) Color.Red else Color.Unspecified,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Calendar Grid
            for (weekIndex in 0 until weeks) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    val weekNumber = yearMonth.atDay((weekIndex * 7 + 1 - startOffset).coerceAtLeast(1))
                        .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

                    Text(
                        text = weekNumber.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(40.dp) // Space for week numbers
                    )

                    for (dayOffset in 0 until 7) {
                        val dayOfMonth = weekIndex * 7 + dayOffset + 1 - startOffset
                        val isValidDay = dayOfMonth in 1..daysInMonth
                        val isToday = isValidDay && dayOfMonth == today.dayOfMonth

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                text = if (isValidDay) dayOfMonth.toString() else "",
                                fontSize = 16.sp,
                                color = if (isToday) Color.White else if (isWeekend(dayOffset, weekStartDay)) Color.Red else Color.Black,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}


// Helper function to check if a given day is a weekend
fun isWeekend(dayOffset: Int, weekStartDay: DayOfWeek): Boolean {
    val realDayOfWeek = (weekStartDay.value + dayOffset - 1) % 7 + 1
    return realDayOfWeek == DayOfWeek.SATURDAY.value || realDayOfWeek == DayOfWeek.SUNDAY.value
}
