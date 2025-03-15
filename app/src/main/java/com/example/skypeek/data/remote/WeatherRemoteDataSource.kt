package com.example.skypeek.data.remote

import android.util.Log
import com.example.skypeek.data.models.WeatherResponse


class WeatherRemoteDataSource(private val weatherApiService: WeatherApiService) : RemoteDataSource  {
    override suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        apiKey: String
    ): WeatherResponse {

        Log.i("TAG", "API Key: $apiKey, Lat: $lat, Lon: $lon") // 🔍 Log API request details

        val response = weatherApiService.getWeatherData(lat, lon, apiKey)

        if (response.isSuccessful) {
            Log.i("TAG", "Success: ${response.body()}")
            return response.body()?: throw Exception("Empty response body")
        } else {
            Log.e("TAG", "API Error: ${response.code()} - ${response.message()}") // Log error
            throw Exception("Failed to fetch weather data: ${response.message()}")
        }
    }
}


