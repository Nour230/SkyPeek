package com.example.skypeek.composablescreens.home

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.BuildConfig
import com.example.skypeek.R
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.ResponseState
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.ui.theme.white
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, locationState: MutableState<Location?>) {
    val currentWeather by homeViewModel.weather.collectAsStateWithLifecycle()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clearnight))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )


    LaunchedEffect(locationState.value) {
        locationState.value?.let { location ->
            homeViewModel.getWeather(
                location.latitude,
                location.longitude,
                BuildConfig.apiKeySafe
            )
        }
    }

    // Wrap the screen inside a Box with a background color
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.night2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Semi-transparent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)) // Adjust alpha for transparency
        )

        Column(modifier = Modifier.padding(16.dp)) {
            when(currentWeather){
                is ResponseState.Error -> {
                    Text(text = "Error: ${(currentWeather as ResponseState.Error).message}", color = white)
                }
                is ResponseState.Loading -> LoadingIndicatore()
                is ResponseState.Success -> WeatherScreen((currentWeather as ResponseState.Success).data)
            }
        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(currentweather: CurrentWeather) {
    val city = currentweather.sys.country
    val mainWeather = currentweather.main
    val nyZoneId = ZoneId.of("Africa/Cairo")
    // Convert timestamp to ZonedDateTime in New York time zone
    val dateTimeInNY = Instant.ofEpochSecond(currentweather.dt.toLong()).atZone(nyZoneId)
    // Format the output
    val formattedDate = dateTimeInNY.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val weather = currentweather.weather

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            TopBar(location = city)

            Spacer(modifier = Modifier.height(32.dp))

            // Weather Icon and Temperature
            WeatherMainInfo(
                temperature = mainWeather.temp.toInt(),
                weatherIcon = R.drawable.sun_rain,
                desc = weather.firstOrNull()?.description ?: "N/A",
                cloud = currentweather.clouds.all.toString()
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Additional Weather Details
            WeatherDetails(
                realFeel = mainWeather.feels_like.toInt(),
                humidity = mainWeather.humidity,
                windSpeed = currentweather.wind.speed.toString(),
                pressure = mainWeather.pressure.toString(),
                date = formattedDate
            )
        }
    }
}

@Composable
fun TopBar(location: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = Color.White,
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = location,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeatherMainInfo(temperature: Int, weatherIcon: Int, desc: String, cloud: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = weatherIcon),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "$temperature°C",
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = desc,
            color = Color.White,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Cloud: $cloud%",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
fun WeatherDetails(
    realFeel: Int,
    humidity: Int,
    windSpeed: String,
    pressure: String,
    date: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = date,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WeatherDetailItem(
                    icon = R.drawable.temperature,
                    label = "RealFeel",
                    value = "$realFeel°"
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(180.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetailItem(
                    icon = R.drawable.humidity,
                    label = "Humidity",
                    value = "$humidity%"
                )
            }
            // Vertical Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(130.dp) // Adjust height as needed
                    .background(Color.White.copy(alpha = 0.5f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WeatherDetailItem(icon = R.drawable.wind, label = "Wind", value = windSpeed)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(180.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetailItem(icon = R.drawable.pressure, label = "Pressure", value = pressure)
            }
        }
    }
}


@Composable
fun WeatherDetailItem(icon: Int, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
@Composable
fun DailyWeatherItem(weather: CurrentWeather, index: Int) {
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


@Composable
fun HouerlyWeather(homeViewModel: HomeViewModel) {
    val currentWeather by homeViewModel.weather.collectAsStateWithLifecycle()



}


@Composable
private fun Weather(weather: CurrentWeather){
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { contentPadding ->
        LazyRow(
            modifier = Modifier.padding(contentPadding)
        ) {
            items(weather.size) { index ->
                DailyWeatherItem(weather!!, index)
            }
        }
    }
}



*/
fun getWeatherIcon(condition: String): Int {
    return when (condition.lowercase()) {
        "clear" -> R.drawable.snow
        "clouds" -> R.drawable.snow
        "rain" -> R.drawable.sun_rain
        "thunderstorm" -> R.drawable.wind
        else -> R.drawable.sun_rain
    }
}



@Composable
private fun LoadingIndicatore(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator()
    }
}
