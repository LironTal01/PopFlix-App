package com.example.popiflix.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

// Language management utility for app language switching
object LanguageManager {

    // Set app language
    fun setLanguage(languageCode: String) {
        val locales = when (languageCode.lowercase()) { // check language code
            "auto"      -> LocaleListCompat.getEmptyLocaleList() // follow system
            "he", "iw"  -> LocaleListCompat.forLanguageTags("he") // normalize Hebrew
            "en"        -> LocaleListCompat.forLanguageTags("en") // English
            else        -> LocaleListCompat.forLanguageTags(languageCode) // other languages
        }
        AppCompatDelegate.setApplicationLocales(locales) // apply language change
    }

    // Get current app language code
    fun getCurrentLanguage(): String {
        val appLocales = AppCompatDelegate.getApplicationLocales() // get app locales
        if (appLocales.isEmpty) return "auto" // if empty, following system
        val lang = appLocales[0]?.language ?: return "auto" // get first language
        return if (lang == "iw") "he" else lang // normalize Hebrew code
    }
}
