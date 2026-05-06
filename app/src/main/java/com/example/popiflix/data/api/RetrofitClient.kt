package com.example.popiflix.data.api

import androidx.annotation.VisibleForTesting
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

/**
 * Retrofit client configuration
 * Provides configured Retrofit instance for API calls
 */
object RetrofitClient {

    @VisibleForTesting
    internal fun deviceLangTagForTmdb(): String {
        // Follow the DEVICE language (emulator setting), not per-app
        val tag = Locale.getDefault().toLanguageTag().lowercase()  // e.g., "he-IL" -> "he-il"
        return when (tag) {
            "he", "iw", "he-il", "iw-il" -> "he-IL" // Hebrew language tag
            "en", "en-il", "en-us"       -> "en-US" // English language tag
            else                         -> tag.replaceFirstChar { it.lowercase(Locale.ROOT) } // other languages
        }
    }

    private val languageInterceptor = Interceptor { chain ->
        val original = chain.request() // get original request
        val apiLanguage = deviceLangTagForTmdb() // get language tag for API

        val newUrl = original.url.newBuilder()
            .setQueryParameter("language", apiLanguage) // add language parameter to URL
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl) // set new URL with language parameter
            .build()

        chain.proceed(newRequest) // proceed with modified request
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC // set logging level to basic
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(languageInterceptor) // add language interceptor
        .addInterceptor(logging) // add logging interceptor
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/") // set TMDB API base URL
        .client(okHttp) // set OkHttp client
        .addConverterFactory(GsonConverterFactory.create()) // add Gson converter
        .build()

    val apiService: TmdbApi = retrofit.create(TmdbApi::class.java) // create API service instance
}
