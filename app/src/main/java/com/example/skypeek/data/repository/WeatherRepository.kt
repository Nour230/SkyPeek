package com.example.skypeek.data.repository

import WeatherResponse


interface WeatherRepository {
    suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse?
}