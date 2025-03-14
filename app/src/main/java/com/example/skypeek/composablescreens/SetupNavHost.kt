package com.example.skypeek.composablescreens

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skypeek.composablescreens.home.HomeViewModel
import com.example.skypeek.composablescreens.splash.SplashScreen
import com.example.skypeek.data.remote.RemoteDataSource
import com.example.skypeek.data.remote.WeatherApiService
import com.example.skypeek.data.remote.WeatherRemoteDataSource
import com.example.skypeek.data.repository.WeatherRepositoryImpl

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skypeek.composablescreens.home.WeatherFactory

@Composable
fun SetupNavHost(navController: NavHostController, apiService: WeatherApiService) {
    val remoteDataSource = WeatherRemoteDataSource(apiService)
    val weatherRepository = WeatherRepositoryImpl(remoteDataSource)

    val homeViewModel: HomeViewModel = viewModel(
        factory = WeatherFactory(weatherRepository)
    )

    NavHost(
        navController = navController,
        startDestination = ScreensRoute.SplashScreen.route
    ) {
        composable(ScreensRoute.SplashScreen.route) {
            SplashScreen {
                navController.navigate(ScreensRoute.HomeScreen.route) {
                    popUpTo(ScreensRoute.SplashScreen.route) { inclusive = true }
                }
            }
        }
        composable(ScreensRoute.HomeScreen.route) {
            HomeScreen(homeViewModel)
        }
    }
}
