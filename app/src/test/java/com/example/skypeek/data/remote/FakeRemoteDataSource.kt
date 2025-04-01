package com.example.skypeek.data.remote

import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

//class FakeRemoteDataSource : RemoteDataSource{
//    override suspend fun getDailyWeather(
//        lat: Double,
//        lon: Double,
//        apiKey: String,
//        units: String,
//        lang: String
//    ): Flow<CurrentWeather> {
//        return flowOf(CurrentWeather())
//    }
//
//    override suspend fun getHourlyWeather(
//        lat: Double,
//        lon: Double,
//        apiKey: String,
//        units: String,
//        lang: String
//    ): Flow<WeatherResponse> {
//        return flowOf(WeatherResponse())
//    }
//
//}