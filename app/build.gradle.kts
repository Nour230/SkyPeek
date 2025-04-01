import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
   // id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.1.10"

}


android {
    namespace = "com.example.skypeek"
    compileSdk = 35
    val file = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(FileInputStream(file))

    defaultConfig {
        applicationId = "com.example.skypeek"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "apiKeySafe", properties.getProperty("apiKey"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.places)
    implementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //lottie
    implementation (libs.lottie.compose)
    //BottomNav
    implementation(libs.animated.navigation.bar)
    //Navigation
    implementation(libs.androidx.navigation.compose)
    //Serialization for NavArgs
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material3) // Material3
    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    //LiveData & Compose
    implementation (libs.androidx.runtime.livedata)
    //ConstraintLayout
    implementation (libs.androidx.constraintlayout.compose)
    //Location
    implementation(libs.play.services.location)
    implementation (libs.accompanist.systemuicontroller)
    // Android Maps Compose composables for the Maps SDK for Android
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose.v2114)
    implementation(libs.accompanist.permissions)
    implementation (libs.places.v340) // Latest Google Places API
    implementation (libs.material)
    //Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    // Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    //for Kotlin + workManager
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    // Testing
    testImplementation ("io.mockk:mockk-android:1.13.17")
    testImplementation ("io.mockk:mockk-agent:1.13.17")
    testImplementation ("androidx.test:core-ktx:1.6.1")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test:core-ktx:1.6.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation ("org.robolectric:robolectric:4.11")
}