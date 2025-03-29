package com.example.skypeek.composablescreens.alert

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.skypeek.MainActivity
import com.example.skypeek.R
import com.example.skypeek.data.models.CurrentWeather
import kotlinx.coroutines.flow.Flow


fun showNotification(context: Context, weatherData: CurrentWeather) {
    val channelId = "message_channel"

    // Intent for Reply Action - Uses Deep Linking
    val replyIntent = Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_VIEW
        data = "favDetails".toUri() // Deep link to favDetails without parameters
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val replyPendingIntent = PendingIntent.getActivity(
        context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.wind)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logoicon))
        .setContentTitle("Current Temperature is ${weatherData.main.temp}")
        .setContentText("Check out the weather in ${weatherData.name}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(R.drawable.wind, "Open", replyPendingIntent) // Navigate to FavDetailsScreen
        .addAction(R.drawable.wind, "Cancel", replyPendingIntent) // Navigate to FavDetailsScreen
        .build()

    with(NotificationManagerCompat.from(context)) {
        notify(1, notification)
    }
}