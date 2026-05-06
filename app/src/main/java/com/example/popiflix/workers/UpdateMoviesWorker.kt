package com.example.popiflix.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.popiflix.data.repositories.MovieRepository
import com.example.popiflix.BuildConfig
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

// Background worker for updating popular movies
class UpdateMoviesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // Execute background work
    override suspend fun doWork(): Result {
        return try {
            // Get MovieRepository through Hilt
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                UpdateMoviesWorkerEntryPoint::class.java
            ) // get Hilt entry point
            val movieRepository = hiltEntryPoint.getMovieRepository() // get repository
            
            // Update popular movies
            val apiKey = BuildConfig.TMDB_API_KEY // get API key
            val response = movieRepository.getPopularMovies(apiKey, 1) // fetch popular movies
            
            // Log success
            android.util.Log.d("UpdateMoviesWorker", "Successfully updated ${response.results.size} popular movies") // log success
            
            Result.success() // return success
        } catch (e: Exception) {
            // Log error
            android.util.Log.e("UpdateMoviesWorker", "Failed to update movies: ${e.message}") // log error
            Result.retry() // retry on error
        }
    }
}

// Hilt entry point for worker
@EntryPoint
@InstallIn(SingletonComponent::class)
interface UpdateMoviesWorkerEntryPoint {
    fun getMovieRepository(): com.example.popiflix.data.repositories.MovieRepository // get movie repository
}
