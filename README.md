# 🌤️ SkyPeek

**SkyPeek** is an Android mobile application that provides real-time weather updates and temperature details based on your current location. 🌍✨ Users can also pick a specific location on the map or search for it using an auto-complete edit text and save it to their **favorite locations** for quick access to weather information. 🌦️📍

---

## 🌟 Features

✅ **Real-time Weather Data** – Get up-to-date weather status and temperature from your location.  
✅ **Location Selection** – Pick a place on the map or search for it using auto-complete.  
✅ **Favorite Locations** – Save frequently accessed locations for easy retrieval.  
✅ **Weather Alerts** – Set alerts for extreme weather conditions like rain ☔, wind 💨, fog 🌫️, snow ❄️, and high/low temperatures 🌡️.  

---

## 🛠️ Dependencies

Copy and paste the following dependencies into your `build.gradle` file:

```gradle
// Bottom Navigation
implementation(libs.animated.navigation.bar)

// Navigation
implementation(libs.androidx.navigation.compose)

// Material 3 Design Components
implementation(libs.material3)

// Constraint Layout for Compose
implementation(libs.androidx.constraintlayout.compose)

// Accompanist System UI Controller
implementation(libs.accompanist.systemuicontroller)

// Material Design Components
implementation(libs.material)

// Kotlinx Serialization for NavArgs
implementation(libs.kotlinx.serialization.json)

// Room Database
implementation("androidx.room:room-runtime:2.6.1")

// Room Compiler (KSP)
ksp("androidx.room:room-compiler:2.6.1")

// Room Kotlin Extensions & Coroutines
implementation("androidx.room:room-ktx:2.6.1")

// LiveData & Compose Integration
implementation(libs.androidx.runtime.livedata)

// Retrofit for API Calls
implementation(libs.retrofit)

// Gson Converter for Retrofit
implementation(libs.converter.gson)

// Logging Interceptor for Debugging API Requests
implementation(libs.logging.interceptor)

// Location and Maps
implementation(libs.play.services.location)

// Google Maps SDK for Android
implementation(libs.play.services.maps)

// Android Maps Compose Composables
implementation(libs.maps.compose.v2114)

// Google Places API
implementation(libs.places.v340)

// Accompanist Permissions Handling
implementation(libs.accompanist.permissions)

// WorkManager for Background Processing
implementation("androidx.work:work-runtime-ktx:2.7.1")
```

---

## 🚀 Setup

To get started with **SkyPeek**, follow these steps:

1️⃣ **Clone the repository**:  
   ```bash
   git clone https://github.com/Nour230/SkyPeek.git
   ```

2️⃣ **Open the project in Android Studio.**

3️⃣ **Ensure all dependencies are correctly synced.**

4️⃣ **Build and run the application on your Android device or emulator.** 📱⚡

---

