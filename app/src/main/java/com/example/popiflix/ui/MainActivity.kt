package com.example.popiflix.ui

import android.content.Context
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.popiflix.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the app
 * Handles navigation and configuration changes
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appLaunchProgress: ProgressBar
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    // Root destinations (tabs)
    private val rootDestinations = setOf(
        R.id.homeFragment,
        R.id.searchFragment,
        R.id.favoritesFragment,
        R.id.watchlistFragment,
        R.id.settingsFragment
    )

    // Flag to suppress onItemSelected when we change selection programmatically
    private var suppressBottomNavCallback = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedTheme() // apply saved theme from preferences
        setContentView(R.layout.activity_main) // set main layout

        appLaunchProgress = findViewById(R.id.appLaunchProgress) // get progress bar
        navController = findNavController(R.id.container) // get navigation controller
        bottomNavigationView = findViewById(R.id.bottomNavigationView) // get bottom navigation

        appLaunchProgress.isVisible = true // show progress bar initially

        // Tab selection: first try to go back to it if it already exists in history; otherwise navigate to it without duplication
        bottomNavigationView.setOnItemSelectedListener { item ->
            if (suppressBottomNavCallback) return@setOnItemSelectedListener true // ignore if we're changing programmatically

            val target = item.itemId // get selected tab ID
            if (navController.currentDestination?.id == target) return@setOnItemSelectedListener true // already on this tab

            val popped = navController.popBackStack(target, /*inclusive=*/false) // try to go back to existing tab
            if (!popped) { // if tab doesn't exist in history
                val opts = NavOptions.Builder()
                    .setLaunchSingleTop(true) // prevent multiple instances
                    .build()
                navController.navigate(target, null, opts) // navigate to new tab
            }
            true
        }
        bottomNavigationView.setOnItemReselectedListener { /* don't reset history */ }

        // Back: if possible to pop - pop; if on root - exit to background
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.popBackStack()) return // if we can go back, do it
                val currentId = navController.currentDestination?.id // get current screen ID
                if (currentId != null && currentId in rootDestinations) { // if on root tab
                    moveTaskToBack(true) // minimize app
                } else {
                    moveTaskToBack(true) // minimize app
                }
            }
        })

        // Mark the correct tab only for root destinations; in deep screens (like movieDetail) don't touch → Back will return to the screen we came from
        navController.addOnDestinationChangedListener { _, destination, _ ->
            suppressBottomNavCallback = true // prevent infinite loop
            when (destination.id) { // check which screen we're on
                R.id.homeFragment -> bottomNavigationView.selectedItemId = R.id.homeFragment // select home tab
                R.id.searchFragment -> bottomNavigationView.selectedItemId = R.id.searchFragment // select search tab
                R.id.favoritesFragment -> bottomNavigationView.selectedItemId = R.id.favoritesFragment // select favorites tab
                R.id.watchlistFragment -> bottomNavigationView.selectedItemId = R.id.watchlistFragment // select watchlist tab
                R.id.settingsFragment -> bottomNavigationView.selectedItemId = R.id.settingsFragment // select settings tab
                else -> { /* deep screens: don't change tab selection */ }
            }
            suppressBottomNavCallback = false // re-enable tab selection

            if (appLaunchProgress.isVisible) { // if progress bar is still showing
                appLaunchProgress.animate()
                    .alpha(0f).setDuration(300) // fade out animation
                    .withEndAction {
                        appLaunchProgress.isVisible = false // hide progress bar
                        appLaunchProgress.alpha = 1f // reset alpha for next time
                    }.start()
            }
        }

        // Safety net to hide progress
        appLaunchProgress.postDelayed({
            if (appLaunchProgress.isVisible) { // if progress bar is still showing after 1.5 seconds
                appLaunchProgress.animate()
                    .alpha(0f).setDuration(300) // fade out animation
                    .withEndAction {
                        appLaunchProgress.isVisible = false // hide progress bar
                        appLaunchProgress.alpha = 1f // reset alpha for next time
                    }.start()
            }
        }, 1500) // wait 1.5 seconds before hiding
    }

    // Apply saved theme from preferences
    private fun applySavedTheme() {
        val sp = getSharedPreferences("PopFlixSettings", Context.MODE_PRIVATE) // get app preferences
        val savedTheme = sp.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) // get saved theme or default
        AppCompatDelegate.setDefaultNightMode(savedTheme) // apply theme to app
    }

    // Hide progress bar when app resumes
    override fun onResume() {
        super.onResume()
        if (::appLaunchProgress.isInitialized && appLaunchProgress.isVisible) { // if progress bar exists and is visible
            appLaunchProgress.animate()
                .alpha(0f).setDuration(200) // fade out animation
                .withEndAction {
                    appLaunchProgress.isVisible = false // hide progress bar
                    appLaunchProgress.alpha = 1f // reset alpha for next time
                }.start()
        }
    }
}
