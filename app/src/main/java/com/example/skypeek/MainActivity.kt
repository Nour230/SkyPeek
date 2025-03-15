package com.example.skypeek

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skypeek.composablescreens.ScreensRoute
import com.example.skypeek.composablescreens.SetupNavHost
import com.example.skypeek.composablescreens.utiles.LocalNavController
import com.example.skypeek.composablescreens.utiles.LocationHelper
import com.example.skypeek.composablescreens.utiles.REQUEST_LOCATION_PERMISSION
import com.example.skypeek.data.remote.RetrofitHelper.weatherApiService
import com.example.skypeek.data.remote.WeatherApiService
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
    lateinit var locationState: MutableState<Location?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemUI()

        locationHelper = LocationHelper(this)
        locationState = mutableStateOf(null)
        setContent {
            WeatherApp(weatherApiService, locationState)
        }

        if (!locationHelper.hasLocationPermissions()) {
            locationHelper.requestLocationPermissions(this)
        } else {
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        locationHelper.getFreshLocation { location ->
            locationState.value = location
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Log.e("TAG", "MainActivity Permission denied by user.")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (locationHelper.hasLocationPermissions()) {
            if (locationHelper.isLocationEnabled()) {
                fetchLocation()
            } else {
                locationHelper.enableLocation()
            }
        } else {
            locationHelper.requestLocationPermissions(this)
        }
    }


    @Composable
    fun WeatherApp(apiService: WeatherApiService, locationState: MutableState<Location?>) {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavController provides navController) {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route ?: ScreensRoute.SplashScreen.route
            Log.i("TAG", "WeatherApp: $currentRoute")
            var selectedIndex by remember { mutableStateOf(0) }
            val navigationBarItems = NavigationBarItems.entries.toTypedArray()

            val hapticFeedback = LocalHapticFeedback.current
            var showBottomBar by remember { mutableStateOf(false) }

            // Update showBottomBar based on the current route
            LaunchedEffect(currentRoute) {
                showBottomBar = currentRoute != ScreensRoute.SplashScreen.route
                Log.i("TAG", "WeatherApp: $showBottomBar")
            }

            Scaffold(
                bottomBar = {
                    // Only show bottom bar if NOT on SplashScreen
                    if (showBottomBar) {
                        AnimatedNavigationBar(
                            modifier = Modifier.height(64.dp),
                            selectedIndex = selectedIndex,
                            cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
                            ballAnimation = Parabolic(tween(300)),
                            indentAnimation = Height(tween(600)),
                            barColor = com.example.skypeek.ui.theme.loyalBlue,
                            ballColor = com.example.skypeek.ui.theme.semone
                        ) {
                            navigationBarItems.forEachIndexed { index, item ->
                                var isShaking by remember { mutableStateOf(false) }

                                val shakeOffset by animateFloatAsState(
                                    targetValue = if (isShaking) 8f else 0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(50, easing = FastOutLinearInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "Shake Animation"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset(x = shakeOffset.dp)
                                        .noRippleClickableWithVibration(hapticFeedback) {
                                            if (selectedIndex != index) {
                                                selectedIndex = index
                                                navController.navigate(item.route) {
                                                    popUpTo(ScreensRoute.HomeScreen.route) {
                                                        inclusive = false
                                                    }
                                                }
                                                isShaking = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(26.dp),
                                        imageVector = item.icon,
                                        contentDescription = "Bottom Nav Icon",
                                        tint = if (selectedIndex == index)
                                            com.example.skypeek.ui.theme.semone
                                        else com.example.skypeek.ui.theme.white
                                    )

                                    LaunchedEffect(isShaking) {
                                        if (isShaking) {
                                            delay(300)
                                            isShaking = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    SetupNavHost(apiService, locationState)
                }
            }
        }
    }

    enum class NavigationBarItems(val icon: ImageVector, val route: String) {
        Home(Icons.Filled.Home, ScreensRoute.HomeScreen.route),
        Search(Icons.Filled.Search, "search"),   // 🚨 Ensure this exists in SetupNavHost()
        Settings(Icons.Filled.Settings, "settings") // 🚨 Ensure this exists in SetupNavHost()
    }

    fun Modifier.noRippleClickableWithVibration(
        hapticFeedback: HapticFeedback,
        onClick: () -> Unit
    ): Modifier = composed {
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For older versions
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

}
