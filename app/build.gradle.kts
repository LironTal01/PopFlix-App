import org.gradle.api.GradleException
import java.util.Properties

plugins {
    id("com.android.application")                    // Android application plugin
    id("org.jetbrains.kotlin.android")              // Kotlin support
    id("org.jetbrains.kotlin.plugin.parcelize")     // Parcelable support for data classes
    id("com.google.dagger.hilt.android")            // Dependency injection with Hilt
    id("androidx.navigation.safeargs.kotlin")       // Safe navigation with type-safe arguments
    kotlin("kapt")                                  // Kotlin annotation processing
}

val localProperties = Properties().apply {
    val propFile = rootProject.file("local.properties")
    if (propFile.exists()) {
        propFile.inputStream().use { load(it) }
    }
}

val tmdbApiKey: String = localProperties.getProperty("TMDB_API_KEY")
    ?: throw GradleException("TMDB_API_KEY missing in local.properties. הוסף שורה כמו TMDB_API_KEY=your_key")

android {
    namespace = "com.example.popiflix"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.popiflix"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
        // Force-package both English and Hebrew resources
        resConfigs("en", "he", "iw")

        // If you ever build App Bundles, do not split languages (ship both)
        bundle {
            language {
                enableSplit = false
            }
        }
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
}

dependencies {
    // Core Android dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // RecyclerView for displaying lists
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    
    



    // Lifecycle components for MVVM architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    
    // Coroutines for asynchronous programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // WorkManager for background tasks
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Navigation component for fragment navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    // Retrofit for API calls to TMDB
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Hilt for dependency injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-fragment:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Room for local database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
