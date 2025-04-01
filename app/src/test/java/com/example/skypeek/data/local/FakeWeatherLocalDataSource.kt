package com.example.skypeek.data.local

import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.data.models.HomePOJO
import com.example.skypeek.data.models.LocationPOJO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherLocalDataSource : WeatherLocalDataSource {

    private val locations = mutableListOf<LocationPOJO>()
    private val alarms = mutableListOf<AlarmPojo>()
    private var lastHome: HomePOJO? = null

    override suspend fun insertLocation(location: LocationPOJO) {
        locations.add(location)
    }

    override suspend fun deleteLocation(location: LocationPOJO) {
        locations.remove(location)
    }

    override fun getAllLocations(): Flow<List<LocationPOJO>> {
        return flowOf(locations)
    }

    override suspend fun insertAlarm(alarmPojo: AlarmPojo) {
        alarms.add(alarmPojo)
    }

    override suspend fun deleteAlarm(alarmPojo: AlarmPojo) {
        alarms.remove(alarmPojo)
    }

    override fun getAllAlarms(): Flow<List<AlarmPojo>> {
        return flowOf(alarms)
    }

    override suspend fun insertHome(homePojo: HomePOJO) {
        lastHome = homePojo
    }

    override fun getAllHomes(): Flow<HomePOJO> {
        return flowOf(lastHome ?: throw IllegalStateException("No home data available"))
    }

}