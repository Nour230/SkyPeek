package com.example.skypeek.data.models

sealed class ResponseState {
    data object Loading: ResponseState()
    data class Success(val data: CurrentWeather): ResponseState()
    data class Error(val message: Throwable): ResponseState()
    data class SuccessForecast(val data: WeatherResponse): ResponseState()
}

