package com.example.tabletdashboard.api

import android.util.Log
import com.example.tabletdashboard.api.Station.entries
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
enum class Station(val stationName: String, val apiName: String) {
    STAMMGELAENDE("Technische Universität", "91000095"),
    OLYMPIAPARK("Olympiazentrum", "91000350"),
    KLINIKUM_RECHTS("Max-Weber-Platz", "91000580"),
    GROSSHADERN("Klinikum Großhadern", "91001540"),
    GARCHING("Forschungszentrum", "1000460"),
    FREISING("Freising, Weihenstephan", "1002911"),
    IMPLERSTRASSE("Implerstraße", "91001140");

    companion object {
        fun fromApiName(apiName: String): Station? {
            return entries.find { it.apiName == apiName }
        }
    }
}

data class MVGDepartureData(
    val departureList: List<Departure>
)

data class Departure(
    val stopID: String,
    val stopName: String,
    val countdown: String,
    val servingLine: ServingLine
)

data class ServingLine(
    val key: String,
    val code: String,
    val number: String,
    val symbol: String,
    val direction: String,
    val name: String,
)


interface MVGApi {

    @GET("ng/XML_DM_REQUEST")
    suspend fun getDepartures(
        @Query("outputFormat") outputFormat: String = "JSON",
        @Query("language") language: String = "en",
        @Query("stateless") stateless: String = "1",
        @Query("coordOutputFormat") coordOutputFormat: String = "WGS84",
        @Query("type_dm") typeDm: String = "stop",
        @Query("name_dm") nameDm: String,
        @Query("timeOffset") timeOffset: String? = null,
        @Query("useRealtime") useRealtime: String = "1",
        @Query("itOptionsActive") itOptionsActive: String = "1",
        @Query("ptOptionsActive") ptOptionsActive: String = "1",
        @Query("limit") limit: String = "20",
        @Query("mergeDep") mergeDep: String = "1",
        @Query("useAllStops") useAllStops: String = "1",
        @Query("mode") mode: String = "direct"
    ): Response<MVGDepartureData>

}

object MVGRetrofitInstance {
    val api: MVGApi by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message -> Log.d(TAG, message) }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).build()

        Retrofit.Builder()
            .baseUrl("https://efa.mvv-muenchen.de")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(MVGApi::class.java)
    }

    private const val TAG = "MVGRetrofitInstance"
}