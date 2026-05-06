package com.example.popiflix.data.di

import com.example.popiflix.BuildConfig
import com.example.popiflix.data.api.TmdbApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Singleton

/**
 * Network module for dependency injection
 * Provides Retrofit, OkHttp, and API-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.themoviedb.org/3/" // TMDB API base URL

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create() // provide Gson instance for JSON parsing

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor() // create logging interceptor
        interceptor.level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY // full logging in debug mode
        else
            HttpLoggingInterceptor.Level.NONE // no logging in release mode
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request() // get original request
                val newUrl = original.url.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY) // add API key to all requests
                    .build()
                val newRequest = original.newBuilder().url(newUrl).build() // build new request
                chain.proceed(newRequest) // proceed with modified request
            }
            .addInterceptor { chain ->
                val original = chain.request() // get original request
                val apiLanguage = getLanguageForApi() // get current language
                val newUrl = original.url.newBuilder()
                    .setQueryParameter("language", apiLanguage) // add language parameter
                    .build()
                val newRequest = original.newBuilder().url(newUrl).build() // build new request
                chain.proceed(newRequest) // proceed with modified request
            }
            .addInterceptor(loggingInterceptor) // add logging interceptor
            .build() // build OkHttp client
    }

    private fun getLanguageForApi(): String {
        val currentLanguage = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: Locale.getDefault().language // get current app language
        return when (currentLanguage) {
            "iw", "he" -> "he-IL" // return Hebrew language tag
            "en" -> "en-US" // return English language tag
            else -> "en-US" // default to English
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL) // set TMDB API base URL
            .addConverterFactory(GsonConverterFactory.create(gson)) // add Gson converter
            .client(client) // set OkHttp client
            .build() // build Retrofit instance

    @Provides
    @Singleton
    fun provideTmdbApi(retrofit: Retrofit): TmdbApi =
        retrofit.create(TmdbApi::class.java) // create TMDB API service
}
