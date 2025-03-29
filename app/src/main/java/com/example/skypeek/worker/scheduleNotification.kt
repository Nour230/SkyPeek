package com.example.skypeek.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.core.content.edit

fun scheduleNotification(calendar: Calendar, context: Context) {
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

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(
                "notification_${calendar.timeInMillis}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        // Persist this schedule in SharedPreferences
        context.getSharedPreferences("notifications", Context.MODE_PRIVATE)
            .edit() {
                putLong("next_notification", calendar.timeInMillis)
            }
    }
}