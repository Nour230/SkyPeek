package com.example.skypeek.composablescreens.home

import android.graphics.Color
import android.icu.text.DateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.R
import com.example.skypeek.data.models.WeatherData
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.ui.theme.black
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.gray
import com.example.skypeek.ui.theme.white
import com.example.skypeek.ui.theme.yellow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


//display (min amd max temp) , icon ,( wind , date )
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherForecastScreen(weather: WeatherResponse) {
    val data = weather.list

    // Get distinct days
    val distinctDays = data.map { it.dt_txt.split(" ")[0] }.distinct()

    // Calculate average min/max temp for each day
    val averageData = distinctDays.mapNotNull { day ->
        val dailyEntries = data.filter { it.dt_txt.startsWith(day) }

        if (dailyEntries.isNotEmpty()) {
            val avgTempMin = dailyEntries.map { it.main.temp_min }.average()
            val avgTempMax = dailyEntries.map { it.main.temp_max }.average()

            dailyEntries.first().copy(
                main = dailyEntries.first().main.copy(
                    temp_min = avgTempMin,
                    temp_max = avgTempMax
                )
            )
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
    ) {
        averageData.forEach { weather ->
            WeatherItem(weather)
        }
    }
     Spacer(modifier = Modifier.height(24.dp))
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherItem(weather: WeatherData) {
    val hour = weather.dt_txt.split(" ")[1].split(":")[0].toInt() // Extract hour from dt_txt
    val isDayTime = hour in 0..12 // Consider 6 AM to 6 PM as day time

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            if (isDayTime) getWeatherLottie(weather.weather[0].main)
            else getNightWeatherLottie(weather.weather[0].main)
        )
    )

    val dayName = getDayName(weather.dt_txt)
    val mainWeather = weather.main

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .alpha(0.8f),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = getMonthAndDay(weather.dt_txt),
                    color = black,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = dayName.substring(0..2),
                    color = black,
                    fontSize = 18.sp
                )
            }
            Text(
                text = "${mainWeather.temp_max.toInt()}°",
                color = black
            )
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )
            Text(
                text = "${mainWeather.temp_min.toInt()}°",
                color = black
            )
            Text(text = weather.weather[0].main, color = black)
        }
        HorizontalDivider(
            color = gray,
            thickness = 2.dp,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun getDayName(dateString: String): String {
    return try {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = LocalDate.parse(dateString.split(" ")[0], DateTimeFormatter.ISO_DATE)
        date.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.FULL,
            Locale.getDefault()
        ) // E.g., "Monday"
    } catch (e: Exception) {
        "Unknown"  // Fallback in case of error
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getMonthAndDay(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString.split(" ")[0], DateTimeFormatter.ISO_DATE)
        val formatter = DateTimeFormatter.ofPattern("MM/d", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        "Unknown"
    }

}
