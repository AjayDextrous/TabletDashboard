package com.example.tabletdashboard.api

import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("generationtime_ms") val generationTimeMs: Double,
    @SerializedName("utc_offset_seconds") val utcOffsetSeconds: Int,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("timezone_abbreviation") val timezoneAbbreviation: String,
    @SerializedName("elevation") val elevation: Double,
    @SerializedName("current_weather_units") val currentWeatherUnits: CurrentWeatherUnits,
    @SerializedName("current_weather") val currentWeather: CurrentWeather
)

data class CurrentWeatherUnits(
    @SerializedName("time") val time: String,
    @SerializedName("interval") val interval: String,
    @SerializedName("temperature") val temperature: String,
    @SerializedName("windspeed") val windSpeed: String,
    @SerializedName("winddirection") val windDirection: String,
    @SerializedName("is_day") val isDay: String,
    @SerializedName("weathercode") val weatherCode: String
)

data class CurrentWeather(
    @SerializedName("time") val time: String,
    @SerializedName("interval") val interval: Int,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("windspeed") val windSpeed: Double,
    @SerializedName("winddirection") val windDirection: Int,
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("weathercode") val weatherCode: Int
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse
}

object WeatherRetrofitInstance {
    val api: WeatherApi by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message -> Log.d(TAG, message) }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).build()

        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WeatherApi::class.java)
    }

    private const val TAG = "WeatherRetrofitInstance"
}
