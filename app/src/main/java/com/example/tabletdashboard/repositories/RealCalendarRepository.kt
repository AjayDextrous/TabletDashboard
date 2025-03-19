package com.example.tabletdashboard.repositories

import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.SortedMap

class RealCalendarRepository: CalendarRepository {

    // Fetch calendar events from Android’s local calendar
    override fun fetchCalendarEvents(context: Context, startDate: Calendar, daysToShow: Int): Map<Calendar, List<CalendarEvent>> {
        val endDate = (startDate.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, daysToShow) }.endOfDay()
        val events = mutableMapOf<Calendar, MutableList<CalendarEvent>>()

        val startMillis = startDate.timeInMillis
        val endMillis = endDate.timeInMillis
        Log.d(TAG, "Start: ${startMillis.formatToDateTime()} End: ${endMillis.formatToDateTime()}")


        val uri = Uri.withAppendedPath(CalendarContract.Instances.CONTENT_URI, "$startMillis/$endMillis")


        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.DISPLAY_COLOR
        )
        val cursor = context.contentResolver.query(uri, projection, null, null, CalendarContract.Instances.BEGIN + " ASC")


        cursor?.use {
            val idIndex = it.getColumnIndex(CalendarContract.Instances.EVENT_ID)
            val titleIndex = it.getColumnIndex(CalendarContract.Instances.TITLE)
            val startIndex = it.getColumnIndex(CalendarContract.Instances.BEGIN)
            val endIndex = it.getColumnIndex(CalendarContract.Instances.END)
            val allDayIndex = it.getColumnIndex(CalendarContract.Instances.ALL_DAY)
            val colorIndex = it.getColumnIndex(CalendarContract.Instances.DISPLAY_COLOR)

            while (it.moveToNext()) {
                val title = it.getString(titleIndex) ?: "No Title"
                val startTime = it.getLong(startIndex)
                val endTime = it.getLong(endIndex)
                val colorInt = it.getInt(colorIndex)
                val color = Color(colorInt)
                val allDay = it.getInt(allDayIndex) == 1

                val eventStart = Calendar.getInstance().apply { timeInMillis = startTime }
                val eventEnd = Calendar.getInstance().apply { timeInMillis = endTime }

                events.getOrPut(eventStart.startOfDay()) { mutableListOf() }
                    .add(CalendarEvent(title, eventStart, eventEnd, color, allDay))
            }
        }

        return events.toSortedMap().also {  it.logValues() }
    }

    private fun SortedMap<Calendar, MutableList<CalendarEvent>>.logValues(): Unit {
        this.keys.forEach { key ->
            Log.d(TAG, "${key.timeInMillis.formatToDateTime()} - ${this[key]?.size} events")
            val events  = this[key]
            events?.forEach { event ->
                Log.d(TAG, "Event: ${event.title} Start: ${event.start.timeInMillis.formatToDateTime()} End: ${event.end.timeInMillis.formatToDateTime()}")
            }
        }
    }

    companion object {
        private const val TAG = "RealCalendarRepository"
    }

}