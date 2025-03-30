package com.example.skypeek.data.repository


import com.example.skypeek.data.local.WeatherLocalDataSource
import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.HomePOJO
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource, private val localDataSource: WeatherLocalDataSource) : WeatherRepository {

    override suspend fun fetchWeather(lat: Double, lon: Double, apiKey: String, units: String, lang:String): Flow<CurrentWeather> {
        return remoteDataSource.getDailyWeather(lat, lon, apiKey, units,lang)
    }

    override suspend fun fetchHourlyWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String,
        lang:String
    ): Flow<WeatherResponse> {
        return remoteDataSource.getHourlyWeather(lat, lon, apiKey,units,lang)
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

    override suspend fun insertAlarm(alarmPojo: AlarmPojo) {
        localDataSource.insertAlarm(alarmPojo)
    }

    override suspend fun deleteAlarm(alarmPojo: AlarmPojo) {
        localDataSource.deleteAlarm(alarmPojo)
    }

    override fun getAllAlarms(): Flow<List<AlarmPojo>> {
        return localDataSource.getAllAlarms()
    }

    override fun getLastHome(): Flow<HomePOJO> {
        return localDataSource.getAllHomes()
    }

    override suspend fun insertLastHome(homePOJO: HomePOJO) {
        localDataSource.insertHome(homePOJO)
    }


}