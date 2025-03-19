package com.example.tabletdashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletdashboard.api.WeatherResponse
import com.example.tabletdashboard.repositories.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState = _weatherState.asStateFlow()

    fun startWeatherUpdates(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.fetchWeatherPeriodically(lat, lon).collect { weather ->
                _weatherState.value = weather
            }
        }
    }
}
