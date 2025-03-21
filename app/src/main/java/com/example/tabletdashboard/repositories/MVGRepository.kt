package com.example.tabletdashboard.repositories

import com.example.tabletdashboard.api.Station
import com.example.tabletdashboard.api.MVGDepartureData
import com.example.tabletdashboard.api.MVGRetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class MVGRepository {
    fun fetchDepartures(station: Station): Flow<Result<MVGDepartureData>> = flow {
        try {
            val response = MVGRetrofitInstance.api.getDepartures(nameDm = station.apiName)
            if(response.isSuccessful){
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch departures")))
            }
        } catch (e: IOException){
            emit(Result.failure(Exception("Network error $e")))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Unexpected error $e")))
        }
    }
}