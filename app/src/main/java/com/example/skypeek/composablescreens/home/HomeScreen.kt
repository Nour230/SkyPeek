package com.example.skypeek.composablescreens.home

import android.location.Location
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.BuildConfig
import com.example.skypeek.R
import com.example.skypeek.data.models.WeatherResponse
import com.example.skypeek.ui.theme.white
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(homeViewModel: HomeViewModel, locationState: MutableState<Location?>) {
    val currentWeather by homeViewModel.weather.observeAsState()
    val errorMessage by homeViewModel.error.observeAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.clearnight)) // Your Lottie JSON
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
        Image(
            painter = painterResource(R.drawable.nn),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.padding(16.dp)) {
            if (errorMessage != null) {
                Log.e("TAG", "Error: $errorMessage")
                Text(text = "Error: $errorMessage", color = white)
            } else {
                currentWeather?.let { weather ->
                    WeatherScreen(weather)
                } ?: Text(text = "Loading...", color = white)
            }
        }
    }
}






//@Preview(showBackground = true)
@Composable
fun WeatherScreen(weather:WeatherResponse) {
    val city = weather.city.name.toString()
    val weather = weather.list.firstOrNull() ?: return
    val mainWeather = weather.main
    val date =
        SimpleDateFormat("EEE, MMM d HH:mm", Locale.getDefault()).format(Date(weather.dt * 1000))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.nn), // Add a space background
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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
            WeatherMainInfo(temperature = mainWeather.temp.toInt(), weatherIcon = R.drawable.sun_rain,desc = weather.weather.firstOrNull()?.description ?: "N/A")

            Spacer(modifier = Modifier.height(22.dp))

            // Additional Weather Details
            WeatherDetails(
                realFeel = mainWeather.feels_like.toInt(),
                humidity = mainWeather.humidity,
                windSpeed = weather.wind.speed.toString(),
                pressure = mainWeather.pressure.toString(),
                date = date
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
//        Icon(
//            imageVector = Icons.Default.Menu,
//            contentDescription = "Menu",
//            tint = Color.White,
//            modifier = Modifier.size(28.dp)
//        )
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
//        Icon(
//            imageVector = Icons.Default.Add,
//            contentDescription = "Add",
//            tint = Color.White,
//            modifier = Modifier.size(28.dp)
//        )
    }
}

@Composable
fun WeatherMainInfo(temperature: Int, weatherIcon: Int,desc:String) {
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
    }
}

@Composable
fun WeatherDetails(realFeel: Int, humidity: Int, windSpeed: String, pressure: String, date: String) {
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
                WeatherDetailItem(icon = R.drawable.temperature, label = "RealFeel", value = "$realFeel°")
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.width(180.dp))
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetailItem(icon = R.drawable.hum, label = "Humidity", value = "$humidity%")
            }
            // Vertical Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(130.dp) // Adjust height as needed
                    .background(Color.White.copy(alpha = 0.5f))
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WeatherDetailItem(icon = R.drawable.wind_small, label = "Wind", value = windSpeed)
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.width(180.dp))
                Spacer(modifier = Modifier.height(16.dp))
                WeatherDetailItem(icon = R.drawable.pressuer, label = "Pressure", value = pressure)
            }
        }
    }
}


@Composable
fun WeatherDetailItem(icon: Int, label: String, value: String) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
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







/*
@Composable
fun WeatherCard(weather:WeatherResponse) {
    val city = weather.city.name.toString()
    val weather = weather.list.firstOrNull() ?: return
    val mainWeather = weather.main
    val date =
        SimpleDateFormat("EEE, MMM d HH:mm", Locale.getDefault()).format(Date(weather.dt * 1000))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Fix here
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (cityText, locationIcon, tempText, weatherText, feelsLikeText, weatherIcon, dateText) = createRefs()

            Icon(
                painter = painterResource(id = R.drawable.snow),
                contentDescription = "Location Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .constrainAs(locationIcon) {
                        top.linkTo(parent.top, margin = 2.dp)
                        start.linkTo(parent.start, margin = 100.dp)
                    }
            )

            Text(
                text = city,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.constrainAs(cityText) {
                    top.linkTo(parent.top, margin = 2.dp)
                    start.linkTo(locationIcon.end, margin = 20.dp)
                }
            )

            Image(
                painter = painterResource(id = R.drawable.sun_rain),
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(120.dp)
                    .constrainAs(weatherIcon) {
                        top.linkTo(cityText.bottom, margin = 36.dp)
                        start.linkTo(parent.start, margin = 100.dp)
                    }
            )

            Text(
                text = mainWeather.temp.toString(),
                fontSize = 36.sp,
                color = Color.White,
                modifier = Modifier.constrainAs(tempText) {
                    top.linkTo(weatherIcon.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 120.dp)
                }
            )

            Text(
                text = weather.weather.firstOrNull()?.description ?: "N/A",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.constrainAs(weatherText) {
                    top.linkTo(tempText.bottom, margin = 11.dp)
                    start.linkTo(tempText.start)
                }
            )

            Text(
                text = mainWeather.feels_like.toString(),
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.constrainAs(feelsLikeText) {
                    top.linkTo(weatherText.bottom, margin = 8.dp)
                    start.linkTo(weatherText.start)
                }
            )

            Text(
                text = date,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.constrainAs(dateText) {
                    top.linkTo(feelsLikeText.bottom, margin = 16.dp)
                    start.linkTo(feelsLikeText.start)
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                }
            )
        }
    }
}
*/





