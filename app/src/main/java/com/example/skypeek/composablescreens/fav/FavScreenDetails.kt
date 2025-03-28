package com.example.skypeek.composablescreens.fav

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.skypeek.composablescreens.home.HomeViewModel
import com.example.skypeek.composablescreens.home.TopBar
import com.example.skypeek.composablescreens.home.Weather
import com.example.skypeek.composablescreens.home.WeatherForecastScreen
import com.example.skypeek.composablescreens.home.WeatherScreen
import com.example.skypeek.composablescreens.home.getNightWeatherLottie
import com.example.skypeek.composablescreens.home.getWeatherLottie
import com.example.skypeek.utiles.getFromSharedPrefrence
import com.example.skypeek.utiles.helpers.setUnitSymbol
import com.example.skypeek.utiles.helpers.setWindSpeedSymbol
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.ResponseState
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.gray
import com.example.skypeek.utiles.SharedPreference
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.content.Context
import com.example.skypeek.utiles.helpers.formatNumberBasedOnLanguage
import com.example.skypeek.utiles.helpers.formatTemperatureUnitBasedOnLanguage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavDetailsScreen(
    data : LocationPOJO,
    viewModel: HomeViewModel,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>
) {
    Log.i("TAG", "FavDetailsScreen: $data ")
    isFAB.value = false
    isNAV.value = false
    val locationState = remember { mutableStateOf<Location?>(null) }
    locationState.value = Location("").apply {
        latitude = data.lat
        longitude = data.long
    }
    val context = LocalContext.current
    val currentWeather by viewModel.weather.collectAsStateWithLifecycle()
    val currentHourlyWeather by viewModel.hourlyWeather.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        locationState.value?.let { location ->
            viewModel.getWeather(
                location.latitude,
                location.longitude,
                BuildConfig.apiKeySafe,
                getFromSharedPrefrence(context, "temperature") ?: "Celsius",
                SharedPreference.getLanguage(context,"language")
            )
        }
    }

    LaunchedEffect(Unit) {
        locationState.value?.let { location ->
            viewModel.getHourlyWeather(
                location.latitude,
                location.longitude,
                BuildConfig.apiKeySafe,
                getFromSharedPrefrence(context, "temperature") ?: "Celsius",
                SharedPreference.getLanguage(context,"language")
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        cardBackGround, gray, cardBackGround
                    )
                )
            )
    ) {
        LazyColumn {
            item {
                when (currentWeather) {
                    is ResponseState.Error -> {
                        WeatherFavDetailsScreen(data.currentWeather)
                        Log.e("TAG", "FavDetailsScreen: offline")
                        Log.e("TAG", "FavDetailsScreen: ${currentWeather as ResponseState.Error}")
                    }
                    is ResponseState.Loading -> {
                        com.example.skypeek.composablescreens.home.LoadingIndicatore()
                    }
                    is ResponseState.Success -> {
                        WeatherFavDetailsScreen((currentWeather as ResponseState.Success).data)
                    }
                    else -> {
                        Log.i("TAG", "HomeScreen: Unexpected state -> $currentWeather")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                when (currentHourlyWeather) {
                    is ResponseState.Error -> {
                        Weather(data.forecast)
                        WeatherForecastScreen(data.forecast)
                        Log.i("TAG", "FavDetailsScreen: offline ")
                        Log.i("TAG", "FavDetailsScreen: ${currentHourlyWeather as ResponseState.Error}")
                    }
                    is ResponseState.Loading -> {
                        com.example.skypeek.composablescreens.home.LoadingIndicatore()
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
fun WeatherFavDetailsScreen(currentweather: CurrentWeather) {
    val context = LocalContext.current
    val city = currentweather.name
    val mainWeather = currentweather.main

    val cityOffsetSeconds = currentweather.timezone
    val cityZoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(cityOffsetSeconds))
    val dateTimeInCity = Instant.ofEpochSecond(currentweather.dt.toLong()).atZone(cityZoneId)

    val isAM = dateTimeInCity.hour < 12
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            if (isAM)
                getWeatherLottie(currentweather.weather[0].main)
            else
                getNightWeatherLottie(currentweather.weather[0].main)
        )
    )

    val formattedDate = dateTimeInCity.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))
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
            TopBar(location = city)

            Spacer(modifier = Modifier.height(32.dp))

            WeatherFavDetailsMainInfo(
                temperature = mainWeather.temp.toInt(),
                desc = weather.firstOrNull()?.description ?: "N/A",
                cloud = currentweather.clouds.all.toString(),
                composition = composition,
                units = getFromSharedPrefrence(context, "temperature") ?: "Celsius"
            )

            Spacer(modifier = Modifier.height(22.dp))

            WeatherFavDetails(
                realFeel = mainWeather.feels_like.toInt(),
                humidity = mainWeather.humidity,
                windSpeed = currentweather.wind.speed.toString(),
                pressure = mainWeather.pressure.toString(),
                date = formatNumberBasedOnLanguage(context,formattedDate),
                units = getFromSharedPrefrence(context, "windspeed") ?: "m/s",
                max = mainWeather.temp_max.toInt().toString(),
                min = mainWeather.temp_min.toInt().toString()
            )
        }
    }
}

