package com.example.skypeek.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skypeek.data.models.LocationPOJO

@Database(entities = [LocationPOJO::class], version = 2)
abstract class WeatherDataBase : RoomDatabase(){
    abstract fun dao(): WeatherDao
    companion object{
        const val DATABASE_NAME = "weather_db"
        @Volatile
        private var INSTANCE : WeatherDataBase? = null
        fun getInstance (ctx: Context): WeatherDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    WeatherDataBase::class.java,
                    DATABASE_NAME)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}