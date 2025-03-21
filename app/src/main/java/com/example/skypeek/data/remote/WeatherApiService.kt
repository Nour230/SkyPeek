package com.example.skypeek.data.remote

import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/forecast")
    suspend fun getHourlyWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Response<WeatherResponse>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Response<CurrentWeather>
}