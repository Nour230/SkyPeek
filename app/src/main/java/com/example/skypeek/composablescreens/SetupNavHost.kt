package com.example.skypeek.composablescreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skypeek.composablescreens.home.HomeScreen
import com.example.skypeek.composablescreens.splash.SplashScreen
@Composable
fun SetupNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.SplashScreen.route // Ensure this is a String
    ) {
        composable(ScreensRoute.SplashScreen.route) {
            SplashScreen {
                navController.navigate(ScreensRoute.HomeScreen.route) {
                    popUpTo(ScreensRoute.SplashScreen.route) { inclusive = true }
                }
            }
        }
        composable(ScreensRoute.HomeScreen.route) {
            HomeScreen()
        }
    }
}

