package com.example.skypeek

import android.content.Context
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
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.skypeek.composablescreens.ScreensRoute
import com.example.skypeek.composablescreens.SetupNavHost
import com.example.skypeek.data.remote.RetrofitHelper.weatherApiService
import com.example.skypeek.data.remote.WeatherApiService
import com.example.skypeek.ui.screenshelper.customShadow
import com.example.skypeek.ui.theme.Purple40
import com.example.skypeek.ui.theme.black
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.loyalBlue
import com.example.skypeek.ui.theme.secbackgroundColor
import com.example.skypeek.utiles.LocalNavController
import com.example.skypeek.utiles.SharedPreference
import com.example.skypeek.utiles.enums.NavigationBarItems
import com.example.skypeek.utiles.helpers.LocaleHelper
import com.example.skypeek.utiles.helpers.LocationHelper
import com.example.skypeek.utiles.helpers.REQUEST_LOCATION_PERMISSION
import com.example.skypeek.utiles.helpers.internet.ConnectivityObserver
import com.example.skypeek.utiles.helpers.internet.checkForInternet
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.libraries.places.api.Places
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
    lateinit var locationState: MutableState<Location?>
    private lateinit var isFAB: MutableState<Boolean>
    private lateinit var isNAV: MutableState<Boolean>
    private lateinit var showDetails: MutableState<Boolean>

    override fun attachBaseContext(newBase: Context?) {
        val language = SharedPreference.getLanguage(newBase!!, "language")
        val langToUse = if (language == "system") {
            Locale.getDefault().language.takeIf { it.isNotEmpty() } ?: "en"
        } else {
            language
        }
        super.attachBaseContext(LocaleHelper.setLocale(newBase, langToUse))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectivityObserver = ConnectivityObserver(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyCaj10hgcwGaosoYRyv79ppLviFJ9eMNmM")
        }

        //createNotificationChannel(this)
        enableEdgeToEdge()

        hideSystemUI()

        locationHelper = LocationHelper(this)
        locationState = mutableStateOf(null)

        // Apply only for Android 11+ (API 30)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {

            val navController = rememberNavController()

            val deepLinkUri = intent?.data?.toString()

            LaunchedEffect(deepLinkUri) {
                deepLinkUri?.let { uri ->
                    if (uri.startsWith("E:\\ITI material\\android using kotlin\\SkyPeek\\app\\src\\main\\java\\com\\example\\skypeek\\composablescreens\\fav")) {
                        val jsonString = uri.substringAfter("favDetails/")
                        navController.navigate("favDetails/$jsonString")
                    }
                }
            }


            isFAB = remember { mutableStateOf(false) }
            isNAV = remember { mutableStateOf(false) }
            showDetails = remember { mutableStateOf(false) }
            WeatherApp(
                weatherApiService,
                locationState,
                isFAB,
                isNAV,
                showDetails,
                connectivityObserver
            )
            FullScreenEffect()
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


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun WeatherApp(
        apiService: WeatherApiService,
        locationState: MutableState<Location?>,
        isFAB: MutableState<Boolean>,
        isNAV: MutableState<Boolean>,
        showDetails: MutableState<Boolean>,
        connectivityObserver: ConnectivityObserver,

        ) {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavController provides navController) {
            var selectedIndex by remember { mutableStateOf(0) }
            val navigationBarItems = NavigationBarItems.entries.toTypedArray()
            val hapticFeedback = LocalHapticFeedback.current
            val isConnected by connectivityObserver.isConnected.collectAsState(
                initial = checkForInternet(this)
            )
            Scaffold(
                topBar = {
                    if (!isConnected) {
                        OfflineBanner()
                    }
                },
                floatingActionButton = {
                    if (isFAB.value) {
                        FloatingActionButton(
                            onClick = {
                                val currentRoute =
                                    navController.currentBackStackEntry?.destination?.route
                                if (currentRoute != null) {
                                    when {
                                        currentRoute.startsWith(ScreensRoute.FavScreen.route) -> {
                                            navController.navigate("${ScreensRoute.MapScreen.route}/true")
                                        }

                                        currentRoute.startsWith(ScreensRoute.AlertScreen.route) -> {
                                            showDetails.value = true
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_add_box_24),
                                contentDescription = "Add",
                            )
                        }
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
                bottomBar = {
                    // Only show bottom bar if NOT on SplashScreen
                    if (isNAV.value) {
                        AnimatedNavigationBar(
                            modifier = Modifier.height(64.dp),
                            selectedIndex = selectedIndex,
                            cornerRadius = shapeCornerRadius(
                                topLeft = 24.dp,
                                topRight = 24.dp,
                                bottomLeft = 0.dp,
                                bottomRight = 0.dp
                            ),
                            ballAnimation = Parabolic(tween(300)),
                            indentAnimation = Height(tween(600)),
                            barColor = secbackgroundColor,
                            ballColor = loyalBlue
                        ) {
                            navigationBarItems.forEachIndexed { index, item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .noRippleClickableWithVibration(hapticFeedback) {
                                            if (selectedIndex != index) {
                                                selectedIndex = index
                                                navController.navigate(item.route) {
                                                    popUpTo(ScreensRoute.HomeScreen.route) {
                                                        inclusive = false
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(26.dp),
                                        imageVector = item.icon,
                                        contentDescription = "Bottom Nav Icon",
                                        tint = if (selectedIndex == index) loyalBlue else cardBackGround
                                    )
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .customShadow(
                            color = Purple40,
                            alpha = 0.5f,
                            shadowRadius = 16.dp,
                            borderRadius = 42.dp,
                            offsetY = (40).dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        SetupNavHost(
                            apiService, locationState, isFAB, isNAV, showDetails
                        )
                    }
                }
            }
        }
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

    fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars()) // Hide only navigation bar
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }


    @Composable
    fun FullScreenEffect() {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setSystemBarsColor(color = black, darkIcons = false)
        }
    }

    @Composable
    fun OfflineBanner() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.error)
                .padding(top = 45.dp)
                .padding(8.dp)
            ,
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(R.string.you_are_offline),
                color = MaterialTheme.colorScheme.onError,
                fontSize = 16.sp
            )
        }
    }
}
