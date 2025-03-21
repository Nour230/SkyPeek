package com.example.skypeek.composablescreens.fav.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun MapScreen(viewModel: MapViewModel, locationState: MutableState<Location?>) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val predictions by viewModel.predictions.collectAsStateWithLifecycle()
    val location by viewModel.location.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            label = { Text("Search Places") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        predictions.forEach { prediction ->
            Text(
                text = prediction.getPrimaryText(null).toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        viewModel.onPlaceSelected(prediction.placeId)
                    }
            )
        }

        GoogleMapScreen(location, locationState)
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapScreen(location: Location?, locationState: MutableState<Location?>) {
    val permissionState =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    MapView(location, locationState)
}

@SuppressLint("MissingPermission")
@Composable
fun MapView(location: Location?, locationState: MutableState<Location?>) {

    val cameraPositionState = rememberCameraPositionState()

    // Initial marker position (default or first location)
    val markerState = rememberMarkerState(
        position = LatLng(
            location?.latitude ?: locationState.value?.latitude ?: 0.0, // Use locationState if location is null
            location?.longitude ?: locationState.value?.longitude ?: 0.0 // Use locationState if location is null
        )
    )

    LaunchedEffect(location) {
        location?.let {
            val newLatLng = LatLng(it.latitude, it.longitude)

            // Update marker position
            markerState.position = newLatLng

            // Move camera smoothly to new location
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newLatLng, 12f))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapClick = { latLng ->
                markerState.position = latLng
            }
        ) {
            Marker(
                state = markerState, // Marker updates dynamically
                title = "Selected Location",
                snippet = "Lat: ${markerState.position.latitude}, Lng: ${markerState.position.longitude}"
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Latitude: ${markerState.position.latitude}",
                )
                Text(
                    text = "Longitude: ${markerState.position.longitude}",
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Add to Favorites Logic */ }) {
                    Text("Add to Favorites")
                }
            }
        }
    }
}

