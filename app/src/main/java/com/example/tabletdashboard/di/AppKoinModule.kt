package com.example.tabletdashboard.di

import com.example.tabletdashboard.repositories.CalendarRepository
import com.example.tabletdashboard.repositories.DummyCalendarRepository
import com.example.tabletdashboard.repositories.RealCalendarRepository
import org.koin.dsl.module

val appModule = module {
    single<CalendarRepository> { RealCalendarRepository() }
}