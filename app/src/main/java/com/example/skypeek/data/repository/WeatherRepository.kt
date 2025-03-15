package com.example.skypeek.data.repository

import com.example.skypeek.data.models.WeatherResponse


interface WeatherRepository {
    suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse?
}