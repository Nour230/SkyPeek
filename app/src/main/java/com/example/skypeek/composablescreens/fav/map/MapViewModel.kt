package com.example.skypeek.composablescreens.fav.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skypeek.data.models.LocationPOJO
import com.example.skypeek.data.repository.WeatherRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val placesClient: PlacesClient,val repo: WeatherRepository) : ViewModel() {

    // State for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery

    // State for autocomplete predictions
    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions: StateFlow<List<AutocompletePrediction>> get() = _predictions

    // State for selected location
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> get() = _location

    // Update search query and fetch predictions
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        fetchPredictions(query)
    }

     //Fetch place details when a prediction is selected
    fun onPlaceSelected(placeId: String) {
        fetchPlaceDetails(placeId)
    }

    // Fetch autocomplete predictions
    private fun fetchPredictions(query: String) {
        if (query.isEmpty()) return
        viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()
            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _predictions.value = response.autocompletePredictions
                }
                .addOnFailureListener { exception ->
                    Log.e("MapViewModel", "Error fetching predictions: ${exception.message}")
                }
        }
    }

    // Fetch place details
     fun fetchPlaceDetails(placeId: String) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                response.place.latLng?.let { latLng ->
                    _location.value = Location("").apply {
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapViewModel", "Error fetching place details: ${exception.message}")
            }
    }

    fun insertLocation(lat: Double, lon: Double) {
        Log.d("TAG", "Inserting location: Lat=$lat, Lon=$lon")
        val location = LocationPOJO(lat = lat, long = lon)
        viewModelScope.launch {
            repo.insertLocation(location)
        }
    }
}


class MapFactory(private val placesClient: PlacesClient,val repo: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(placesClient,repo) as T
    }
}