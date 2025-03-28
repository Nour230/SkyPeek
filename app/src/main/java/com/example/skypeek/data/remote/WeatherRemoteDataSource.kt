package com.example.skypeek.data.remote

import android.util.Log
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf


class WeatherRemoteDataSource(private val weatherApiService: WeatherApiService) : RemoteDataSource  {
    override suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang:String
    ): Flow<CurrentWeather> {
        return flowOf(weatherApiService.getCurrentWeatherData(lat, lon, apiKey,units,lang).body()) as Flow<CurrentWeather>
    }

    override suspend fun getHourlyWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang:String
    ): Flow<WeatherResponse> = flow {
        val response = weatherApiService.getHourlyWeatherData(lat, lon, apiKey,units,lang)
        if (response.isSuccessful && response.body() != null) {
            Log.d("TAG", "getHourlyWeather: ${response.body()}")
            emit(response.body()!!)
        } else {
            Log.e("TAG", "Failed to fetch hourly weather")
            throw Exception("Failed to fetch hourly weather")
        }
    }

}


