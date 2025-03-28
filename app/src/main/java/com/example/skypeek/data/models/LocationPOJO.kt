package com.example.skypeek.data.models

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.skypeek.utiles.Converters

@TypeConverters(Converters::class)
@Entity(tableName = "location", primaryKeys = ["lat", "long"])
data class LocationPOJO(
   val lat: Double,
   val long: Double,
   val currentWeather: CurrentWeather,
   val forecast:WeatherResponse,
   val city :String
)
