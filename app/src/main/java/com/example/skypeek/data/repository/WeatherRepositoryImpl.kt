package com.example.skypeek.data.repository


import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource) : WeatherRepository {

    override suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String): Flow<CurrentWeather> {
        return remoteDataSource.getDailyWeather(lat, lon, apiKey)
    }
}