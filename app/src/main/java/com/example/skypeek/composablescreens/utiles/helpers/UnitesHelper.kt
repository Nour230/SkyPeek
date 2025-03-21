package com.example.skypeek.composablescreens.utiles.helpers

import com.example.skypeek.composablescreens.utiles.enums.Temperature
import com.example.skypeek.composablescreens.utiles.enums.WindSpeed

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
        WindSpeed.MERE_SEC.toString() -> "metric"
        WindSpeed.MILES_HOUR.toString() -> "imperial"
        else -> "standard"
    }
}

fun setWindSpeedSymbol(windSpeed: String): String {
    return when(windSpeed){
        WindSpeed.MERE_SEC.toString() -> "m/s"
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
    return unit == WindSpeed.MERE_SEC.toString() ||
            unit == WindSpeed.MILES_HOUR.toString()
}