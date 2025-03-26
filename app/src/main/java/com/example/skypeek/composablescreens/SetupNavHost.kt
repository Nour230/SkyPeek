package com.example.skypeek.composablescreens

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skypeek.composablescreens.fav.FavDetailsScreen
import com.example.skypeek.composablescreens.fav.FavFactory
import com.example.skypeek.composablescreens.fav.FavScreen
import com.example.skypeek.composablescreens.fav.FavViewModel
import com.example.skypeek.composablescreens.fav.map.MapFactory
import com.example.skypeek.composablescreens.fav.map.MapScreen
import com.example.skypeek.composablescreens.fav.map.MapViewModel
import com.example.skypeek.composablescreens.home.HomeScreen
import com.example.skypeek.composablescreens.home.HomeViewModel
import com.example.skypeek.composablescreens.home.WeatherFactory
import com.example.skypeek.composablescreens.settings.SettingFactory
import com.example.skypeek.composablescreens.settings.SettingScreen
import com.example.skypeek.composablescreens.settings.SettingsViewModel
import com.example.skypeek.composablescreens.splash.SplashScreen
import com.example.skypeek.composablescreens.utiles.LocalNavController
import com.example.skypeek.composablescreens.utiles.helpers.LocationHelper
import com.example.skypeek.data.local.WeatherDataBase
import com.example.skypeek.data.local.WeatherLocalDataSourceImpl
import com.example.skypeek.data.remote.WeatherApiService
import com.example.skypeek.data.remote.WeatherRemoteDataSource
import com.example.skypeek.data.repository.WeatherRepositoryImpl
import com.google.android.libraries.places.api.Places

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(
    apiService: WeatherApiService,
    locationState: MutableState<Location?>,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val remoteDataSource = WeatherRemoteDataSource(apiService)
    val localDataSource =
        WeatherLocalDataSourceImpl(WeatherDataBase.getInstance(LocalContext.current).dao())
    val weatherRepository = WeatherRepositoryImpl(remoteDataSource, localDataSource)

    val homeViewModel: HomeViewModel = viewModel(
        factory = WeatherFactory(weatherRepository)
    )

    val mapViewModel: MapViewModel = viewModel(
        factory = MapFactory(
            placesClient = Places.createClient(LocalContext.current),
            weatherRepository
        )
    )
    val favViewModel: FavViewModel = viewModel(
        factory = FavFactory(weatherRepository)
    )
    val settingViewModel: SettingsViewModel = viewModel(
        factory = SettingFactory(context)
    )
    NavHost(
        navController = navController,
        startDestination = ScreensRoute.SplashScreen.route
    ) {
        composable(ScreensRoute.SplashScreen.route) {
            SplashScreen(isNAV) {
                navController.navigate(ScreensRoute.HomeScreen.route) {
                    popUpTo(ScreensRoute.SplashScreen.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
        composable(ScreensRoute.HomeScreen.route) {
            HomeScreen(homeViewModel, locationState, isFAB,isNAV)
        }
        composable(ScreensRoute.SettingScreen.route) {
            SettingScreen(settingViewModel, isFAB,isNAV,navigateToMAP = {
                navController.navigate("${ScreensRoute.MapScreen.route}/false")}, LocationHelper(context),locationState)
        }
        composable(ScreensRoute.FavScreen.route) {
            FavScreen(favViewModel, isFAB,isNAV, goToFavDetailsScreen = { locationPOJO ->
                navController.navigate("favDetails/${locationPOJO.lat}/${locationPOJO.long}")
            })
        }

        composable("favDetails/{lat}/{long}") { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val long = backStackEntry.arguments?.getString("long")?.toDoubleOrNull() ?: 0.0
            FavDetailsScreen(lat, long, homeViewModel, isFAB,isNAV)
        }

        composable(
            route = "${ScreensRoute.MapScreen.route}/{isFavorite}",
            arguments = listOf(navArgument("isFavorite") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isFavorite = backStackEntry.arguments?.getBoolean("isFavorite") ?: false
            MapScreen(mapViewModel, locationState, isFAB, isNAV, isFavorite, onLocationSelected = { latLng ->
                if (!isFavorite) {
                   locationState.value = Location("").apply {
                       latitude = latLng.latitude
                       longitude = latLng.longitude
                   }
                   // navController.navigate(ScreensRoute.HomeScreen.route)
                }
            })
        }
    }
}
