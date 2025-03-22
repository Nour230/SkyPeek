package com.example.skypeek.data.local

import com.example.skypeek.data.models.LocationPOJO
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun insertLocation(location: LocationPOJO)
    suspend fun deleteLocation(location: LocationPOJO)
    fun  getAllLocations(): Flow<List<LocationPOJO>>

}