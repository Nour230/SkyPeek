package com.example.skypeek.composablescreens
import kotlinx.serialization.Serializable
sealed class ScreensRoute(val route: String) {
    object SplashScreen : ScreensRoute("splash")  // Ensure it's a string
    object HomeScreen : ScreensRoute("home")
    object SettingScreen : ScreensRoute("setting")
    object FavScreen : ScreensRoute("fav")
    object MapScreen : ScreensRoute("map")
}

