package com.example.tabletdashboard.repositories

import com.example.tabletdashboard.api.WeatherRetrofitInstance
import com.example.tabletdashboard.api.WeatherResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository {
    fun fetchWeatherPeriodically(lat: Double, lon: Double): Flow<WeatherResponse> = flow {
        while (true) {
            val response = WeatherRetrofitInstance.api.getWeather(lat, lon)
            emit(response)
            delay(15 * 60 * 1000) // 15 minutes
        }
    }
}
