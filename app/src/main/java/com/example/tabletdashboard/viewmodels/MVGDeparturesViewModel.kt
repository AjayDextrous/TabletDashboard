package com.example.tabletdashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletdashboard.api.MVGResponse
import com.example.tabletdashboard.api.MVGRetrofitInstance
import com.example.tabletdashboard.api.Station
import com.example.tabletdashboard.api.WeatherRetrofitInstance
import com.example.tabletdashboard.repositories.MVGRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MVGDeparturesViewModel: ViewModel() {

    private val mvgRepository = MVGRepository()

    val selectedStation: MutableStateFlow<Station> = MutableStateFlow<Station>(Station.STAMMGELAENDE)
    private val _mvgResponse = MutableStateFlow<MVGResponse?>(null)
    val mvgResponse = _mvgResponse.asStateFlow()

    init {
        viewModelScope.launch {
            selectedStation.collect { station ->
                mvgRepository.fetchDepartures(station).collect { response ->
                    _mvgResponse.value = response
                }
            }
        }
        viewModelScope.launch {
            while (true) {
                mvgRepository.fetchDepartures(selectedStation.value).collect { response ->
                    _mvgResponse.value = response
                }
                delay(1 * 60 * 1000) // update every 1 minute
            }
        }
    }

    fun loadStation(station: Station) {
        selectedStation.value = station
    }

}