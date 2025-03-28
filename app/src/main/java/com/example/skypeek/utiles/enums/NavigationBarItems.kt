package com.example.skypeek.utiles.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.skypeek.composablescreens.ScreensRoute

enum class NavigationBarItems(val icon: ImageVector, val route: String) {
    Home(Icons.Filled.Home, ScreensRoute.HomeScreen.route),
    Search(Icons.Filled.Favorite, "fav"),
    Alarm(Icons.Filled.Notifications, "alert"),
    Settings(Icons.Filled.Settings, "setting")
}