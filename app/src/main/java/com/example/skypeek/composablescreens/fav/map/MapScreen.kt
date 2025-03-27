package com.example.skypeek.composablescreens.fav.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skypeek.R
import com.example.skypeek.composablescreens.utiles.saveToSharedPrefrence
import com.example.skypeek.ui.theme.cardBackGround
import com.example.skypeek.ui.theme.loyalBlue
import com.example.skypeek.ui.theme.white
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    locationState: MutableState<Location?>,
    isFAB: MutableState<Boolean>,
    isNAV: MutableState<Boolean>,
    isFAVORITE: Boolean,
    onLocationSelected: (LatLng) -> Unit = {}
) {
    isFAB.value = false
    isNAV.value = false
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val predictions by viewModel.predictions.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMapScreen(location, locationState, viewModel, isFAVORITE, onLocationSelected)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text(stringResource(R.string.search_places)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(cardBackGround),
                textStyle = TextStyle(
                    color = white,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
            )
            predictions.forEach { prediction ->
                Text(
                    text = prediction.getPrimaryText(null).toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            viewModel.onPlaceSelected(prediction.placeId)
                        },
                    style = MaterialTheme.typography.titleMedium,
                    color = loyalBlue
                )
            }
        }
    }

}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapScreen(
    location: Location?,
    locationState: MutableState<Location?>,
    viewModel: MapViewModel,
    isFAVORITE: Boolean,
    onLocationSelected: (LatLng) -> Unit = {}
) {
    val context = LocalContext.current
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    // Define a callback for when the marker position changes
    val onAction: (LatLng) -> Unit = { latLng ->
        if (isFAVORITE) {
            viewModel.insertLocation(latLng.latitude, latLng.longitude)
        } else {
            onLocationSelected(latLng)
            saveToSharedPrefrence(context = context, latLng.latitude.toString(), "lat")
            saveToSharedPrefrence(context = context, latLng.longitude.toString(), "long")
        }
    }

    MapView(
        location = location,
        locationState = locationState,
        actionName = if (isFAVORITE) stringResource(R.string.add_to_favourites) else stringResource(
            R.string.select_location
        ),
        onAction = onAction
    )
}

@SuppressLint("MissingPermission")
@Composable
fun MapView(
    location: Location?,
    locationState: MutableState<Location?>,
    actionName: String,
    onAction: (LatLng) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val markerState = rememberMarkerState(
        position = LatLng(
            location?.latitude ?: locationState.value?.latitude ?: 0.0,
            location?.longitude ?: locationState.value?.longitude ?: 0.0
        )
    )

    LaunchedEffect(location) {
        location?.let {
            val newLatLng = LatLng(it.latitude, it.longitude)
            markerState.position = newLatLng
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newLatLng, 12f))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapClick = { latLng ->
                markerState.position = latLng
            }
        ) {
            Marker(
                state = markerState,
                title = stringResource(
                    R.string.select_location
                ),
                snippet = stringResource(R.string.lat, markerState.position.latitude) + stringResource(
                    R.string.lng, markerState.position.longitude
                )
            )
        }
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
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
                    text = stringResource(
                        R.string.select_location),
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.black),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.latitude, markerState.position.latitude))
                Text(text = stringResource(R.string.longitude, markerState.position.longitude))
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onAction(markerState.position)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = if (actionName == context.getString(R.string.add_to_favourites))
                                    context.getString(R.string.location_added_to_favorites)
                                else
                                    context.getString(R.string.location_selected_for_weather),
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.darkBlue)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = actionName,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorResource(R.color.white),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

