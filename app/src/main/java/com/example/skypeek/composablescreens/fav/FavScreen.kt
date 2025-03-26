package com.example.skypeek.composablescreens.fav

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.example.skypeek.R
import com.example.skypeek.composablescreens.utiles.helpers.getAddressFromLocation
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.ResponseStateFav
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.gray
import kotlinx.coroutines.launch

@Composable
fun FavScreen(
    viewModel: FavViewModel,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>,
    goToFavDetailsScreen: (LocationPOJO) -> Unit = {}
) {
    isFAB.value = true
    isNAV.value = true
    val uiState by viewModel.favList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.gerFavList()
    }
    when (uiState) {
        is ResponseStateFav.Error -> Text(
            text = (uiState as ResponseStateFav.Error).message.toString()
        )

        is ResponseStateFav.Loading -> LoadingIndicatore()
        is ResponseStateFav.Success -> StartFavScreen(
            fav = (uiState as ResponseStateFav.Success).data,
            viewModel = viewModel,
            goToFavDetailsScreen = goToFavDetailsScreen
        )
    }
}

@Composable
fun StartFavScreen(
    fav: List<LocationPOJO>,
    viewModel: FavViewModel,
    goToFavDetailsScreen: (LocationPOJO) -> Unit = {}
) {
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
                            .padding(24.dp)
                            .height(400.dp)
                            .width(400.dp),
                        composition = com.airbnb.lottie.compose.rememberLottieComposition(
                            spec = com.airbnb.lottie.compose.LottieCompositionSpec.RawRes(
                                R.raw.fav_animation
                            )
                        ).value,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(
                        text = "There is no Favorites yet",
                        fontSize = 24.sp
                    )
                }
            } else {
                items(fav.size) {
                    FavItem(
                        data = fav[it],
                        viewModel = viewModel,
                        goToFavDetailsScreen = goToFavDetailsScreen,
                        snack = snackbarHostState
                    )
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
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun FavItem(
    data: LocationPOJO,
    viewModel: FavViewModel,
    goToFavDetailsScreen: (LocationPOJO) -> Unit = {},
    snack: SnackbarHostState
) {
    val city = getAddressFromLocation(data)
    val deleted by viewModel.isDelete.collectAsStateWithLifecycle("item deleted from favorite")
    val coroutineScope = rememberCoroutineScope()
    val isDeleted = remember { mutableStateOf(false) }
    if (!isDeleted.value) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clickable { goToFavDetailsScreen(data) },
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(colorResource(R.color.cardBackground)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "City is : $city",
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isDeleted.value = true
                            val result = snack.showSnackbar(
                                message = deleted,
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short,
                                withDismissAction = true,
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                isDeleted.value = false
                            } else {
                                viewModel.deleteFromRoom(data)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.cardBackgroundgray)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }

}


@Composable
private fun LoadingIndicatore() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        CircularProgressIndicator()
    }
}

