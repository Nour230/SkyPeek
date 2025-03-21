package com.example.skypeek.composablescreens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skypeek.composablescreens.utiles.helpers.convertUnit
import com.example.skypeek.composablescreens.utiles.helpers.mapTemperatureUnit
import com.example.skypeek.data.models.ResponseState

import com.example.skypeek.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (private val repo:WeatherRepository):ViewModel(){
    private val mutableWeather = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val weather  = mutableWeather.asStateFlow()

    private val mutableHourlyWeather = MutableStateFlow<ResponseState>(ResponseState.Loading)
    val hourlyWeather  = mutableHourlyWeather.asStateFlow()

    private val mutableError = MutableSharedFlow<String>()
    val error  = mutableError.asSharedFlow()


    fun getWeather(lat: Double, lon: Double, apiKey: String,units:String) {
        val tempUnite = convertUnit(units)
        viewModelScope.launch {
            try {
                val response = repo.fetchWeather(lat, lon, apiKey,tempUnite)
                response.catch {ex->
                    mutableWeather.value = ResponseState.Error(ex)
                    mutableError.emit(ex.message.toString())
                }.collect{
                    mutableWeather.value = ResponseState.Success(it)
                }
            } catch (e: Exception) {
                mutableError.emit(e.message.toString())
                Log.e("HomeViewModel", "Error fetching weather: ${e.localizedMessage}")
            }
        }
    }


    fun getHourlyWeather(lat: Double, lon: Double, apiKey: String,units:String) {
        val tempUnite = convertUnit(units)
        viewModelScope.launch {
            try {
                mutableHourlyWeather.value = ResponseState.Loading // 🔹 Force UI to detect change

                val response = repo.fetchHourlyWeather(lat, lon, apiKey,tempUnite)
                response.catch { ex ->
                    Log.e("TAG", "getHourlyWeather: Error fetching weather -> ${ex.message}")
                    mutableHourlyWeather.value = ResponseState.Error(ex)
                    mutableError.emit(ex.message.toString())
                }.collect {
                    mutableHourlyWeather.value = ResponseState.SuccessForecast(it) // ✅ Success state
                }
            } catch (e: Exception) {
                mutableError.emit(e.message.toString())
            }
        }
    }


}



class WeatherFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}