//package com.example.skypeek.data.models
//
//import com.google.gson.annotations.SerializedName
//
//data class WeatherResponse(
//    val lat: Double,
//    val lon: Double,
//    val timezone: String,
//    @SerializedName("timezone_offset") val timezoneOffset: Int,
//    val current: CurrentWeather,
//    val minutely: List<MinutelyWeather>?,
//    val hourly: List<HourlyWeather>?,
//    val daily: List<DailyWeather>?,
//    val alerts: List<WeatherAlert>?
//)
//
//data class CurrentWeather(
//    val dt: Long,
//    val sunrise: Long,
//    val sunset: Long,
//    val temp: Double,
//    @SerializedName("feels_like") val feelsLike: Double,
//    val pressure: Int,
//    val humidity: Int,
//    @SerializedName("dew_point") val dewPoint: Double,
//    val uvi: Double,
//    val clouds: Int,
//    val visibility: Int,
//    @SerializedName("wind_speed") val windSpeed: Double,
//    @SerializedName("wind_deg") val windDeg: Int,
//    @SerializedName("wind_gust") val windGust: Double?,
//    val weather: List<WeatherDescription>
//)
//
//data class MinutelyWeather(
//    val dt: Long,
//    val precipitation: Double
//)
//
//data class HourlyWeather(
//    val dt: Long,
//    val temp: Double,
//    @SerializedName("feels_like") val feelsLike: Double,
//    val pressure: Int,
//    val humidity: Int,
//    @SerializedName("dew_point") val dewPoint: Double,
//    val uvi: Double,
//    val clouds: Int,
//    val visibility: Int,
//    @SerializedName("wind_speed") val windSpeed: Double,
//    @SerializedName("wind_deg") val windDeg: Int,
//    @SerializedName("wind_gust") val windGust: Double?,
//    val weather: List<WeatherDescription>,
//    val pop: Double
//)
//
//data class DailyWeather(
//    val dt: Long,
//    val sunrise: Long,
//    val sunset: Long,
//    val moonrise: Long,
//    val moonset: Long,
//    @SerializedName("moon_phase") val moonPhase: Double,
//    val summary: String?,
//    val temp: Temperature,
//    @SerializedName("feels_like") val feelsLike: FeelsLike,
//    val pressure: Int,
//    val humidity: Int,
//    @SerializedName("dew_point") val dewPoint: Double,
//    @SerializedName("wind_speed") val windSpeed: Double,
//    @SerializedName("wind_deg") val windDeg: Int,
//    @SerializedName("wind_gust") val windGust: Double?,
//    val weather: List<WeatherDescription>,
//    val clouds: Int,
//    val pop: Double,
//    val rain: Double?,
//    val uvi: Double
//)
//
//data class Temperature(
//    val day: Double,
//    val min: Double,
//    val max: Double,
//    val night: Double,
//    val eve: Double,
//    val morn: Double
//)
//
//data class FeelsLike(
//    val day: Double,
//    val night: Double,
//    val eve: Double,
//    val morn: Double
//)
//
//data class WeatherDescription(
//    val id: Int,
//    val main: String,
//    val description: String,
//    val icon: String
//)
//
//data class WeatherAlert(
//    @SerializedName("sender_name") val senderName: String,
//    val event: String,
//    val start: Long,
//    val end: Long,
//    val description: String,
//    val tags: List<String>
//)
//



data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherData>,
    val city: City
)

data class WeatherData(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: Sys,
    val dt_txt: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Sys(
    val pod: String
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)