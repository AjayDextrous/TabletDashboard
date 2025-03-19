package com.example.tabletdashboard.repositories

import android.content.Context
import androidx.compose.ui.graphics.Color
import java.util.Calendar

class DummyCalendarRepository: CalendarRepository {
    override fun fetchCalendarEvents(
        context: Context,
        startDate: Calendar,
        daysToShow: Int
    ): Map<Calendar, List<CalendarEvent>> {
        val endDate = (startDate.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, daysToShow) }.endOfDay()
        val events = mutableMapOf<Calendar, MutableList<CalendarEvent>>()

        val startMillis = startDate.timeInMillis
        val endMillis = endDate.timeInMillis

        (0..daysToShow).forEach { day ->
            (0..1).forEach {
                val title = "Meeting $it"
                val startTime = startDate.timeInMillis + day*(1000*60*60*24) + (it+8)*(1000*60*60)
                val endTime = startDate.timeInMillis + day*(1000*60*60*24) + (it+listOf(9,10,11).random())*(1000*60*60)
                val color = listOf(Color(0xFF0165AF), Color(0xFFBE7B00), Color(0xFF01AB85)).random()
                val allDay = false

                val eventStart = Calendar.getInstance().apply { timeInMillis = startTime }
                val eventEnd = Calendar.getInstance().apply { timeInMillis = endTime }

                events.getOrPut(eventStart.startOfDay()) { mutableListOf() }
                    .add(CalendarEvent(title, eventStart, eventEnd, color, allDay))
            }
        }
        return events.toSortedMap()
    }
}