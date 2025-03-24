package com.example.skypeek.data.models

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "location", primaryKeys = ["lat", "long"])
data class LocationPOJO(
   val lat: Double,
   val long: Double
)
