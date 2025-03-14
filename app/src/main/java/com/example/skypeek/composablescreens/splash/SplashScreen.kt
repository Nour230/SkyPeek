package com.example.skypeek.composablescreens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.R
import com.example.skypeek.ui.theme.*
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navToMainScreen: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_animation))
    val progress by animateLottieCompositionAsState(composition)

    LaunchedEffect(key1 = true) {
        delay(4000)
        navToMainScreen()
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = background),
        contentAlignment = Alignment.Center
    ) {
        // LottieAnimation centered below the text
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(500.dp)
                . align(Alignment.TopCenter)
                .padding(top = 160.dp)
        )
        // Text at the top center
        Text(
            text = "SKYPEEK",
            fontSize = 32.sp,
            color = loyalBlue,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}