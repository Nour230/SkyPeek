package com.example.skypeek.composablescreens.home.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.HomePOJO
import com.example.skypeek.data.models.ResponseState
import com.example.skypeek.data.models.ResponseStateLocal
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.data.repository.WeatherRepository
import com.example.skypeek.utiles.helpers.convertUnit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val mutableWeather = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val weather = mutableWeather.asStateFlow()

    private val mutableLocalWeather = MutableStateFlow<ResponseStateLocal>(ResponseStateLocal.Loading)
    val localCurrentWeather = mutableLocalWeather.asStateFlow()

    private val mutableHourlyWeather = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val hourlyWeather = mutableHourlyWeather.asStateFlow()

    private val _insertionError = MutableSharedFlow<String>()
    val insertionError = _insertionError.asSharedFlow()

    private val mutableError = MutableSharedFlow<String>()
    val error = mutableError.asSharedFlow()

    fun getWeather(lat: Double, lon: Double, apiKey: String, units: String, lang: String) {
        val tempUnit = convertUnit(units)
        viewModelScope.launch {
            try {
                mutableWeather.value = ResponseState.Loading
                repo.fetchWeather(lat, lon, apiKey, tempUnit, lang)
                    .collect { response ->
                        mutableWeather.value = ResponseState.Success(response)
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching weather: ${e.localizedMessage}")
                mutableError.emit(e.message.toString())
                mutableWeather.value = ResponseState.Error(e)
            }
        }
    }

    fun getHourlyWeather(lat: Double, lon: Double, apiKey: String, units: String, lang: String) {
        val tempUnite = convertUnit(units)
        viewModelScope.launch {
            try {
                mutableHourlyWeather.value = ResponseState.Loading
                val response = repo.fetchHourlyWeather(lat, lon, apiKey, tempUnite, lang)
                response.catch { ex ->
                    Log.e("TAG", "getHourlyWeather: Error fetching weather -> ${ex.message}")
                    mutableHourlyWeather.value = ResponseState.Error(ex)
                    mutableError.emit(ex.message.toString())
                }.collect {
                    mutableHourlyWeather.value =
                        ResponseState.SuccessForecast(it) // ✅ Success state
                }
            } catch (e: Exception) {
                mutableError.emit(e.message.toString())
            }
        }
    }

    fun insertHome(homeCurrent: CurrentWeather, homeHourly: WeatherResponse) {
        val home = HomePOJO(currentWeather = homeCurrent, forcast = homeHourly)
        viewModelScope.launch {
            try {
                repo.insertLastHome(home)
            } catch (e: Exception) {
                _insertionError.emit(e.message ?: "Unknown error")
            }
        }
    }

    fun getLastHome() {
        viewModelScope.launch {
            repo.getLastHome().collect {
                mutableLocalWeather.value = ResponseStateLocal.Success(it)
            }
        }
    }
}


class WeatherFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}