package com.example.tabletdashboard.repositories

import com.example.tabletdashboard.api.MensaMenuResponse
import com.example.tabletdashboard.api.MensaRetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MensaRepository {
    fun fetchMensaMenu(canteen: String, year: Int, weekNumber: Int) : Flow<MensaMenuResponse> = flow {
        val response = MensaRetrofitInstance.api.getMenu(canteen, year, weekNumber)
        emit(response)
    }
}