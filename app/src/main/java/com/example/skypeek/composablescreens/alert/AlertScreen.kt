package com.example.skypeek.composablescreens.alert

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.R
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.gray

@Composable
fun AlertScreen(
    isNAV: MutableState<Boolean>,
    isFAB: MutableState<Boolean>,
    showDetails: MutableState<Boolean>
) {
    isNAV.value = true
    isFAB.value = true
    val fav = emptyList<String>()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        cardBackGround, gray, cardBackGround
                    )
                )
            ),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            cardBackGround, gray, cardBackGround
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (fav.isEmpty()) {
                item {
                    LottieAnimation(
                        modifier = Modifier
                            .padding(24.dp, bottom = 0.dp)
                            .height(500.dp)
                            .width(500.dp),
                        composition = rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(R.raw.alert_animation)
                        ).value,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(
                        text = stringResource(R.string.no_planned_alerts_yet),
                        fontSize = 24.sp
                    )
                }
            } else {
                items(fav.size) {
                    FavItem()
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )
    }
    if (showDetails.value) {
        AlertDetailsScreen(
            isNAV = isNAV,
            isFAB = isFAB,
            onDismiss = { showDetails.value = false }
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Preview(showBackground = true)
@Composable
fun FavItem(
) {

    val isDeleted = remember { mutableStateOf(false) }
    if (!isDeleted.value) {

         Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(colorResource(R.color.white))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "City",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text ="date & time")
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.darkBlue)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.titleMedium,
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}
