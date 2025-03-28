package com.example.skypeek.utiles.helpers

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.skypeek.data.models.LocationPOJO
import java.util.Locale

 fun getAddressFromLocation(lat:Double,lon:Double,context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(lat,lon, 1)
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



