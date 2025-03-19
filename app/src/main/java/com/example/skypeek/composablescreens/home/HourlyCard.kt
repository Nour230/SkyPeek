package com.example.skypeek.composablescreens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.data.models.WeatherData
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.ui.theme.black
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Weather(weather: WeatherResponse) {
    val currentDate = SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault()).format(Date())
    val currentDayWeather = weather.list.filter {
        it.dt_txt.startsWith(currentDate)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentDayWeather.size) {
                DailyWeatherItem(currentDayWeather[it])
            }
        }
    }
}


@Composable
fun DailyWeatherItem(weather: WeatherData) {
    val mainWeather = weather.main
    val date = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(weather.dt * 1000))
// Load Lottie animation dynamically
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(getWeatherLottie(weather.weather[0].main))
    )
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(100.dp)
            .height(150.dp)
            .alpha(0.8f)

    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(), // Safe because the parent Card has a fixed size
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )
            Text(
                text = date.take(10),
                fontSize = 14.sp,
                color = black
            )
            Text(
                text = "${mainWeather.temp.toInt()}°C",
                color = black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}