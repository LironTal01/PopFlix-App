package com.example.popiflix

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp
import com.example.popiflix.util.LanguageManager
import com.example.popiflix.workers.WorkManagerModule
import androidx.work.WorkManager
import javax.inject.Inject

// Main Application class
@HiltAndroidApp
class PopFlixApp : Application() {
    
    @Inject
    lateinit var workManager: WorkManager // inject work manager
    
    // Initialize app when created
    override fun onCreate() {
        super.onCreate()
        // Load saved language preference or default to system language
        loadLanguagePreference() // load language settings
        
        // Schedule daily movie updates at 9 AM
        WorkManagerModule.scheduleDailyMovieUpdates(workManager) // start background updates
    }
    
    // Load the saved language preference from SharedPreferences
    private fun loadLanguagePreference() {
        val sharedPreferences = getSharedPreferences("PopFlixSettings", Context.MODE_PRIVATE) // get app preferences
        val savedLanguage = sharedPreferences.getString("language_preference", "auto") ?: "auto" // get saved language or default
        
        // Apply the saved language preference
        LanguageManager.setLanguage(savedLanguage) // set app language
    }
}
