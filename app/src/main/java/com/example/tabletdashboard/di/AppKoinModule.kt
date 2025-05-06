package com.example.tabletdashboard.di

import org.koin.core.module.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import com.example.tabletdashboard.repositories.CalendarRepository
import com.example.tabletdashboard.repositories.MVGRepository
import com.example.tabletdashboard.repositories.MensaRepository
import com.example.tabletdashboard.repositories.RealCalendarRepository
import com.example.tabletdashboard.repositories.WeatherRepository
import com.example.tabletdashboard.viewmodels.CalendarTimelineViewModel
import com.example.tabletdashboard.viewmodels.MVGDeparturesViewModel
import com.example.tabletdashboard.viewmodels.MensaViewModel
import com.example.tabletdashboard.viewmodels.ObsidianDailyViewModel
import com.example.tabletdashboard.viewmodels.PomodoroTimerViewModel
import com.example.tabletdashboard.viewmodels.WeatherViewModel
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.koin.dsl.module

val appModule = module {
    single<CalendarRepository> { RealCalendarRepository() }
    single<MensaRepository> { MensaRepository() }
    single<MVGRepository> { MVGRepository() }
    single<WeatherRepository> { WeatherRepository() }
    single<Markwon> { Markwon.builder(androidContext())
        .usePlugin(TaskListPlugin.create(androidContext()))
        .build() }

    viewModel { CalendarTimelineViewModel(appContext = androidContext(), get()) }
    viewModel { MensaViewModel(get()) }
    viewModel { MVGDeparturesViewModel(get()) }
    viewModel { WeatherViewModel(get()) }
    viewModel { ObsidianDailyViewModel(get()) }
    viewModel { PomodoroTimerViewModel() }
}