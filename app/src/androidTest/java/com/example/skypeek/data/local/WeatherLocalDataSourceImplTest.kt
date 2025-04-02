package com.example.skypeek.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.data.models.HomePOJO
import com.example.skypeek.data.models.LocationPOJO
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest {
    private lateinit var dataBase: WeatherDataBase
    private lateinit var dao: WeatherDao
    private lateinit var localDataSourceImpl: WeatherLocalDataSourceImpl

    @Before
    fun setup(){
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()
        dao = dataBase.dao()
        localDataSourceImpl = WeatherLocalDataSourceImpl(dao)
    }
    @After
    fun tearDown() = dataBase.close()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertLocation_ShouldInsertLocationCorrectly() = runTest {
        // Given a test location
        val location = LocationPOJO(
            lat = 40.7128,
            long = -74.0060,
            currentWeather = mockk(relaxed = true),
            forecast = mockk(relaxed = true),
            city = "cairo",
        )

        //when
        localDataSourceImpl.insertLocation(location)
        advanceUntilIdle()

        // then
        val locations = localDataSourceImpl.getAllLocations().first()
        assertThat(locations.size,`is`(1))
        assertThat(locations[0].city,`is`(location.city))

    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testInsertHome() = runTest {
        // Given
        val home = HomePOJO(
            id = 1,
            currentWeather = mockk(relaxed = true),
            forcast = mockk(relaxed = true)
        )

        // When
        localDataSourceImpl.insertHome(home)
        advanceUntilIdle()

        // Then
        val result = localDataSourceImpl.getAllHomes().first()
        assertThat(result, `is`(home))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testInsertAndDeleteAlarm() = runTest {
        // Given an alarm object
        val alarm = AlarmPojo(
            id = 1,
            time = "08:00 AM",
            date = "2025-04-01"
        )
        val alarm2 = AlarmPojo(
            id = 2,
            time = "10:00 AM",
            date = "2025-04-01"
        )

        // When inserting the alarm
        localDataSourceImpl.insertAlarm(alarm)
        localDataSourceImpl.insertAlarm(alarm2)

        // Then assert the alarm has been inserted
        val insertedAlarms = localDataSourceImpl.getAllAlarms().first()
        assertThat(insertedAlarms.size, `is`(2))
        assertThat(insertedAlarms[0].id, `is`(alarm.id))

        // When deleting the alarm
        localDataSourceImpl.deleteAlarm(alarm)
        advanceUntilIdle()

        // Then assert the alarm has been deleted
        val remainingAlarms = localDataSourceImpl.getAllAlarms().first()
        assertThat(remainingAlarms.size, `is`(1))
    }


}