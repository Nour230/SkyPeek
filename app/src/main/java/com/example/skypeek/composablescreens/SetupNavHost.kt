package com.example.skypeek.composablescreens

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skypeek.composablescreens.home.HomeViewModel
import com.example.skypeek.composablescreens.splash.SplashScreen
import com.example.skypeek.data.remote.WeatherApiService
import com.example.skypeek.data.remote.WeatherRemoteDataSource
import com.example.skypeek.data.repository.WeatherRepositoryImpl
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skypeek.composablescreens.fav.FavFactory
import com.example.skypeek.composablescreens.home.HomeScreen
import com.example.skypeek.composablescreens.home.WeatherFactory
import com.example.skypeek.composablescreens.utiles.LocalNavController
import com.example.skypeek.composablescreens.fav.FavScreen
import com.example.skypeek.composablescreens.fav.FavViewModel
import com.example.skypeek.composablescreens.fav.map.MapFactory
import com.example.skypeek.composablescreens.fav.map.MapScreen
import com.example.skypeek.composablescreens.fav.map.MapViewModel
import com.example.skypeek.composablescreens.settings.SettingScreen
import com.example.skypeek.composablescreens.settings.SettingsViewModel
import com.example.skypeek.data.local.WeatherDataBase
import com.example.skypeek.data.local.WeatherLocalDataSourceImpl
import com.google.android.libraries.places.api.Places

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(apiService: WeatherApiService,
                 locationState: MutableState<Location?>,
                 isFAB: MutableState<Boolean>
) {
    val navController = LocalNavController.current
    val remoteDataSource = WeatherRemoteDataSource(apiService)
    val localDataSource = WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(LocalContext.current).dao())
    val weatherRepository = WeatherRepositoryImpl(remoteDataSource,localDataSource)

    val homeViewModel: HomeViewModel = viewModel(
        factory = WeatherFactory(weatherRepository)
    )
    
    val mapViewModel: MapViewModel = viewModel(
        factory = MapFactory(
            placesClient = Places.createClient(LocalContext.current),
            weatherRepository
        )
    )
    val favViewModel : FavViewModel = viewModel(
        factory = FavFactory(weatherRepository)
    )
    val settingViewModel: SettingsViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.SplashScreen.route
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
            HomeScreen(homeViewModel, locationState,isFAB)
        }
        composable(ScreensRoute.SettingScreen.route) {
            SettingScreen(settingViewModel,isFAB)
        }
        composable(ScreensRoute.FavScreen.route) {
            FavScreen(favViewModel,isFAB)
        }
        composable(ScreensRoute.MapScreen.route) {
            MapScreen(mapViewModel,locationState,isFAB)
        }
    }
}
