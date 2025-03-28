package com.example.skypeek.composablescreens.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skypeek.utiles.getFromSharedPrefrence
import com.example.skypeek.utiles.saveToSharedPrefrence

class SettingsViewModel(private val context: Context) : ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
        private const val PREF_LOCATION = "location"
        private const val PREF_TEMPERATURE = "temperature"
        private const val PREF_LANGUAGE = "language"
        private const val PREF_WIND_SPEED = "windspeed"

        const val DEFAULT_LOCATION = "gps"
        const val DEFAULT_TEMPERATURE = "celsius"
        const val DEFAULT_LANGUAGE = "english"
        const val DEFAULT_WIND_SPEED = "miles/hour"
    }

    // Location state
    private val _selectedLocation = mutableStateOf(
        getFromSharedPrefrence(context, PREF_LOCATION) ?: DEFAULT_LOCATION
    )
    val selectedLocation: State<String> = _selectedLocation

    // Temperature state
    private val _selectedTemperature = mutableStateOf(
        getFromSharedPrefrence(context, PREF_TEMPERATURE) ?: DEFAULT_TEMPERATURE
    ).also {
        Log.d(TAG, "Initial Temperature: ${it.value}")
    }
    val selectedTemperature: State<String> = _selectedTemperature

    // Language state
    private val _selectedLanguage = mutableStateOf(
        getFromSharedPrefrence(context, PREF_LANGUAGE) ?: DEFAULT_LANGUAGE
    )
    val selectedLanguage: State<String> = _selectedLanguage

    // Wind speed state
    private val _selectedWindSpeed = mutableStateOf(
        getFromSharedPrefrence(context, PREF_WIND_SPEED) ?: DEFAULT_WIND_SPEED
    ).also {
        Log.d(TAG, "Initial Wind Speed: ${it.value}")
    }
    val selectedWindSpeed: State<String> = _selectedWindSpeed

    fun setLocation(value: String) {
        _selectedLocation.value = value
        saveToSharedPrefrence(context, value, PREF_LOCATION)
        Log.d(TAG, "Saved Location: $value")
    }

    fun setTemperature(value: String) {
        _selectedTemperature.value = value
        saveToSharedPrefrence(context, value, PREF_TEMPERATURE)
        Log.d(TAG, "Saved Temperature: $value")
    }

    fun setLanguage(value: String) {
        _selectedLanguage.value = value
        saveToSharedPrefrence(context, value, PREF_LANGUAGE)
        Log.d(TAG, "Saved Language: $value")
    }

    fun setWindSpeed(value: String) {
        _selectedWindSpeed.value = value
        saveToSharedPrefrence(context, value, PREF_WIND_SPEED)
        Log.d(TAG, "Saved Wind Speed: $value")
    }


}

class SettingFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}