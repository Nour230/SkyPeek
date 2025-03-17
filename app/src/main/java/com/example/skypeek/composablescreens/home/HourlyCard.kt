package com.example.skypeek.composablescreens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skypeek.data.models.ResponseState
import com.example.skypeek.data.models.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
 fun Weather(weather: WeatherResponse){
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { contentPadding ->
        LazyRow(
            modifier = Modifier.padding(contentPadding)
        ) {
            items(weather.list.size) { index ->
                DailyWeatherItem(weather, index)
            }
        }
    }
}

@Composable
fun DailyWeatherItem(weather: WeatherResponse, index: Int) {
    val weatherItem = weather.list.getOrNull(index) ?: return
    val mainWeather = weatherItem.main
    val date = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(weatherItem.dt * 1000))

    Card(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight()
            .wrapContentWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(getWeatherIcon(weatherItem.weather.firstOrNull()?.main ?: "N/A")),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = date,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${mainWeather.temp.toInt()}°C",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}