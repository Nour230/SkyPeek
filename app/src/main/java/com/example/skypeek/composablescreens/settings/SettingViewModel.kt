package com.example.skypeek.composablescreens.settings

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModelProvider
import com.example.skypeek.composablescreens.home.HomeViewModel
import com.example.skypeek.data.repository.WeatherRepository

class SettingsViewModel : ViewModel() {

    private val _selectedLocation = mutableStateOf("GPS")
    val selectedLocation: State<String> = _selectedLocation

    private val _selectedTemperature = mutableStateOf("Celsius")
    val selectedTemperature: State<String> = _selectedTemperature

    private val _selectedLanguage = mutableStateOf("English")
    val selectedLanguage: State<String> = _selectedLanguage

    private val _selectedWindSpeed = mutableStateOf("miles/hour")
    val selectedWindSpeed: State<String> = _selectedWindSpeed

    fun setLocation(value: String) {
        _selectedLocation.value = value
    }

    fun setTemperature(value: String) {
        _selectedTemperature.value = value
    }

    fun setLanguage(value: String) {
        _selectedLanguage.value = value
    }

    fun setWindSpeed(value: String) {
        _selectedWindSpeed.value = value
    }
}


