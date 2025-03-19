package com.example.tabletdashboard.repositories

import android.content.Context
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface CalendarRepository {

    fun fetchCalendarEvents(context: Context, startDate: Calendar, daysToShow: Int): Map<Calendar, List<CalendarEvent>>

}

// Extension function to set a Calendar instance to 00:00:00.000
fun Calendar.startOfDay(): Calendar {
    val newCal: Calendar = this.clone() as Calendar
    newCal.set(Calendar.HOUR_OF_DAY, 0)
    newCal.set(Calendar.MINUTE, 0)
    newCal.set(Calendar.SECOND, 0)
    newCal.set(Calendar.MILLISECOND, 0)
    return newCal
}

// Extension function to set a Calendar instance to 23:59:59.999
fun Calendar.endOfDay(): Calendar {
    val newCal: Calendar = this.clone() as Calendar
    newCal.set(Calendar.HOUR_OF_DAY, 23)
    newCal.set(Calendar.MINUTE, 59)
    newCal.set(Calendar.SECOND, 59)
    newCal.set(Calendar.MILLISECOND, 999)
    return newCal
}

fun Long.formatToDateTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(this))
}

// Data class for events
data class CalendarEvent(val title: String, val start: Calendar, val end: Calendar, val color: Color = Color.Blue, val isAllDay: Boolean = false)

