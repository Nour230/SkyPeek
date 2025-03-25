package com.example.skypeek.composablescreens.home

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.BuildConfig
import com.example.skypeek.R
import com.example.skypeek.composablescreens.utiles.getFromSharedPrefrence
import com.example.skypeek.composablescreens.utiles.helpers.setUnitSymbol
import com.example.skypeek.composablescreens.utiles.helpers.setWindSpeedSymbol
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.ResponseState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    locationState: MutableState<Location?>,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>
) {
    isNAV.value=true
    isFAB.value = false
    val context = LocalContext.current
    val currentWeather by homeViewModel.weather.collectAsStateWithLifecycle()
    val currentHourlyWeather by homeViewModel.hourlyWeather.collectAsStateWithLifecycle()

    LaunchedEffect(locationState.value) {
        locationState.value?.let { location ->
            homeViewModel.getWeather(
                location.latitude,
                location.longitude,
                BuildConfig.apiKeySafe,
                getFromSharedPrefrence(context, "temperature") ?: "Celsius"
            )
        }
    }


    LaunchedEffect(locationState.value) {
        locationState.value?.let { location ->
            homeViewModel.getHourlyWeather(
                location.latitude,
                location.longitude,
                BuildConfig.apiKeySafe,
                getFromSharedPrefrence(context, "temperature") ?: "Celsius"
            )
        }
    }

    Image(
        painter = painterResource(R.drawable.night2),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        LazyColumn {

            // Current Weather Section
            item {
                when (currentWeather) {
                    is ResponseState.Error -> {
                        Text(
                            text = "Error: ${(currentWeather as ResponseState.Error).message}",
                            color = Color.White
                        )
                    }

                    is ResponseState.Loading -> {
                        LoadingIndicatore()
                    }

                    is ResponseState.Success -> {
                        WeatherScreen((currentWeather as ResponseState.Success).data)
                    }

                    else -> {
                        Log.i("TAG", "HomeScreen: Unexpected state -> $currentWeather")
                    }
                }
            }

            // Spacer to separate sections
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Hourly Weather Section
            item {
                when (currentHourlyWeather) {
                    is ResponseState.Error -> {
                        Text(
                            text = "Error: ${(currentHourlyWeather as ResponseState.Error).message}",
                            color = Color.White
                        )
                    }

                    is ResponseState.Loading -> {
                        LoadingIndicatore()
                    }

                    is ResponseState.SuccessForecast -> {
                        Weather((currentHourlyWeather as ResponseState.SuccessForecast).data)
                        WeatherForecastScreen((currentHourlyWeather as ResponseState.SuccessForecast).data)

                    }

                    else -> {
                        Log.i(
                            "TAG",
                            "HomeScreen: currentHourlyWeather type -> ${currentHourlyWeather::class.java.simpleName}"
                        )
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherScreen(currentweather: CurrentWeather) {
    val city = currentweather.name

    val mainWeather = currentweather.main
    val nyZoneId = ZoneId.of("Africa/Cairo")
    val dateTimeInNY = Instant.ofEpochSecond(currentweather.dt.toLong()).atZone(nyZoneId)
    val isAM = dateTimeInNY.hour < 12
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            if (isAM)
                getWeatherLottie(currentweather.weather[0].main)
            else
                getNightWeatherLottie(currentweather.weather[0].main)
        )
    )
    // Format the output
    val formattedDate = dateTimeInNY.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))
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
                desc = weather.firstOrNull()?.description ?: "N/A",
                cloud = currentweather.clouds.all.toString(),
                composition = composition,
                units = getFromSharedPrefrence(LocalContext.current, "temperature") ?: "Celsius"
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Additional Weather Details
            WeatherDetails(
                realFeel = mainWeather.feels_like.toInt(),
                humidity = mainWeather.humidity,
                windSpeed = currentweather.wind.speed.toString(),
                pressure = mainWeather.pressure.toString(),
                date = formattedDate,
                units = getFromSharedPrefrence(LocalContext.current, "windspeed") ?: "m/s"
            )
        }
    }
}


@Composable
fun WeatherMainInfo(
    temperature: Int, composition:
    LottieComposition?, desc: String, cloud: String, units: String
) {
    val tempUnit = setUnitSymbol(units)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = 2.0f,
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        )
        Text(
            text = "$temperature $tempUnit",
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
    date: String,
    units: String
) {
    val windUnit = setWindSpeedSymbol(units)
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
                WeatherDetailItem(
                    icon = R.drawable.wind,
                    label = "Wind",
                    value = "$windSpeed $windUnit"
                )
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


@Composable
fun LoadingIndicatore() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator()
    }
}


fun getWeatherLottie(weatherCondition: String): Int {
    return when (weatherCondition.lowercase()) {
        "clear" -> R.raw.sun_animation // Replace with actual Lottie file in res/raw
        "clouds" -> R.raw.cloud
        "rain" -> R.raw.rain
        "snow" -> R.raw.snow
        "thunderstorm" -> R.raw.thunderstorm
        else -> R.raw.wind_animation
    }
}

fun getNightWeatherLottie(weatherCondition: String): Int {
    return when (weatherCondition.lowercase()) {
        "clear" -> R.raw.clear_night
        "clouds" -> R.raw.cloud_night
        "rain" -> R.raw.rain
        "snow" -> R.raw.snow
        "thunderstorm" -> R.raw.thunderstorm_night
        else -> R.raw.wind_animation
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