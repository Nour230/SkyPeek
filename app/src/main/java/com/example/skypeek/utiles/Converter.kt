package com.example.skypeek.utiles

import androidx.room.TypeConverter
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCurrentWeather(currentWeather: CurrentWeather): String {
        return gson.toJson(currentWeather)
    }

    @TypeConverter
    fun toCurrentWeather(data: String): CurrentWeather {
        val type = object : TypeToken<CurrentWeather>() {}.type
        return gson.fromJson(data, type)
    }

    @TypeConverter
    fun fromForecast(forecast: WeatherResponse): String {
        return gson.toJson(forecast)
    }

    @TypeConverter
    fun toForecast(data: String): WeatherResponse {
        val type = object : TypeToken<WeatherResponse>() {}.type
        return gson.fromJson(data, type)
    }
}

fun millisToTime(millis: Long): String {
    val date = Date(millis)
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(date)
}