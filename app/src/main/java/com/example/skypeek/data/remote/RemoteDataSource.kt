package com.example.skypeek.data.remote

import com.example.skypeek.data.models.CurrentWeather
import kotlinx.coroutines.flow.Flow


interface RemoteDataSource {
    suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<CurrentWeather>
}