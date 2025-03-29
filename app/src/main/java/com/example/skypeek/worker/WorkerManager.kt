package com.example.skypeek.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.skypeek.BuildConfig
import com.example.skypeek.composablescreens.alert.showNotification
import com.example.skypeek.data.local.WeatherDataBase
import com.example.skypeek.data.local.WeatherLocalDataSourceImpl
import com.example.skypeek.data.remote.RetrofitHelper
import com.example.skypeek.data.remote.WeatherRemoteDataSource
import com.example.skypeek.data.repository.WeatherRepository
import com.example.skypeek.data.repository.WeatherRepositoryImpl
import com.example.skypeek.utiles.SharedPreference
import com.example.skypeek.utiles.getFromSharedPrefrence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        return try {
            Log.d("Notification", "Worker executing at ${System.currentTimeMillis()}")
            val repo : WeatherRepository = WeatherRepositoryImpl(
                remoteDataSource = WeatherRemoteDataSource(
                    weatherApiService = RetrofitHelper.weatherApiService
                ),
                localDataSource = WeatherLocalDataSourceImpl(
                    weatherDao = WeatherDataBase.getInstance(applicationContext).dao()
                )
            )
            Log.i("TAG", "doWork() called ${getFromSharedPrefrence(applicationContext, "cityLat")}")
            Log.i("TAG", "doWork() called ${getFromSharedPrefrence(applicationContext, "cityLong")}")
            CoroutineScope(Dispatchers.IO).launch {
            val data = repo.fetchWeather(
                getFromSharedPrefrence(applicationContext, "cityLat")?.toDoubleOrNull() ?: 31.2433,
                getFromSharedPrefrence(applicationContext, "cityLong")?.toDoubleOrNull() ?:29.9187 ,
                BuildConfig.apiKeySafe,
                getFromSharedPrefrence(applicationContext, "temperature") ?: "Celsius",
                SharedPreference.getLanguage(applicationContext,"language")
            ).first()
                showNotification(applicationContext, data)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("Notification", "Failed to show notification", e)
            Result.retry()
        }
    }
}