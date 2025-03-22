package com.example.skypeek.composablescreens.fav

import android.location.Geocoder
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.example.skypeek.R
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.models.ResponseStateFav
import java.util.Locale

@Composable
fun FavScreen(viewModel: FavViewModel, isFAB: MutableState<Boolean>) {
    isFAB.value = true
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
            viewModel = viewModel
        )
    }
}

@Composable
fun StartFavScreen(fav: List<LocationPOJO>,viewModel: FavViewModel) {
//    val navController = LocalNavController.current
//    Scaffold(
//
//        floatingActionButton = {
//            FloatingActionButton(onClick = {
//                navController.navigate(ScreensRoute.MapScreen.route)
//            }) {
//                Icon(
//                    painter = painterResource(R.drawable.baseline_add_24),
//                    contentDescription = "Add"
//                )
//            }
//        },
//        floatingActionButtonPosition = FabPosition.End
//    ) { paddingValues ->
//
//    }
    FavScreenContent(fav,viewModel)
}

@Composable
fun FavScreenContent(fav: List<LocationPOJO>,viewModel: FavViewModel) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun FavItem(data: LocationPOJO,viewModel: FavViewModel) {
    val city = getAddressFromLocation(data)
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(colorResource(R.color.cardBackground))
    ){
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
                onClick = { viewModel.deleteFromRoom(data)},
                colors = ButtonDefaults.buttonColors(colorResource(R.color.darkBlue)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Delete"
                )
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

@Composable
private fun getAddressFromLocation(location: LocationPOJO): String {
    val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(location.lat, location.long, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            address.getCountryName()
        } else {
            "Address not found"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Error fetching address"
    }
}