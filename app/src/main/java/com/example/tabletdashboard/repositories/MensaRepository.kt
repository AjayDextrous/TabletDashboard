package com.example.tabletdashboard.repositories

import com.example.tabletdashboard.api.MensaMenu
import com.example.tabletdashboard.api.MensaRetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

class MensaRepository {
    fun fetchMensaMenu(canteen: String, year: Int, weekNumber: Int) : Flow<Result<MensaMenu>> = flow {
        try {
            val response = MensaRetrofitInstance.api.getMenu(canteen, year, weekNumber)
            if(response.isSuccessful){
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch mensa menu")))
            }
        } catch (e: IOException){
            emit(Result.failure(Exception("Network error $e")))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Unexpected error $e")))
        }
    }
}