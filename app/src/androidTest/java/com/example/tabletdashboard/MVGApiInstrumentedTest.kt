package com.example.tabletdashboard

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tabletdashboard.api.Station
import com.example.tabletdashboard.api.MVGDepartureData
import com.example.tabletdashboard.api.MVGRetrofitInstance
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class MVGApiInstrumentedTest {

    @Test
    fun testGetDepartures() = runBlocking {
        try {
            val station = Station.GARCHING  // Choose a station
            val response = MVGRetrofitInstance.api.getDepartures(nameDm = station.apiName)

            println(response.displayString())

        } catch (e: Exception) {
            fail("API call failed: ${e.message}")
        }
    }

    private fun MVGDepartureData.displayString() {
        println("no. departures = ${departureList.size}")
        departureList.forEach {
            println("Departure: ${it.stopName} in ${it.countdown} minutes")
        }
    }
}
