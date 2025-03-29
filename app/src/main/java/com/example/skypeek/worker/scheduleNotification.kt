package com.example.skypeek.worker


import android.content.Context
import androidx.work.*
import com.example.skypeek.data.models.LocationPOJO
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

fun scheduleNotification(context: Context, location: LocationPOJO, delayInMillis: Long) {
    val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
        .setInputData(workDataOf("location" to Gson().toJson(location))) // Pass data
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}