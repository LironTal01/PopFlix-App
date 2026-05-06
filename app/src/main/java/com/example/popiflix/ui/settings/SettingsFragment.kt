package com.example.popiflix.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.popiflix.R
import android.widget.TextView
import com.example.popiflix.util.LanguageManager

// SettingsFragment allows users to customize app appearance
class SettingsFragment : Fragment() {

    // UI components
    private lateinit var radioGroupTheme: RadioGroup // theme selection radio group
    private lateinit var radioGroupLanguage: RadioGroup // language selection radio group
    private lateinit var buttonApply: FrameLayout // apply settings button

    // SharedPreferences for saving settings
    private lateinit var sharedPreferences: android.content.SharedPreferences // app preferences

    // Create the fragment view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false) // inflate settings layout
    }

    // Set up UI when view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("PopFlixSettings", Context.MODE_PRIVATE) // get app preferences

        // Initialize UI components
        initializeViews(view) // set up view references

        // Load current settings
        loadCurrentSettings() // load saved settings

        // Set up click listeners
        setupClickListeners() // set up button listeners
    }

    // Handle configuration changes (like theme change)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Reload settings when configuration changes (e.g., theme change)
        loadCurrentSettings() // reload settings
    }

    // Ensure settings are loaded when fragment resumes
    override fun onResume() {
        super.onResume()
        // Ensure settings are properly loaded when fragment becomes visible
        loadCurrentSettings() // reload settings
    }

    // Initialize all UI components
    private fun initializeViews(view: View) {
        radioGroupTheme = view.findViewById(R.id.radioGroupTheme) // get theme radio group
        radioGroupLanguage = view.findViewById(R.id.radioGroupLanguage) // get language radio group
        buttonApply = view.findViewById(R.id.buttonApply) // get apply button
    }

    // Load current settings from SharedPreferences
    private fun loadCurrentSettings() {
        // Load theme preference
        val currentTheme = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) // get saved theme
        when (currentTheme) { // check theme mode
            AppCompatDelegate.MODE_NIGHT_NO -> radioGroupTheme.check(R.id.radioLight) // select light theme
            AppCompatDelegate.MODE_NIGHT_YES -> radioGroupTheme.check(R.id.radioDark) // select dark theme
            else -> radioGroupTheme.check(R.id.radioSystem) // select system theme
        }
        
        // Load language preference
        val currentLanguage = sharedPreferences.getString("language_preference", "auto") ?: "auto" // get saved language
        when (currentLanguage) { // check language
            "en" -> radioGroupLanguage.check(R.id.radioLanguageEnglish) // select English
            "he" -> radioGroupLanguage.check(R.id.radioLanguageHebrew) // select Hebrew
            else -> radioGroupLanguage.check(R.id.radioLanguageAuto) // select auto
        }
    }
    


    // Set up click listeners for settings changes
    private fun setupClickListeners() {
        buttonApply.setOnClickListener {
            applySettings() // apply settings when button clicked
        }
    }
    
    // Apply selected settings and save to SharedPreferences
    private fun applySettings() {
        // Get selected theme
        val selectedTheme = when (radioGroupTheme.checkedRadioButtonId) { // check theme selection
            R.id.radioLight -> AppCompatDelegate.MODE_NIGHT_NO // light theme
            R.id.radioDark -> AppCompatDelegate.MODE_NIGHT_YES // dark theme
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // system theme
        }

        // Get selected language
        val selectedLanguage = when (radioGroupLanguage.checkedRadioButtonId) { // check language selection
            R.id.radioLanguageEnglish -> "en" // English
            R.id.radioLanguageHebrew -> "he" // Hebrew
            else -> "auto" // auto
        }

        // Save settings
        sharedPreferences.edit() // get editor
            .putInt("theme_mode", selectedTheme) // save theme
            .putString("language_preference", selectedLanguage) // save language
            .apply() // apply changes

        // Apply theme immediately for better user experience
        AppCompatDelegate.setDefaultNightMode(selectedTheme) // set theme mode
        
        // Apply language change
        LanguageManager.setLanguage(selectedLanguage) // set language

        // Show success message
        Toast.makeText(requireContext(), getString(R.string.settings_saved), Toast.LENGTH_SHORT).show() // show success message
        
        // Only restart activity if language was changed manually (not "auto")
        if (selectedLanguage != "auto") { // if language was manually selected
            requireActivity().recreate() // restart activity to apply language
        }
    }
}