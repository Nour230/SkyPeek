package com.example.skypeek.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.skypeek.data.models.AlarmPojo
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDAOTest {
    private lateinit var dataBase: WeatherDataBase
    private lateinit var dao: WeatherDao

    @Before
    fun serup() {
        dataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()
        dao = dataBase.dao()
    }

    @After
    fun tearDown() {
        dataBase.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAlarm_ShouldInsertAlarmCorectly() = runTest {
        // Given a test alarm
        val alarm = AlarmPojo(
            time = "08:00",
            date = "2025-04-01"
        )
        //when
        dao.insertAlarm(alarm)
        advanceUntilIdle()

        //then
        val alarms = dao.getAllAlarms().first()
        assertEquals(1, alarms.size)
        assertEquals("08:00", alarms.first().time)
        assertEquals("2025-04-01", alarms.first().date)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllAlarms_returnZero_whenNoAlarmsAdded() = runTest {
        //Given No Alarms in database

        //when getting all alarms
        val alarms = dao.getAllAlarms().first()
        advanceUntilIdle()

        //then
        assert(alarms.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAlarm_shoulddeleteanalarmsuccessfully() = runTest {
        // Insert alarm
        val alarm = AlarmPojo(time = "08:00", date = "2025-04-01", id = 1)
        dao.insertAlarm(alarm)

        // Delete alarm
        dao.deleteAlarm(alarm)
        advanceUntilIdle()

        // then check if alarm is deleted
        val alarms = dao.getAllAlarms().first()
        assertEquals(0, alarms.size)
    }


}