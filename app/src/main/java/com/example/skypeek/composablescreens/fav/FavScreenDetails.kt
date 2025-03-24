package com.example.skypeek.composablescreens.fav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.example.skypeek.BuildConfig
import com.example.skypeek.composablescreens.home.HomeViewModel

@Composable
fun FavDetailsScreen(lat: Double, long: Double,viewModel: HomeViewModel,isFAB: MutableState<Boolean>,isNAV: MutableState<Boolean>){
    isFAB.value = false
    isNAV.value = false
    viewModel.getWeather(lat,long, BuildConfig.apiKeySafe,"Celsius")

    Text(text = "Lat: $lat, Long: $long")
}