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
        const val DEFAULT_LANGUAGE = "system"
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
    )
    val selectedTemperature: State<String> = _selectedTemperature

    // Language state
    private val _selectedLanguage = mutableStateOf(
        getFromSharedPrefrence(context, PREF_LANGUAGE) ?: DEFAULT_LANGUAGE
    )
    val selectedLanguage: State<String> = _selectedLanguage

    // Wind speed state
    private val _selectedWindSpeed = mutableStateOf(
        getFromSharedPrefrence(context, PREF_WIND_SPEED) ?: DEFAULT_WIND_SPEED
    )
    val selectedWindSpeed: State<String> = _selectedWindSpeed

    fun setLocation(value: String) {
        _selectedLocation.value = value
        saveToSharedPrefrence(context, value, PREF_LOCATION)
    }

    fun setTemperature(value: String) {
        _selectedTemperature.value = value
        saveToSharedPrefrence(context, value, PREF_TEMPERATURE)
    }

    fun setLanguage(value: String) {
        _selectedLanguage.value = value // ✅ Update UI state immediately
        saveToSharedPrefrence(context, value, PREF_LANGUAGE)
    }

    fun setWindSpeed(value: String) {
        _selectedWindSpeed.value = value
        saveToSharedPrefrence(context, value, PREF_WIND_SPEED)
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