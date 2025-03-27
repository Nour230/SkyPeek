package com.example.skypeek.composablescreens.alert

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun AlertDetailsScreen(isNAV: MutableState<Boolean>, isFAB: MutableState<Boolean>){
    isNAV.value = false
    isFAB.value = false
    Text(text = "Alert Details Screen")
}