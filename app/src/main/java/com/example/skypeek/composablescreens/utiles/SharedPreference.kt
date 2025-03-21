package com.example.skypeek.composablescreens.utiles

import android.content.Context
import android.util.Log
import androidx.core.content.edit

fun saveToSharedPrefrence(context: Context, value: String, key: String) {
    Log.d("TAG", "saveToSharedPrefrence: $value")
    val sheredPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    sheredPref.edit() {
        putString(key, value)
    }
}

fun getFromSharedPrefrence(context: Context, key: String): String? {
    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    return sharedPref.getString(key, null)
}

fun deleteSharedPrefrence(context: Context,key: String) {
    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    sharedPref.edit { remove(key) }
}