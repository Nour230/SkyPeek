package com.example.skypeek.data.repository

import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow


interface WeatherRepository {
    suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String, units: String): Flow<CurrentWeather>
    suspend fun fetchHourlyWeather(lat: Double, lon: Double, apiKey: String, units: String): Flow<WeatherResponse>
}