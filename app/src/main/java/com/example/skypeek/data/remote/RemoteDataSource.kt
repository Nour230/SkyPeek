package com.example.skypeek.data.remote

import com.example.skypeek.data.models.WeatherResponse


interface RemoteDataSource {
    suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): WeatherResponse?
}