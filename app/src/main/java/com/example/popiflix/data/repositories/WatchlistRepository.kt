package com.example.popiflix.data.repository

import androidx.lifecycle.LiveData
import com.example.popiflix.data.database.WatchlistMovie
import com.example.popiflix.data.database.WatchlistMovieDao
import com.example.popiflix.data.models.MovieDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for watchlist movies operations
 * Handles database operations for watchlist movies
 */
@Singleton
class WatchlistRepository @Inject constructor(
    private val watchlistMovieDao: WatchlistMovieDao,
    private val movieRepository: com.example.popiflix.data.repositories.MovieRepository
) {

    fun getAllWatchlistMovies(): LiveData<List<WatchlistMovie>> {
        return watchlistMovieDao.getAllWatchlistMovies() // get all movies from watchlist
    }

    fun getUnwatchedMovies(): LiveData<List<WatchlistMovie>> {
        return watchlistMovieDao.getUnwatchedMovies() // get movies that haven't been watched yet
    }

    fun getWatchedMovies(): LiveData<List<WatchlistMovie>> {
        return watchlistMovieDao.getWatchedMovies() // get movies that have been watched
    }

    fun getMoviesByPriority(priority: Int): LiveData<List<WatchlistMovie>> {
        return watchlistMovieDao.getMoviesByPriority(priority) // get movies by specific priority level
    }

    suspend fun getWatchlistMovieById(movieId: Int): WatchlistMovie? {
        return withContext(Dispatchers.IO) {
            watchlistMovieDao.getWatchlistMovieById(movieId) // get specific movie from watchlist by ID
        }
    }

    suspend fun searchWatchlistMovies(query: String): List<WatchlistMovie> {
        return withContext(Dispatchers.IO) {
            watchlistMovieDao.searchWatchlistMovies(query) // search movies in watchlist by query
        }
    }


    suspend fun addToWatchlist(movieDetails: MovieDetails, priority: Int = 0, userNotes: String? = null) {
        withContext(Dispatchers.IO) {
            // Convert genres list to comma-separated string for storage
            val genresString = movieDetails.genres.joinToString(",") { it.name } // join genres with commas
            
            val watchlistMovie = WatchlistMovie(
                id = movieDetails.id, // movie ID
                title = movieDetails.title, // movie title
                titleEn = movieDetails.title, // Always save English title
                titleHe = movieDetails.title, // For now, save the same title in Hebrew (you can implement Hebrew translation later)
                overview = movieDetails.overview, // movie description
                posterPath = movieDetails.posterPath, // poster image path
                backdropPath = movieDetails.backdropPath, // backdrop image path
                releaseDate = movieDetails.releaseDate, // release date
                voteAverage = movieDetails.voteAverage, // average rating
                voteCount = movieDetails.voteCount, // number of votes
                popularity = movieDetails.popularity, // popularity score
                priority = priority, // user priority level
                userNotes = userNotes, // user notes
                genres = genresString // Save genres
            )
            watchlistMovieDao.insertWatchlistMovie(watchlistMovie) // save to database
        }
    }

    suspend fun removeFromWatchlist(movie: WatchlistMovie) {
        withContext(Dispatchers.IO) {
            watchlistMovieDao.deleteWatchlistMovie(movie) // remove movie from watchlist
        }
    }

    suspend fun removeFromWatchlist(movieId: Int) {
        withContext(Dispatchers.IO) {
            watchlistMovieDao.deleteWatchlistMovieById(movieId) // remove movie by ID from watchlist
        }
    }

    suspend fun isInWatchlist(movieId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            watchlistMovieDao.isInWatchlist(movieId) // check if movie is in watchlist
        }
    }

    suspend fun markAsWatched(movieId: Int, isWatched: Boolean = true) {
        withContext(Dispatchers.IO) {
            val dateWatched = if (isWatched) System.currentTimeMillis() else null // set current time if watched
            watchlistMovieDao.updateWatchedStatus(movieId, isWatched, dateWatched) // update watched status
        }
    }

    suspend fun updateNotes(movieId: Int, notes: String?) {
        withContext(Dispatchers.IO) {
            watchlistMovieDao.updateNotes(movieId, notes) // update user notes for movie
        }
    }

    suspend fun updatePriority(movieId: Int, priority: Int) {
        withContext(Dispatchers.IO) {
            watchlistMovieDao.updatePriority(movieId, priority) // update priority level for movie
        }
    }

    suspend fun setReminder(movieId: Int, reminderDate: Long?) {
        withContext(Dispatchers.IO) {
            watchlistMovieDao.updateReminderDate(movieId, reminderDate) // set reminder date for movie
        }
    }

    suspend fun getUnwatchedCount(): Int {
        return withContext(Dispatchers.IO) {
            watchlistMovieDao.getUnwatchedCount() // get count of unwatched movies
        }
    }

    suspend fun getWatchedCount(): Int {
        return withContext(Dispatchers.IO) {
            watchlistMovieDao.getWatchedCount() // get count of watched movies
        }
    }

    suspend fun getMoviesWithDueReminders(): List<WatchlistMovie> {
        return withContext(Dispatchers.IO) {
            watchlistMovieDao.getMoviesWithDueReminders(System.currentTimeMillis()) // get movies with due reminders
        }
    }

    suspend fun updateWatchlistMovie(movie: WatchlistMovie) {
        withContext(Dispatchers.IO) {
            watchlistMovieDao.updateWatchlistMovie(movie) // update movie in watchlist
        }
    }


    
    
    /**
     * Update watchlist for current language - simple and clean
     */
    suspend fun updateWatchlistForCurrentLanguage() {
        withContext(Dispatchers.IO) {
            val allMovies = watchlistMovieDao.getAllWatchlistMoviesSync() // get all movies from database
            val currentLanguage = java.util.Locale.getDefault().language // get device language
            val isHebrew = currentLanguage == "iw" || currentLanguage == "he" // check if Hebrew
            
            allMovies.forEach { movie ->
                try {
                    val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY // get API key
                    val movieDetails = movieRepository.getMovieDetails(movie.id, apiKey) // fetch movie details from API
                    val genresString = movieDetails.genres.joinToString(",") { it.name } // join genres with commas
                    
                    val updatedMovie = movie.copy(
                        title = movieDetails.title, // update title with current language
                        titleEn = if (isHebrew) movie.titleEn else movieDetails.title, // set English title
                        titleHe = if (isHebrew) movieDetails.title else movie.titleHe, // set Hebrew title
                        genres = genresString // update genres with current language
                    )
                    watchlistMovieDao.updateWatchlistMovie(updatedMovie) // update movie in database
                } catch (e: Exception) {
                    // Keep original data if translation fails
                }
            }
        }
    }
}
