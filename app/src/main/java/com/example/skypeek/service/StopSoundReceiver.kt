package com.example.skypeek.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.skypeek.MainActivity
import com.example.skypeek.composablescreens.alert.stopNotificationSound

class StopSoundReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 1)

        // Cancel the notification
        NotificationManagerCompat.from(context).cancel(notificationId)

        stopNotificationSound()

        if (intent.getBooleanExtra("open_main", false)) {
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(mainIntent)
        }
    }
}