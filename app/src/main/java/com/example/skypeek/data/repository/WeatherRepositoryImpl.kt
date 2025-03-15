package com.example.skypeek.data.repository


import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.data.remote.RemoteDataSource

class WeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource) : WeatherRepository {

    override suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String): WeatherResponse? {
        return remoteDataSource.getDailyWeather(lat, lon, apiKey)
    }
}