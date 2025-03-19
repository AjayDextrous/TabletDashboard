package com.example.tabletdashboard.repositories

import com.example.tabletdashboard.api.Station
import com.example.tabletdashboard.api.MVGResponse
import com.example.tabletdashboard.api.MVGRetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MVGRepository {
    fun fetchDepartures(station: Station): Flow<MVGResponse> = flow {
        val response = MVGRetrofitInstance.api.getDepartures(nameDm = station.apiName)
        emit(response)
    }
}