package com.example.tabletdashboard.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletdashboard.api.MVGDepartureData
import com.example.tabletdashboard.api.Station
import com.example.tabletdashboard.repositories.MVGRepository
import com.example.tabletdashboard.tools.AsyncState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MVGDeparturesViewModel(private val mvgRepository: MVGRepository): ViewModel() {

    val selectedStation: MutableStateFlow<Station> = MutableStateFlow<Station>(Station.STAMMGELAENDE)
    private val _mvgResponse = MutableStateFlow<AsyncState<MVGDepartureData>>(AsyncState.Loading)
    val mvgResponse = _mvgResponse.asStateFlow()

    init {
        viewModelScope.launch {
            selectedStation.collect { station ->
                mvgRepository.fetchDepartures(station).collect { response ->
                    response.fold(
                        onSuccess = {
                            _mvgResponse.value = AsyncState.Success(it)
                        },
                        onFailure = {
                            _mvgResponse.value = AsyncState.Error(it.toString())
                        }
                    )
                }
            }
        }
        viewModelScope.launch {
            while (true) {
                mvgRepository.fetchDepartures(selectedStation.value).collect { response ->
                    response.fold(
                        onSuccess = {
                            _mvgResponse.value = AsyncState.Success(it)
                        },
                        onFailure = {
                            _mvgResponse.value = AsyncState.Error(it.toString())
                        }
                    )
                }
                delay(1 * 60 * 1000) // update every 1 minute
            }
        }
    }

    fun loadStation(station: Station) {
        selectedStation.value = station
    }

    fun fetchManually() {
        viewModelScope.launch {
            mvgRepository.fetchDepartures(selectedStation.value).collect { response ->
                response.fold(
                    onSuccess = {
                        _mvgResponse.value = AsyncState.Success(it)
                    },
                    onFailure = {
                        _mvgResponse.value = AsyncState.Error(it.toString())
                    }
                )
            }
        }
    }

}