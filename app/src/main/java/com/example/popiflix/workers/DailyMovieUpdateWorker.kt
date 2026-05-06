package com.example.popiflix.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.popiflix.data.repositories.MovieRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker that updates movie data daily at 9 AM
 */
class DailyMovieUpdateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerEntryPoint {
        fun movieRepository(): MovieRepository
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                WorkerEntryPoint::class.java
            )
            val movieRepository = entryPoint.movieRepository()
            
            // Load fresh movie data for all carousels
            val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
            
            // Load popular movies (for hero and popular worldwide)
            movieRepository.getPopularMovies(apiKey, 1)
            android.util.Log.d("DailyMovieUpdate", "Popular movies loaded")
            
            // Load new movies in cinemas
            movieRepository.getNowPlayingMovies(apiKey, 1)
            android.util.Log.d("DailyMovieUpdate", "New movies loaded")
            
            // Load upcoming movies
            movieRepository.getUpcomingMovies(apiKey, 1)
            android.util.Log.d("DailyMovieUpdate", "Upcoming movies loaded")
            
            // Log success
            android.util.Log.d("DailyMovieUpdate", "Daily movie update completed successfully - all carousels updated")
            
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("DailyMovieUpdate", "Daily movie update failed", e)
            Result.retry()
        }
    }
}
