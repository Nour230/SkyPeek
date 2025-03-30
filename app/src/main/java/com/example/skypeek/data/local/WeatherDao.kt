package com.example.skypeek.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.data.models.HomePOJO
import com.example.skypeek.data.models.LocationPOJO
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM location")
    fun getAllLocations(): Flow<List<LocationPOJO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationPOJO)

    @Delete
    suspend fun deleteLocation(location: LocationPOJO)

    @Query("SELECT * FROM alarm")
    fun getAllAlarms(): Flow<List<AlarmPojo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(location: AlarmPojo)

    @Delete
    suspend fun deleteAlarm(location: AlarmPojo)


    @Query("SELECT * FROM home")
    fun getLastHome(): Flow<HomePOJO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertLastHome(home: HomePOJO)
}