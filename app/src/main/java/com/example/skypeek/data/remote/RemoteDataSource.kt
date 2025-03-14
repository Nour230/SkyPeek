package com.example.skypeek.data.remote

import WeatherResponse


interface RemoteDataSource {
    suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): WeatherResponse?
}