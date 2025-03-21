package com.example.tabletdashboard.repositories

import com.example.tabletdashboard.api.WeatherRetrofitInstance
import com.example.tabletdashboard.api.Weather
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

class WeatherRepository {
    fun fetchWeatherPeriodically(lat: Double, lon: Double): Flow<Result<Weather>> = flow {
        while (true) {
            try {
                val response = WeatherRetrofitInstance.api.getWeather(lat, lon)
                if (response.isSuccessful){
                    emit(Result.success(response.body()!!))
                } else {
                    emit(Result.failure(Exception("Failed to fetch weather")))
                }
            } catch (e: IOException){
                emit(Result.failure(Exception("Network error $e")))
            } catch (e: Exception) {
                emit(Result.failure(Exception("Unexpected error $e")))
            }
            delay(15 * 60 * 1000) // 15 minutes
        }
    }

    fun fetchWeather(lat: Double, lon: Double): Flow<Result<Weather>> = flow {
        try {
            val response = WeatherRetrofitInstance.api.getWeather(lat, lon)
            if (response.isSuccessful){
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch weather")))
            }
        } catch (e: IOException){
            emit(Result.failure(Exception("Network error $e")))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Unexpected error $e")))
        }
    }
}
