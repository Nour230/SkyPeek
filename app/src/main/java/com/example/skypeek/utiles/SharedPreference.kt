package com.example.skypeek.utiles

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

fun saveToSharedPrefrence(context: Context, value: String, key: String) {
   // Log.d("TAG", "saveToSharedPrefrence: $value")
    val prefs: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    prefs.edit().putString(key, value).apply()
}

fun getFromSharedPrefrence(context: Context, key: String): String? {
    val prefs: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    return prefs.getString(key, null) // ✅ Ensure default value is handled
}

fun deleteSharedPrefrence(context: Context,key: String) {
    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    sharedPref.edit { remove(key) }
}

object SharedPreference {
fun setLanguage(context: Context, languageCode: String, LANGUAGE_KEY: String) {
    val prefs: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    prefs.edit() { putString(LANGUAGE_KEY, languageCode) }
}

fun getLanguage(context: Context, LANGUAGE_KEY: String): String {
    val prefs: SharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    Log.i("TAG", "getLanguage: ${prefs.getString(LANGUAGE_KEY, "en")}")
    return prefs.getString(LANGUAGE_KEY, "en") ?: "en"
}}