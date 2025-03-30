package com.example.skypeek.utiles.helpers

import com.example.skypeek.utiles.enums.Temperature
import com.example.skypeek.utiles.enums.WindSpeed

fun mapTemperatureUnit(temperature: String): String {
    return when(temperature){
        Temperature.CELSIUS.toString() -> "metric"
        Temperature.FAHRENHEIT.toString() -> "imperial"
        Temperature.KELVIN.toString() -> "standard"
        else -> "metric"
    }
}

fun setUnitSymbol(temperature: String): String {
    return when(temperature){
        Temperature.CELSIUS.toString() -> "°C"
        Temperature.FAHRENHEIT.toString() -> "°F"
        Temperature.KELVIN.toString() -> "K"
        else -> "°C"
    }
}


fun mapWindSpeedUnit(windSpeed: String): String {
    return when(windSpeed){
        WindSpeed.METER_SEC.toString() -> "metric"
        WindSpeed.MILES_HOUR.toString() -> "imperial"
        else -> "standard"
    }
}

fun setWindSpeedSymbol(windSpeed: String): String {
    return when(windSpeed){
        WindSpeed.METER_SEC.toString() -> "m/s"
        WindSpeed.MILES_HOUR.toString() -> "mph"
        else -> "m/s"
    }
}

fun convertUnit(unit: String): String {
    return when {
        isTemperatureUnit(unit) -> mapTemperatureUnit(unit)
        isWindSpeedUnit(unit) -> mapWindSpeedUnit(unit)
        else -> "unknown"
    }
}

/**
 * Checks if the provided unit is a temperature unit.
 */
fun isTemperatureUnit(unit: String): Boolean {
    return unit == Temperature.CELSIUS.toString() ||
            unit == Temperature.FAHRENHEIT.toString() ||
            unit == Temperature.KELVIN.toString()
}

/**
 * Checks if the provided unit is a wind speed unit.
 */
fun isWindSpeedUnit(unit: String): Boolean {
    return unit == WindSpeed.METER_SEC.toString() ||
            unit == WindSpeed.MILES_HOUR.toString()
}



fun mapLanguageCodeToName(code: String): String {
    return when (code) {
        "en" -> "english"
        "ar" -> "arabic"
        "kr" -> "korean"
        else -> "english"
    }
}