package com.example.skypeek.composablescreens

import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.skypeek.composablescreens.alert.AlertDetailsScreen
import com.example.skypeek.composablescreens.alert.AlertScreen
import com.example.skypeek.composablescreens.alert.NotificationScreen
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
import com.example.skypeek.utiles.LocalNavController
import com.example.skypeek.utiles.helpers.LocationHelper
import com.example.skypeek.data.local.WeatherDataBase
import com.example.skypeek.data.local.WeatherLocalDataSourceImpl
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.remote.WeatherApiService
import com.example.skypeek.data.remote.WeatherRemoteDataSource
import com.example.skypeek.data.repository.WeatherRepositoryImpl
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(
    apiService: WeatherApiService,
    locationState: MutableState<Location?>,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>,
    showDetails: MutableState<Boolean>
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    val skipSplash = sharedPref.getBoolean("SkipSplash", false)
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
        startDestination = if (skipSplash) ScreensRoute.HomeScreen.route else ScreensRoute.SplashScreen.route
    ) {
        composable(ScreensRoute.SplashScreen.route) {
            sharedPref.edit().putBoolean("SkipSplash", false).apply() // Reset after first launch
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
                val gson = Gson()
                val jsonString = gson.toJson(locationPOJO)
                navController.navigate("favDetails/$jsonString")
            })
        }

        composable("favDetails/{data}") { backStackEntry ->
            val jsonString = backStackEntry.arguments?.getString("data")
            val location = try {
                Gson().fromJson(jsonString, LocationPOJO::class.java)
            } catch (e: Exception) {
                Log.e("TAG", "Error parsing JSON: $e")
                null
            }
            if (location != null) {
                NotificationScreen(location)
               //FavDetailsScreen(location, homeViewModel, isFAB, isNAV)
            } else {
                Log.e("TAG", "Location data is null")
            }
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
                }
            })
        }
        composable(ScreensRoute.AlertScreen.route) {
            AlertScreen(isNAV, isFAB,showDetails)
        }

        composable(ScreensRoute.AlertDetailsScreen.route) {
            AlertDetailsScreen(
                isNAV = isNAV,
                isFAB = isFAB,
                onDismiss = { navController.navigate(ScreensRoute.AlertScreen.route) }
            )
        }
    }
}
