package com.example.skypeek.composablescreens.fav.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

@Composable
fun MapScreen(locationViewModel: MutableState<Location?>) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyCaj10hgcwGaosoYRyv79ppLviFJ9eMNmM") // Replace with your actual API key
        }
    }

    val placesClient = remember { Places.createClient(context) }

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                fetchPredictions(it.text, placesClient) { newPredictions ->
                    predictions = newPredictions
                }
            },
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
                        fetchPlaceDetails(prediction.placeId, placesClient, locationViewModel)
                        searchQuery = TextFieldValue(prediction.getPrimaryText(null).toString())
                        predictions = emptyList()
                    }
            )
        }

        GoogleMapScreen(locationViewModel)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GoogleMapScreen(location: MutableState<Location?>) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    MapView(location, )
}

@SuppressLint("MissingPermission")
@Composable
fun MapView(location: MutableState<Location?>) {
    val cameraPositionState = rememberCameraPositionState()

    // Initial marker position (default or first location)
    val markerState = rememberMarkerState(
        position = location.value?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(0.0, 0.0)
    )

    LaunchedEffect(location.value) {
        location.value?.let {
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
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
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
                location.value?.let {
                    Text("Latitude: ${it.latitude}")
                    Text("Longitude: ${it.longitude}")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Add to Favorites Logic */ }) {
                    Text("Add to Favorites")
                }
            }
        }
    }
}

private fun fetchPlaceDetails(placeId: String, placesClient: PlacesClient, locationViewModel: MutableState<Location?>) {
    val placeFields = listOf(Place.Field.LAT_LNG)
    val request = FetchPlaceRequest.builder(placeId, placeFields).build()

    placesClient.fetchPlace(request).addOnSuccessListener { response ->
        response.place.latLng?.let { latLng ->
            locationViewModel.value = Location("").apply {
                latitude = latLng.latitude
                longitude = latLng.longitude
            }
        }
    }.addOnFailureListener { exception ->
        Log.e("PlacesAutocomplete", "Error fetching place details: ${exception.message}")
    }
}
private fun fetchPredictions(query: String, placesClient: PlacesClient, onResults: (List<AutocompletePrediction>) -> Unit) {
    if (query.isEmpty()) return // Avoid unnecessary API calls

    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            Log.d("TAG", "Predictions fetched: ${response.autocompletePredictions.size}")
            onResults(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            Log.e("TAG", "Error: ${exception.message}")
        }
}
