package com.example.skypeek.composablescreens.alert

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.skypeek.R
import com.example.skypeek.composablescreens.home.LoadingIndicatore
import com.example.skypeek.data.models.AlarmPojo
import com.example.skypeek.data.models.ResponseStateAlarm
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.gray
import com.example.skypeek.utiles.millisToTime

@Composable
fun AlertScreen(
    isNAV: MutableState<Boolean>,
    isFAB: MutableState<Boolean>,
    showDetails: MutableState<Boolean>,
    viewModel: AlarmViewModel
) {
    isNAV.value = true
    isFAB.value = true
    val uiState by viewModel.alarmList.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle("error while loading data")
    LaunchedEffect(Unit) {
        viewModel.gerAlarmList()
    }
    when (uiState) {
        is ResponseStateAlarm.Error -> {
            Text(text = error)
        }

        is ResponseStateAlarm.Loading -> LoadingIndicatore()
        is ResponseStateAlarm.Success -> {
            AlertScreenData(
                isNAV = isNAV,
                isFAB = isFAB,
                showDetails = showDetails,
                viewModel = viewModel,
                list = (uiState as ResponseStateAlarm.Success).data
            )
        }
    }
}

@Composable
fun AlertScreenData(
    isNAV: MutableState<Boolean>,
    isFAB: MutableState<Boolean>,
    showDetails: MutableState<Boolean>,
    viewModel: AlarmViewModel,
    list: List<AlarmPojo>
) {
    isNAV.value = true
    isFAB.value = true
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
            if (list.isEmpty()) {
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
                items(list.size) {
                    FavItem(item = list[it]
                    , viewModel = viewModel)
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
            onDismiss = { showDetails.value = false },
            viewModel = viewModel
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun FavItem(
    item:AlarmPojo,
    viewModel: AlarmViewModel
) {
    val time = item.time
    val timeNow = System.currentTimeMillis()
    val context = LocalContext.current
    val now = millisToTime(timeNow)
    if (now >= time) {
        viewModel.deleteAlarmRoom(item, context)
    }

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
                    text = item.date,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.time)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        isDeleted.value = true
                        viewModel.deleteAlarmRoom(item,context)
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
