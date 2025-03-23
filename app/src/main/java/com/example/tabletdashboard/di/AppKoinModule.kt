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
import com.example.tabletdashboard.viewmodels.WeatherViewModel
import org.koin.dsl.module

val appModule = module {
    single<CalendarRepository> { RealCalendarRepository() }
    single<MensaRepository> { MensaRepository() }
    single<MVGRepository> { MVGRepository() }
    single<WeatherRepository> { WeatherRepository() }

    viewModel { CalendarTimelineViewModel(appContext = androidContext(), get()) }
    viewModel { MensaViewModel(get()) }
    viewModel { MVGDeparturesViewModel(get()) }
    viewModel { WeatherViewModel(get()) }
}