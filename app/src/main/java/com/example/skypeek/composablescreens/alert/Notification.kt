package com.example.skypeek.composablescreens.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import com.example.skypeek.MainActivity
import com.example.skypeek.R
import com.example.skypeek.data.models.LocationPOJO
import com.google.gson.Gson

 fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "message_channel"
        val name = "Messages"
        val descriptionText = "Notification for new messages"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


private fun showNotification(context: Context, locationPOJO: LocationPOJO) {
    val channelId = "message_channel"

    val gson = Gson()
    val jsonString = gson.toJson(locationPOJO) // Convert object to JSON

    // Intent for Reply Action - Uses Deep Linking
    val replyIntent = Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_VIEW
        data = "app://skypeek/favDetails/$jsonString".toUri() // Deep link to favDetails
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val replyPendingIntent = PendingIntent.getActivity(
        context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.wind)
        .setContentTitle("Favorite Location")
        .setContentText("Check out this favorite place!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(R.drawable.alarm, "Reply", replyPendingIntent) // Navigate to FavDetailsScreen
        .addAction(R.drawable.alarm, "Cancel", replyPendingIntent) // Navigate to FavDetailsScreen
        .build()

    with(NotificationManagerCompat.from(context)) {
        notify(1, notification)
    }
}

@Composable
fun NotificationScreen(pojo : LocationPOJO) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { showNotification(
            context,
            locationPOJO = pojo
        ) }) {
            Text(text = "Show Notification")
        }
    }
}