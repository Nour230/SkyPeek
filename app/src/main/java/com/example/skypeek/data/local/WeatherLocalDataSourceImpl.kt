package com.example.skypeek.data.local

import com.example.skypeek.data.models.LocationPOJO
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(private val weatherDao: WeatherDao) :WeatherLocalDataSource {
    override suspend fun insertLocation(location: LocationPOJO) {
       weatherDao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: LocationPOJO) {
        weatherDao.deleteLocation(location)
    }

    override fun getAllLocations(): Flow<List<LocationPOJO>> {
      return weatherDao.getAllLocations()
    }


}