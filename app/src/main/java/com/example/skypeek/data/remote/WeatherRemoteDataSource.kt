package com.example.skypeek.data.remote

import com.example.skypeek.data.models.CurrentWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class WeatherRemoteDataSource(private val weatherApiService: WeatherApiService) : RemoteDataSource  {
    override suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<CurrentWeather> {

        return flowOf(weatherApiService.getCurrentWeatherData(lat, lon, apiKey).body()) as Flow<CurrentWeather>
    }
}


