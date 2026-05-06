buildscript {
    repositories {
        google()        // Google's Maven repository for Android libraries
        mavenCentral()  // Central Maven repository for Java/Kotlin libraries
    }
    dependencies {
        // Hilt plugin for dependency injection
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
        
        // Safe Args plugin for type-safe navigation
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
    }
}

plugins {
    // Android application plugin for building APKs
    alias(libs.plugins.android.application) apply false
    
    // Kotlin plugin for Android development
    alias(libs.plugins.kotlin.android) apply false
}
