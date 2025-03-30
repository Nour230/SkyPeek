package com.example.skypeek.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.utiles.Converters
import com.example.skypeek.data.models.LocationPOJO
@TypeConverters(Converters::class)
@Database(entities = [LocationPOJO::class, AlarmPojo::class], version = 5, exportSchema = false)
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}