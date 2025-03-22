package com.example.skypeek.data.models

import androidx.room.Entity

@Entity(tableName = "location", primaryKeys = ["lat", "long"])
data class LocationPOJO(
   val lat: Double,
   val long: Double
)
