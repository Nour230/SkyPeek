package com.example.skypeek.composablescreens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.R
import com.example.skypeek.ui.theme.backgroundColor
import com.example.skypeek.ui.theme.loyalBlue
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    isNAV: MutableState<Boolean>, navToMainScreen: () -> Unit
) {
    isNAV.value = false
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_animation))
    val progress by animateLottieCompositionAsState(composition)

    LaunchedEffect(key1 = true) {
        delay(4000)
        navToMainScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(600.dp)
                .align(Alignment.TopCenter)
                .padding(top = 170.dp)
        )
        Text(
            text = stringResource(R.string.sky_peek),
            fontSize = 32.sp,
            color = loyalBlue,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 100.dp)
        )
    }
}