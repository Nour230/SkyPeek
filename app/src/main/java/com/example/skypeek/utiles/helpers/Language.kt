package com.example.skypeek.utiles.helpers

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import com.example.skypeek.MainActivity
import com.example.skypeek.utiles.SharedPreference
import java.util.*
import androidx.core.content.edit

object LocaleHelper {
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val newContext = context.createConfigurationContext(config)
        newContext.resources.updateConfiguration(config, newContext.resources.displayMetrics)

        return newContext
    }


    fun updateResources(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

fun changeLanguage(context: Context, languageCode: String, language: String) {
    SharedPreference.setLanguage(context, languageCode, language)
    LocaleHelper.updateResources(context, languageCode)

    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    sharedPref.edit() { putBoolean("SkipSplash", true) }
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}



fun formatNumberBasedOnLanguage(context: Context, number: Any): String {
    val language = SharedPreference.getLanguage(context, "language")
    val numberStr = when (number) {
        is Int -> number.toString()
        is Double -> number.toString() // or keep decimals if needed
        is Float -> number.toString()
        is String -> number
        else -> number.toString()
    }
    return if (language == "ar") convertToArabicNumbers(numberStr) else numberStr
}

fun convertToArabicNumbers(number: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.map {
        if (it.isDigit()) arabicDigits[it.toString().toInt()] else it
    }.joinToString("")
}

fun formatTemperatureUnitBasedOnLanguage(unit: String): String {
    val language = Locale.getDefault().language
    if (language == "ar") {
        return when (unit) {
            "°C" -> "°س"
            "°F" -> "°ف"
            "°K" -> "°ك"
            "mph"->"م/س"
            "m/s"->"م/ث"
            else -> "°س"
        }
    }
    return unit
}
