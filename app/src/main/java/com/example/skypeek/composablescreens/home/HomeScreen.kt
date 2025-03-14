import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skypeek.composablescreens.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val currentWeather by homeViewModel.weather.observeAsState()
    val errorMessage by homeViewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        homeViewModel.getWeather(40.7128, -74.0060, "504e873b86ab70eb44cd86f97a12b723")  // Replace with actual values
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Current Weather")

        if (errorMessage != null) {
            Log.i("TAG", "HomeScreen: "+errorMessage)
            Text(text = "Error: $errorMessage")
        } else {
            currentWeather?.let { weather ->
                WeatherCard(weather)  // ✅ Displays current weather
            } ?: Text(text = "Loading...")
        }
    }
}

@Composable
fun WeatherCard(weather: WeatherResponse) {
    val weather = weather.list.firstOrNull() ?: return
    val mainWeather = weather.main
    val date = SimpleDateFormat("EEE, MMM d HH:mm", Locale.getDefault()).format(Date(weather.dt * 1000))

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Date: $date")
            Text(text = "Temperature: ${mainWeather.temp}°C")
            Text(text = "Feels like: ${mainWeather.feels_like}°C")
            Text(text = "Pressure: ${mainWeather.pressure} hPa")
            Text(text = "Humidity: ${mainWeather.humidity}%")
            Text(text = "Clouds: ${weather.clouds}%")
            Text(text = "Wind Speed: ${weather.wind.speed} m/s")
            Text(text = "Condition: ${weather.weather.firstOrNull()?.description ?: "N/A"}")
        }
    }
}
