package com.example.skypeek.data.remote

import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow


interface RemoteDataSource {
    suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): Flow<CurrentWeather>

    suspend fun getHourlyWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): Flow<WeatherResponse>
}