package com.example.skypeek.data.repository

import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.HomePOJO
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow


interface WeatherRepository {
    suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String, units: String, lang:String): Flow<CurrentWeather>
    suspend fun fetchHourlyWeather(lat: Double, lon: Double, apiKey: String, units: String, lang:String): Flow<WeatherResponse>
    suspend fun insertLocation(locationPOJO: LocationPOJO)
    suspend fun deleteLocation(locationPOJO: LocationPOJO)
    fun getAllLocations(): Flow<List<LocationPOJO>>
    suspend fun insertAlarm(alarmPojo: AlarmPojo)
    suspend fun deleteAlarm(alarmPojo: AlarmPojo)
    fun getAllAlarms(): Flow<List<AlarmPojo>>

    fun getLastHome(): Flow<HomePOJO>
    suspend fun insertLastHome(homePOJO: HomePOJO)

}