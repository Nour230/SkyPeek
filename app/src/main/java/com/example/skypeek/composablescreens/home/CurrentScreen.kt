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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.skypeek.utiles.getFromSharedPrefrence
import com.example.skypeek.utiles.helpers.internet.ConnectivityObserver
import com.example.skypeek.utiles.helpers.internet.checkForInternet
import com.example.skypeek.utiles.helpers.setUnitSymbol
import com.example.skypeek.utiles.helpers.setWindSpeedSymbol
import com.example.skypeek.data.models.CurrentWeather
import com.example.skypeek.data.models.ResponseState
import com.example.skypeek.data.models.ResponseStateLocal
import com.example.skypeek.utiles.SharedPreference
import com.example.skypeek.utiles.deleteSharedPrefrence
import com.example.skypeek.utiles.helpers.formatNumberBasedOnLanguage
import com.example.skypeek.utiles.helpers.formatTemperatureUnitBasedOnLanguage
import com.example.skypeek.utiles.saveToSharedPrefrence
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    locationState: MutableState<Location?>,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>
) {
    isNAV.value = true
    isFAB.value = false
    val context = LocalContext.current

    val currentWeather by homeViewModel.weather.collectAsStateWithLifecycle()
    val currentHourlyWeather by homeViewModel.hourlyWeather.collectAsStateWithLifecycle()
    val currentLocalWeather by homeViewModel.localCurrentWeather.collectAsStateWithLifecycle()
    val connectivityObserver = remember { ConnectivityObserver(context) }
    val isConnected by connectivityObserver.isConnected.collectAsStateWithLifecycle(
        initialValue = checkForInternet(
            context
        )
    )

    // Lottie composition for error animation
    val errorComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.animation) // Replace with your Lottie file
    )

    LaunchedEffect(isConnected) {
        if (isConnected) {
            locationState.value?.let { location ->
                homeViewModel.getHourlyWeather(
                    getFromSharedPrefrence(context, "lat")?.toDoubleOrNull() ?: location.latitude,
                    getFromSharedPrefrence(context, "long")?.toDoubleOrNull() ?: location.longitude,
                    BuildConfig.apiKeySafe,
                    getFromSharedPrefrence(context, "temperature") ?: "Celsius",
                    SharedPreference.getLanguage(context,"language")
                )
            }
            locationState.value?.let { location ->
                homeViewModel.getWeather(
                    getFromSharedPrefrence(context, "lat")?.toDoubleOrNull() ?: location.latitude,
                    getFromSharedPrefrence(context, "long")?.toDoubleOrNull() ?: location.longitude,
                    BuildConfig.apiKeySafe,
                    getFromSharedPrefrence(context, "temperature") ?: "Celsius",
                    SharedPreference.getLanguage(context,"language")
                )
            }
        }else{
            homeViewModel.getLastHome()
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
            // Spacer to separate sections
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Hourly Weather Section
            item {
                when {

                    !isConnected -> {
                       when(currentLocalWeather){
                           is ResponseStateLocal.Error -> ErrorAnimation(errorComposition)
                           is ResponseStateLocal.Loading -> LoadingIndicatore()
                           is ResponseStateLocal.Success -> {
                               WeatherScreen(((currentLocalWeather as ResponseStateLocal.Success).data.currentWeather))
                               Weather(((currentLocalWeather as ResponseStateLocal.Success).data.forcast))
                               WeatherForecastScreen(((currentLocalWeather as ResponseStateLocal.Success).data.forcast))
                           }
                       }
                    }

                    currentHourlyWeather is ResponseState.Error -> {

                        Text(text = "Error loading hourly weather data ${currentHourlyWeather as ResponseState.Error}")
                    }

                    currentHourlyWeather is ResponseState.Loading -> {
                        LoadingIndicatore()
                    }

                    currentHourlyWeather is ResponseState.SuccessForecast && currentWeather is ResponseState.Success-> {
                        WeatherScreen((currentWeather as ResponseState.Success).data)
                        Weather((currentHourlyWeather as ResponseState.SuccessForecast).data)
                        WeatherForecastScreen((currentHourlyWeather as ResponseState.SuccessForecast).data)
                        homeViewModel.insertHome(
                            (currentWeather as ResponseState.Success).data,
                            (currentHourlyWeather as ResponseState.SuccessForecast).data
                        )
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
    val context = LocalContext.current
    val city = currentweather.name

    val mainWeather = currentweather.main

    // Get the city's timezone offset (in seconds) from OpenWeatherMap API
    val cityOffsetSeconds = currentweather.timezone
    val cityZoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(cityOffsetSeconds))

    // Convert Unix timestamp to local time of the city
    val dateTimeInCity = Instant.ofEpochSecond(currentweather.dt.toLong()).atZone(cityZoneId)

    deleteSharedPrefrence(context, "cityLat")
    deleteSharedPrefrence(context, "cityLong")
    saveToSharedPrefrence(context, currentweather.coord.lat.toString(), "cityLat")
    saveToSharedPrefrence(context, currentweather.coord.lon.toString(), "cityLong")
    val isAM = dateTimeInCity.hour in 6..18
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            if (isAM)
                getWeatherLottie(currentweather.weather[0].main)
            else
                getNightWeatherLottie(currentweather.weather[0].main)
        )
    )
    // Format the output
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
            // Top Bar
            TopBar(location = city)

            Spacer(modifier = Modifier.height(32.dp))
            val lang = SharedPreference.getLanguage(context = LocalContext.current, "language")
            val tempText = if (lang == "ar") {
                formatNumberBasedOnLanguage(LocalContext.current, mainWeather.temp.toInt().toString())
            } else {
                mainWeather.temp.toInt().toString()
            }
            // Weather Icon and Temperature
            WeatherMainInfo(
                temperature = tempText,
                desc = weather.firstOrNull()?.description ?: "N/A",
                cloud = currentweather.clouds.all.toString(),
                composition = composition,
                units = getFromSharedPrefrence(context, "temperature") ?: "Celsius"
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Additional Weather Details
            WeatherDetails(
                realFeel = mainWeather.feels_like.toInt(),
                humidity = mainWeather.humidity,
                windSpeed = currentweather.wind.speed.toString(),
                pressure = mainWeather.pressure.toString(),
                date = formatNumberBasedOnLanguage(context,formattedDate),
                units = getFromSharedPrefrence(context, "windspeed") ?: "m/s"
            )
        }
    }
}


