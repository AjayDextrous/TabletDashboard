package com.example.tabletdashboard.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletdashboard.api.Weather
import com.example.tabletdashboard.repositories.WeatherRepository
import com.example.tabletdashboard.tools.AsyncState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _weatherState = MutableStateFlow<AsyncState<Weather>>(AsyncState.Loading)
    val weatherState = _weatherState.asStateFlow()

    fun startWeatherUpdates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchWeatherPeriodically(lat, lon).collect { weatherResult ->
                weatherResult.fold(
                    onSuccess = {
                        _weatherState.value = AsyncState.Success(it)
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to fetch weather", it)
                    },
                )
            }
        }
    }

    fun fetchWeatherManually(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchWeather(lat, lon).collect { weatherResult ->
                weatherResult.fold(
                    onSuccess = {
                        _weatherState.value = AsyncState.Success(it)
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to fetch weather", it)
                    },
                )
            }
        }
    }

    companion object {
        private const val TAG = "WeatherViewModel"
    }
}
