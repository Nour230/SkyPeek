package com.example.skypeek.data.repository


import com.example.skypeek.data.local.WeatherLocalDataSource
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource, private val localDataSource: WeatherLocalDataSource) : WeatherRepository {

    override suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String, units: String): Flow<CurrentWeather> {
        return remoteDataSource.getDailyWeather(lat, lon, apiKey, units)
    }

    override suspend fun fetchHourlyWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): Flow<WeatherResponse> {
        return remoteDataSource.getHourlyWeather(lat, lon, apiKey,units)
    }

    override suspend fun insertLocation(locationPOJO: LocationPOJO) {
        localDataSource.insertLocation(locationPOJO)
    }

    override suspend fun deleteLocation(locationPOJO: LocationPOJO) {
        localDataSource.deleteLocation(locationPOJO)
    }

    override fun getAllLocations(): Flow<List<LocationPOJO>> {
        return localDataSource.getAllLocations()
    }


}