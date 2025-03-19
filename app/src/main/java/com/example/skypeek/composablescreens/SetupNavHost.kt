package com.example.skypeek.composablescreens

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skypeek.composablescreens.home.HomeViewModel
import com.example.skypeek.composablescreens.splash.SplashScreen
import com.example.skypeek.data.remote.WeatherApiService
import com.example.skypeek.data.remote.WeatherRemoteDataSource
import com.example.skypeek.data.repository.WeatherRepositoryImpl
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.skypeek.composablescreens.home.HomeScreen
import com.example.skypeek.composablescreens.home.WeatherFactory
import com.example.skypeek.composablescreens.utiles.LocalNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(apiService: WeatherApiService,
                 locationState: MutableState<Location?>
                 ) {
    val navController = LocalNavController.current
    val remoteDataSource = WeatherRemoteDataSource(apiService)
    val weatherRepository = WeatherRepositoryImpl(remoteDataSource)

    val homeViewModel: HomeViewModel = viewModel(
        factory = WeatherFactory(weatherRepository)
    )

    NavHost(
        navController = navController,
        startDestination = ScreensRoute.HomeScreen.route
    ) {
        composable(ScreensRoute.SplashScreen.route) {
            SplashScreen {
                navController.navigate(ScreensRoute.HomeScreen.route) {
                    popUpTo(ScreensRoute.SplashScreen.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable(ScreensRoute.HomeScreen.route) {
            HomeScreen(homeViewModel, locationState)
        }
    }
}
