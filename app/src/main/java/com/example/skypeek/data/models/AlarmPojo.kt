package com.example.skypeek.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "alarm")
data class AlarmPojo(val time: String,val date: String,@PrimaryKey(autoGenerate = true) val id:Int=0)
