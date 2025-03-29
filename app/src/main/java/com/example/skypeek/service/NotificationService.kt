package com.example.skypeek.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.utiles.calculateDelay
import com.example.skypeek.worker.scheduleNotification
import com.google.gson.Gson


class NotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationJson = intent?.getStringExtra("location") ?: return START_NOT_STICKY
        val location = Gson().fromJson(locationJson, LocationPOJO::class.java)

        val delay = calculateDelay(8, 0) // Schedule for 8 AM
        scheduleNotification(this, location, delay)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}