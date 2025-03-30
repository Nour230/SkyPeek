package com.example.skypeek.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "home")
data class HomePOJO(@PrimaryKey val id :Int=1,val currentWeather: CurrentWeather,val forcast:WeatherResponse)
