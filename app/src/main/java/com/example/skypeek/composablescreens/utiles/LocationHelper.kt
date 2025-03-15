package com.example.skypeek.composablescreens.utiles

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.provider.Settings
import android.util.Log

const val REQUEST_LOCATION_PERMISSION = 2005
class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Request location permissions */
    fun requestLocationPermissions(activity: androidx.activity.ComponentActivity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    /** Check if GPS/Network location is enabled */
    fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /** Open location settings */
    fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    /** Fetch the last known location */
    @SuppressLint("MissingPermission")
    fun getFreshLocation(onLocationReceived: (Location?) -> Unit) {
        if (!hasLocationPermissions()) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationReceived(location)
                } else {
                    Log.e("TAG", "LocationHelper Failed to retrieve location.")
                    onLocationReceived(null)
                }
            }
            .addOnFailureListener {
                Log.e("TAG", "LocationHelper Failed to get location: ${it.message}")
                onLocationReceived(null)
            }
    }
}