package com.example.skypeek.composablescreens.alert

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.skypeek.MainActivity
import com.example.skypeek.R
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.service.StopSoundReceiver
import kotlinx.coroutines.flow.Flow

private var mediaPlayer: MediaPlayer? = null
fun showNotification(context: Context, weatherData: CurrentWeather) {
    val channelId = "message_channel"
    val notificationId = 1 // Unique ID for this notification

    playNotificationSound(context) // Start alarm sound

    // Intent to stop sound, open MainActivity, and cancel notification
    val openIntent = Intent(context, StopSoundReceiver::class.java).apply {
        putExtra("open_main", true)
        putExtra("notification_id", notificationId) // Pass the notification ID
    }
    val openPendingIntent = PendingIntent.getBroadcast(
        context, 1, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Intent to stop sound and cancel notification
    val stopSoundIntent = Intent(context, StopSoundReceiver::class.java).apply {
        putExtra("notification_id", notificationId) // Pass the notification ID
    }
    val stopSoundPendingIntent = PendingIntent.getBroadcast(
        context, 0, stopSoundIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.wind)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logoicon))
        .setContentTitle("Current Temperature is ${weatherData.main.temp}")
        .setContentText("Check out the weather in ${weatherData.name}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .addAction(R.drawable.wind, "Open", openPendingIntent)
        .addAction(R.drawable.wind, "Cancel", stopSoundPendingIntent)
        .build()

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notification)
    }
}
fun playNotificationSound(context: Context) {
    if (mediaPlayer == null) {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.notification).apply {
                isLooping = true
                start()
            }
            Log.d("Sound", "MediaPlayer created and started")
        } catch (e: Exception) {
            Log.e("Sound", "Error creating MediaPlayer", e)
        }
    }
}

fun stopNotificationSound() {
    mediaPlayer?.stop()
    mediaPlayer?.release()
    mediaPlayer = null
}