@Composable
fun WeatherMainInfo(
    temperature: String, // Changed to String
    composition: LottieComposition?,
    desc: String,
    cloud: String,
    units: String
) {
    val context = LocalContext.current
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
            text = temperature +formatTemperatureUnitBasedOnLanguage(tempUnit),
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
            text = stringResource(R.string.cloud) +" ${formatNumberBasedOnLanguage(context, cloud)}%",
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
    val context = LocalContext.current
    val windUnit = setWindSpeedSymbol(units)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = date, // Date formatting should already respect locale
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
                    label = stringResource(R.string.realfeel),
                    value = "${formatNumberBasedOnLanguage(context, realFeel)}°"
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
                    label = stringResource(R.string.humidity),
                    value = "${formatNumberBasedOnLanguage(context, humidity)}%"
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(130.dp)
                    .background(Color.White.copy(alpha = 0.5f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WeatherDetailItem(
                    icon = R.drawable.wind,
                    label = stringResource(R.string.wind),
                    value = formatNumberBasedOnLanguage(context, windSpeed) + formatTemperatureUnitBasedOnLanguage(windUnit)
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.width(180.dp),
                    thickness = 1.dp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetailItem(
                    icon = R.drawable.pressure,
                    label = stringResource(R.string.pressure),
                    value = formatNumberBasedOnLanguage(context, pressure)
                )
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


@Composable
fun ErrorAnimation(composition: LottieComposition?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(500.dp)
        )
        Text(
            text = "Error loading weather data",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}