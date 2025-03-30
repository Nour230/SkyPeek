package com.example.skypeek.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.core.content.edit
import com.example.skypeek.data.models.AlarmPojo

fun scheduleNotification(calendar: Calendar, alarm: AlarmPojo, context: Context) {
    Log.i("TAG", "scheduleNotification: ${calendar.timeInMillis}")

    val delay = calendar.timeInMillis - System.currentTimeMillis()
    if (delay > 0) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("persistent_notification")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        val workManager = WorkManager.getInstance(context.applicationContext)
        val uniqueWorkName = "notification_${calendar.timeInMillis}"

        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        // Store the scheduled time in SharedPreferences using a combination of time and date
        val prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE)
        prefs.edit().putLong("${alarm.time}_${alarm.date}", calendar.timeInMillis).apply()
    }
}
