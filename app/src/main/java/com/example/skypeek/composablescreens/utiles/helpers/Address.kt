package com.example.skypeek.composablescreens.utiles.helpers

import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.skypeek.data.models.LocationPOJO
import java.util.Locale

@Composable
 fun getAddressFromLocation(location: LocationPOJO): String {
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



