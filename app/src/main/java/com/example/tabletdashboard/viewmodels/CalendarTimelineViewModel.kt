package com.example.tabletdashboard.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletdashboard.repositories.CalendarEvent
import com.example.tabletdashboard.repositories.CalendarRepository
import com.example.tabletdashboard.repositories.startOfDay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration.Companion.minutes

class CalendarTimelineViewModel(val appContext: Context, val calendarRepository: CalendarRepository): ViewModel() {

    val selectableDays = listOf(1, 3, 4, 5, 7)
    val selectedDays = MutableStateFlow(1)
    val startDate = MutableStateFlow(Calendar.getInstance().startOfDay())
    private val timerFlow = flow {
        while (true) {
            emit(Unit)
            delay(10.minutes)
        }
    }
    val events = MutableStateFlow<Map<Calendar, List<CalendarEvent>>>(emptyMap())

    init {
        viewModelScope.launch {
            combine(startDate, selectedDays, timerFlow) { startDate, selectedDays, _ ->
                calendarRepository.fetchCalendarEvents(appContext, startDate, selectedDays)
            }.collect { updatedEvents ->
                events.value = updatedEvents
            }
        }
    }

    fun goToPrevious(){
        startDate.value = (startDate.value.clone() as Calendar).also {
            it.add(
                Calendar.DAY_OF_YEAR,
                -selectedDays.value
            )
        }
    }

    fun goToToday() {
        startDate.value = Calendar.getInstance().startOfDay()
    }

    fun goToNext(){
        startDate.value = (startDate.value.clone() as Calendar).also {
            it.add(
                Calendar.DAY_OF_YEAR,
                selectedDays.value
            )
        }
    }

}