@Composable
fun WeatherFavDetailsMainInfo(
    temperature: Int,
    composition: LottieComposition?,
    desc: String,
    cloud: String,
    units: String
) {
    val context = LocalContext.current
    val tempUnit = formatTemperatureUnitBasedOnLanguage(setUnitSymbol(units))
    val formattedTemp = formatNumberBasedOnLanguage(context, temperature)
    val formattedCloud = formatNumberBasedOnLanguage(context, cloud)

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
            text = "$formattedTemp $tempUnit",
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
            text = "${stringResource(R.string.cloud)}: $formattedCloud%",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
fun WeatherFavDetails(
    realFeel: Int,
    humidity: Int,
    windSpeed: String,
    pressure: String,
    date: String,
    units: String,
    max: String,
    min: String
) {
    val context = LocalContext.current
    val windUnit = formatTemperatureUnitBasedOnLanguage(setWindSpeedSymbol(units))
    val formattedRealFeel = formatNumberBasedOnLanguage(context, realFeel)
    val formattedHumidity = formatNumberBasedOnLanguage(context, humidity)
    val formattedWindSpeed = formatNumberBasedOnLanguage(context, windSpeed)
    val formattedPressure = formatNumberBasedOnLanguage(context, pressure)
    val formattedMax = formatNumberBasedOnLanguage(context, max)
    val formattedMin = formatNumberBasedOnLanguage(context, min)

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
                WeatherFavDetailItem(
                    icon = R.drawable.temperature,
                    label = stringResource(R.string.realfeel),
                    value = "$formattedRealFeel°"
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(180.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherFavDetailItem(
                    icon = R.drawable.humidity,
                    label = stringResource(R.string.humidity),
                    value = "$formattedHumidity%"
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(180.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherFavDetailItem(
                    icon = R.drawable.max_temp,
                    label = stringResource(R.string.min_temp),
                    value = "$formattedMin °"
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(160.dp)
                    .background(Color.White.copy(alpha = 0.5f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WeatherFavDetailItem(
                    icon = R.drawable.wind,
                    label = stringResource(R.string.wind),
                    value = "$formattedWindSpeed $windUnit"
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(180.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherFavDetailItem(
                    icon = R.drawable.pressure,
                    label = stringResource(R.string.pressure),
                    value = formattedPressure
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(180.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherFavDetailItem(
                    icon = R.drawable.max_temp,
                    label = stringResource(R.string.max_temp),
                    value = "$formattedMax °"
                )
            }
        }
    }
}

@Composable
fun WeatherFavDetailItem(icon: Int, label: String, value: String) {
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

