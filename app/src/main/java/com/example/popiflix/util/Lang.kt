package com.example.popiflix.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

// Language utility for consistent language handling
object Lang {

    // Get current app locale
    fun currentAppLocale(): Locale {
        val list: LocaleListCompat = AppCompatDelegate.getApplicationLocales() // get app locales
        return list[0] ?: Locale.getDefault() // return first locale or default
    }

    // Get TMDB-friendly language tag for API calls
    fun tmdbLangTag(): String {
        val loc = currentAppLocale() // get current locale
        return when (loc.language) { // check language code
            "he", "iw" -> "he-IL" // normalize Hebrew
            "en"       -> "en-US" // use US English for better catalog
            else       -> loc.toLanguageTag().ifBlank { "en-US" } // use locale tag or default to en-US
        }
    }

    // Get short app language code
    fun appLanguageCode(): String =
        currentAppLocale().language.let { if (it == "iw") "he" else it } // normalize Hebrew code
}