/*
@Composable
fun DailyWeatherCard(weather: WeatherResponse, modifier: Modifier = Modifier) {
    val weather = weather.list.firstOrNull() ?: return
    val mainWeather = weather.main
    val date =
        SimpleDateFormat("EEE, MMM d HH:mm", Locale.getDefault()).format(Date(weather.dt * 1000))

    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (forcastImage, forcastValue, windImage, title, desc, background) = createRefs()
        CardBackground(
            modifier = Modifier.constrainAs(background) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top, topMargin = 24.dp,
                    bottom = desc.bottom
                )
                height = Dimension.fillToConstraints
            }
        )
//        Image(
//            painter = painterResource(id = R.drawable.sun_rain),
//            contentDescription = null,
//            contentScale = ContentScale.FillHeight,
//            modifier = Modifier
//                .height(175.dp)
//                .constrainAs(forcastImage) {
//                    top.linkTo(anchor = parent.top)
//                    start.linkTo(anchor = parent.start, margin = 4.dp)
//                }
//        )
        Text(
            text = weather.weather.firstOrNull()?.description ?: "N/A",
            style = MaterialTheme.typography.titleLarge,
            color = backgroundColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.constrainAs(title) {
                start.linkTo(anchor = parent.start, margin = 16.dp)
                top.linkTo(anchor = parent.top, margin = 128.dp)
            }
        )

        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            color = backgroundColor,
            modifier = Modifier
                .constrainAs(desc) {
                    start.linkTo(anchor = title.start)
                    top.linkTo(anchor = title.bottom)
                }
                .padding(bottom = 16.dp)
        )

        ForcastValue(
            modifier = Modifier.constrainAs(forcastValue) {
                end.linkTo(anchor = parent.end, margin = 24.dp)
                top.linkTo(anchor = parent.top)
                bottom.linkTo(anchor = title.bottom)
            },
            degree = mainWeather.temp.toString(),
            decs = mainWeather.feels_like.toString()
        )

        WindForcastImage(
            modifier= Modifier.constrainAs(windImage){
                linkTo(
                    top = title.top,
                    bottom = title.bottom
                )
                end.linkTo(anchor = parent.end, margin = 24.dp)
            }
        )
    }

   /* Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Date: $date")
            Text(text = "Temperature: ${mainWeather.temp}°C")
            Text(text = "Feels like: ${mainWeather.feels_like}°C")
            Text(text = "Pressure: ${mainWeather.pressure} hPa")
            Text(text = "Humidity: ${mainWeather.humidity}%")
            Text(text = "Clouds: ${weather.clouds}%")
            Text(text = "Wind Speed: ${weather.wind.speed} m/s")
            Text(text = "Condition: ${weather.weather.firstOrNull()?.description ?: "N/A"}")
            Text(text = "City : ${city} ")
        }
    }*/
}


@Composable
private fun CardBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.Transparent, // Fully transparent background
                shape = RoundedCornerShape(32.dp)
            )
    )
}



@Composable
private fun ForcastValue(
    modifier: Modifier = Modifier,
    degree: String,
    decs: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = degree,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        0f to white,
                        1f to white.copy(alpha = 0.3f)
                    ),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black
                ),
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = "°",
                style = TextStyle(
                    brush = Brush.linearGradient(
                        0f to white,
                        1f to white.copy(alpha = 0.3f)
                    ),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light
                )
            )

        }

        Text(
            text = "Feels like $decs",
            color = white.copy(alpha = 0.3f),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )

    }
}


@Composable
private  fun WindForcastImage(
    modifier: Modifier = Modifier
){
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(R.drawable.snow),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
                .padding(end = 20.dp),
            tint = white
        )
        Icon(
            painter = painterResource(R.drawable.wind),
            contentDescription = null,
            modifier = Modifier.size(55.dp),
            tint = white
        )
    }
}


@Composable
private fun LocationInfo(
    modifier: Modifier = Modifier,
    location : String
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(R.drawable.snow),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(18.dp)
            )
            Text(
                text = location,
                style = MaterialTheme.typography.titleLarge,
                color = loyalBlue,
                fontWeight = FontWeight.Bold
            )
        }
        ProgressBar()
    }
}

@Composable
private fun ProgressBar(
    modifier: Modifier =Modifier
){
    Box(
        modifier = modifier.
        background(
            brush = Brush.linearGradient(
                0f to lightBlue,
                0.5f to secBlue,
                1f to loyalBlue
            ),
            shape = RoundedCornerShape(8.dp)
        )
            .padding(
                vertical = 2.dp,
                horizontal = 10.dp
            )
    ){
        Text(
            text = "Updating",
            style = MaterialTheme.typography.labelSmall,
            color = white
        )
    }
}


*/





