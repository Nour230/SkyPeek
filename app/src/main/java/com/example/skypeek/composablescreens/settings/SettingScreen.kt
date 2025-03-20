package com.example.skypeek.composablescreens.settings

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skypeek.ui.theme.backgroundColor
import com.example.skypeek.ui.theme.gray
import com.example.skypeek.ui.theme.loyalBlue
import com.example.skypeek.ui.theme.secbackgroundColor
import com.example.skypeek.ui.theme.semonelight
import com.example.skypeek.ui.theme.white

@Composable
fun SettingScreen(viewModel: SettingsViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(loyalBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Select Location
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp), // Add corner radius
            colors = CardDefaults.cardColors(containerColor = secbackgroundColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Select Location", style = MaterialTheme.typography.titleLarge)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.selectedLocation.value == "GPS",
                            onClick = { viewModel.setLocation("GPS") }
                        )
                        Text(text = "GPS", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.selectedLocation.value == "MAP",
                            onClick = { viewModel.setLocation("MAP") }
                        )
                        Text(text = "MAP", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Units of Temperature
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp), // Add corner radius
            colors = CardDefaults.cardColors(containerColor = secbackgroundColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Units of Temperature", style = MaterialTheme.typography.titleLarge)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.selectedTemperature.value == "Celsius",
                            onClick = { viewModel.setTemperature("Celsius") }
                        )
                        Text(text = "Celsius", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.selectedTemperature.value == "Fahrenheit",
                            onClick = { viewModel.setTemperature("Fahrenheit") },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(text = "Fahrenheit", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.selectedTemperature.value == "Kelvin",
                            onClick = { viewModel.setTemperature("Kelvin") }
                        )
                        Text(text = "Kelvin", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Language Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp), // Add corner radius
            colors = CardDefaults.cardColors(containerColor = secbackgroundColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Language", style = MaterialTheme.typography.titleLarge)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.selectedLanguage.value == "English",
                            onClick = { viewModel.setLanguage("English") }
                        )
                        Text(text = "English", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = viewModel.selectedLanguage.value == "Arabic",
                            onClick = { viewModel.setLanguage("Arabic") }
                        )
                        Text(text = "Arabic", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Units of Wind Speed
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp), // Add corner radius
            colors = CardDefaults.cardColors(containerColor = secbackgroundColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Units of Wind Speed", style = MaterialTheme.typography.titleLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = viewModel.selectedWindSpeed.value == "miles/hour",
                        onClick = { viewModel.setWindSpeed("miles/hour") }
                    )
                    Text(text = "Miles/Hour", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = viewModel.selectedWindSpeed.value == "kilometers/hour",
                        onClick = { viewModel.setWindSpeed("kilometers/hour") }
                    )
                    Text(text = "Meter/Sec", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
