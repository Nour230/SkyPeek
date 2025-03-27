package com.example.skypeek.composablescreens
import com.example.skypeek.data.models.LocationPOJO
import kotlinx.serialization.Serializable
@Serializable
sealed class ScreensRoute(val route: String) {
    @Serializable
    data object SplashScreen : ScreensRoute("splash")  // Ensure it's a string
    @Serializable
    data object HomeScreen : ScreensRoute("home")
    @Serializable
    data object SettingScreen : ScreensRoute("setting")
    @Serializable
    data object FavScreen : ScreensRoute("fav")
    @Serializable
    data object MapScreen : ScreensRoute("map")
    @Serializable
    data class FavDetailsScreen(val loc:LocationPOJO) : ScreensRoute("favDetails")
    @Serializable
    data object AlertScreen : ScreensRoute("alert")
    @Serializable
    data object AlertDetailsScreen : ScreensRoute("search")
}

