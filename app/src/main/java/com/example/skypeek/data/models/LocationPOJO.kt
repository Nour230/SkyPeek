package com.example.skypeek.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationPOJO(@PrimaryKey(autoGenerate = true) val id :Int, val lat:Long, val long :Long)
