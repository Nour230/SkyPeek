package com.example.skypeek.composablescreens.settings

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.skypeek.R
import com.example.skypeek.utiles.deleteSharedPrefrence
import com.example.skypeek.utiles.enums.Temperature
import com.example.skypeek.utiles.enums.WindSpeed
import com.example.skypeek.utiles.helpers.LocationHelper
import com.example.skypeek.utiles.saveToSharedPrefrence
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.gray
import com.example.skypeek.ui.theme.secbackgroundColor
import com.example.skypeek.utiles.helpers.changeLanguage
import com.example.skypeek.utiles.helpers.mapLanguageCodeToName

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp

@Composable
fun SettingScreen(
    viewModel: SettingsViewModel,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>,
    navigateToMAP: () -> Unit,
    locationHelper: LocationHelper,
    locationState: MutableState<Location?>
) {
    isFAB.value = false
    isNAV.value = true
    val selectedLanguage by viewModel.selectedLanguage
    val context = LocalContext.current
    val selectedLocation by viewModel.selectedLocation
    val selectedTemperature by viewModel.selectedTemperature
    val selectedWindSpeed by viewModel.selectedWindSpeed

    Log.i("TAG", "SettingScreen: $selectedLanguage")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(cardBackGround, gray, cardBackGround)
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()), // ✅ Makes the screen scrollable
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingsCard(title = stringResource(R.string.select_location)) {
            RadioOption(
                text = stringResource(R.string.gps),
                isSelected = selectedLocation.equals("gps", ignoreCase = true),
                onSelected = {
                    viewModel.setLocation("gps")
                    deleteSharedPrefrence(context, "lat")
                    deleteSharedPrefrence(context, "long")
                    if (locationHelper.hasLocationPermissions()) {
                        if (locationHelper.isLocationEnabled()) {
                            locationHelper.getFreshLocation { location ->
                                locationState.value = location
                            }
                        } else {
                            locationHelper.enableLocation()
                        }
                    }
                }
            )
            RadioOption(
                text = stringResource(R.string.map),
                isSelected = selectedLocation.equals("map", ignoreCase = true),
                onSelected = {
                    viewModel.setLocation("map")
                    navigateToMAP()
                }
            )
        }

        SettingsCard(title = stringResource(R.string.units_of_temperature)) {
            RadioOption(
                text = stringResource(R.string.celsius),
                isSelected = selectedTemperature.equals("celsius", ignoreCase = true),
                onSelected = {
                    viewModel.setTemperature("celsius")
                    updateTemperaturePref(context, Temperature.CELSIUS)
                    viewModel.setWindSpeed("METER_SEC")
                    updateWindSpeedPref(context, WindSpeed.METER_SEC)
                }
            )
            RadioOption(
                text = stringResource(R.string.fahrenheit),
                isSelected = selectedTemperature.equals("fahrenheit", ignoreCase = true),
                onSelected = {
                    viewModel.setTemperature("fahrenheit")
                    updateTemperaturePref(context, Temperature.FAHRENHEIT)
                    viewModel.setWindSpeed("MILES_HOUR")
                    updateWindSpeedPref(context, WindSpeed.MILES_HOUR)
                }
            )
            RadioOption(
                text = stringResource(R.string.kelvin),
                isSelected = selectedTemperature.equals("kelvin", ignoreCase = true),
                onSelected = {
                    viewModel.setTemperature("kelvin")
                    updateTemperaturePref(context, Temperature.KELVIN)
                    viewModel.setWindSpeed("METER_SEC")
                    updateWindSpeedPref(context, WindSpeed.METER_SEC)
                }
            )
        }

        SettingsCard(title = stringResource(R.string.language)) {
            RadioOption(
                text = stringResource(R.string.english),
                isSelected = mapLanguageCodeToName(selectedLanguage).equals("english", ignoreCase = true),
                onSelected = {
                    viewModel.setLanguage("english")
                    changeLanguage(context, "en", "language")
                }
            )
            RadioOption(
                text = stringResource(R.string.arabic),
                isSelected = mapLanguageCodeToName(selectedLanguage).equals("arabic", ignoreCase = true),
                onSelected = {
                    viewModel.setLanguage("arabic")
                    changeLanguage(context, "ar", "language")
                }
            )
            RadioOption(
                text = stringResource(R.string.korean),
                isSelected = mapLanguageCodeToName(selectedLanguage).equals("korean", ignoreCase = true),
                onSelected = {
                    viewModel.setLanguage("korean")
                    changeLanguage(context, "kr", "language")
                }
            )
        }

        SettingsCard(title = stringResource(R.string.units_of_wind_speed)) {
            RadioOption(
                text = stringResource(R.string.miles_hour),
                isSelected = selectedWindSpeed.equals("MILES_HOUR", ignoreCase = true),
                onSelected = {
                    viewModel.setWindSpeed("MILES_HOUR")
                    updateWindSpeedPref(context, WindSpeed.MILES_HOUR)

                    // Set temperature to Fahrenheit
                    viewModel.setTemperature("fahrenheit")
                    updateTemperaturePref(context, Temperature.FAHRENHEIT)
                }
            )
            RadioOption(
                text = stringResource(R.string.meter_sec),
                isSelected = selectedWindSpeed.equals("METER_SEC", ignoreCase = true),
                onSelected = {
                    viewModel.setWindSpeed("METER_SEC")
                    updateWindSpeedPref(context, WindSpeed.METER_SEC)

                    // Set temperature to Celsius
                    viewModel.setTemperature("celsius")
                    updateTemperaturePref(context, Temperature.CELSIUS)
                }
            )
        }

    }
}


@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = secbackgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun RadioOption(
    text: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected,
            colors = RadioButtonDefaults.colors(selectedColor = cardBackGround)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun updateTemperaturePref(context: Context, temperature: Temperature) {
    deleteSharedPrefrence(context, "temperature")
    saveToSharedPrefrence(
        context = context,
        temperature.toString(),
        "temperature"
    )
}

private fun updateWindSpeedPref(context: Context, windSpeed: WindSpeed) {
    deleteSharedPrefrence(context, "windspeed")
    saveToSharedPrefrence(
        context = context,
        windSpeed.toString(),
        "windspeed"
    )
}