package com.example.skypeek.composablescreens.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skypeek.data.models.WeatherResponse

import com.example.skypeek.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class HomeViewModel (private val repo:WeatherRepository):ViewModel(){
    private val mutableWeather = MutableLiveData<WeatherResponse?>()
    val weather : MutableLiveData<WeatherResponse?> = mutableWeather

    private val mutableError : MutableLiveData<String> = MutableLiveData()
    val error : MutableLiveData<String> = mutableError
    fun getWeather(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repo.fetchWeather(lat, lon, apiKey)
                if (response != null) {
                    Log.i("TAG", "Weather data received: $response")
                    mutableWeather.value = response
                } else {
                    Log.e("TAG", "API Response is null")
                    mutableError.value = "Failed to fetch weather data: No data received"
                }
            } catch (e: Exception) {
                Log.e("TAG", "getWeather Exception: ${e.message}")
                mutableError.value = "Failed to fetch weather data: ${e.message}"
            }
        }
    }

}



class WeatherFactory(private val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}