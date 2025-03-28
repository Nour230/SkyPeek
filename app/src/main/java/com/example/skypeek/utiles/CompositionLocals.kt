package com.example.skypeek.utiles

import android.util.Log
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    Log.d("TAG", "No NavController provided!: ")
    error("No NavController provided!") // Throw an error if no NavController is provided
}