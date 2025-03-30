package com.example.skypeek.data.models

sealed class ResponseState {
    data object Loading: ResponseState()
    data class Success(val data: CurrentWeather): ResponseState()
    data class Error(val message: Throwable): ResponseState()
    data class SuccessForecast(val data: WeatherResponse): ResponseState()
}

sealed class ResponseStateLocal {
    data object Loading: ResponseStateLocal()
    data class Success(val data: HomePOJO): ResponseStateLocal()
    data class Error(val message: Throwable): ResponseStateLocal()
}

sealed class ResponseStateFav {
    data object Loading: ResponseStateFav()
    data class Success(val data: List<LocationPOJO>): ResponseStateFav()
    data class Error(val message: Throwable): ResponseStateFav()
}

sealed class ResponseStateAlarm {
    data object Loading: ResponseStateAlarm()
    data class Success(val data: List<AlarmPojo>): ResponseStateAlarm()
    data class Error(val message: Throwable): ResponseStateAlarm()
}


