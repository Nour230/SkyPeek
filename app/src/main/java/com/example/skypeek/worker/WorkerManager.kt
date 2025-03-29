package com.example.skypeek.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.skypeek.composablescreens.alert.showNotification
import com.example.skypeek.data.models.LocationPOJO
import com.google.gson.Gson

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val locationJson = inputData.getString("location") ?: return Result.failure()
        val location = Gson().fromJson(locationJson, LocationPOJO::class.java)

        showNotification(applicationContext, location) // Trigger notification

        return Result.success()
    }
}