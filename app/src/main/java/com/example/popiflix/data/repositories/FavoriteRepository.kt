package com.example.popiflix.data.repository

import androidx.lifecycle.LiveData
import com.example.popiflix.data.database.FavoriteMovieDao
import com.example.popiflix.data.database.FavoriteMovie
import com.example.popiflix.data.models.Movie
import com.example.popiflix.data.models.MovieDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing favorite movies in the local Room database
 * 
 * This repository provides a clean API for:
 * - Adding/removing movies from favorites
 * - Querying favorite movies with different sorting options
 * - Checking if a movie is in favorites
 * - Managing user ratings and reviews
 * 
 * All database operations are performed on background threads using coroutines.
 */
@Singleton
class FavoriteRepository @Inject constructor(
    private val favoriteMovieDao: FavoriteMovieDao,
    private val movieRepository: com.example.popiflix.data.repositories.MovieRepository
) {

    /**
     * Get all favorite movies ordered by date added (newest first)
     * Returns LiveData for automatic UI updates
     */
    fun getAllFavorites(): LiveData<List<FavoriteMovie>> {
        return favoriteMovieDao.getAllFavorites()
    }


    /**
     * Get a specific favorite movie by its ID
     * Returns null if movie is not in favorites
     */
    suspend fun getFavoriteById(movieId: Int): FavoriteMovie? {
        return withContext(Dispatchers.IO) {
            favoriteMovieDao.getFavoriteById(movieId)
        }
    }

    /**
     * Search favorites by title (case-insensitive partial match)
     */
    suspend fun searchFavorites(query: String): List<FavoriteMovie> {
        return withContext(Dispatchers.IO) {
            favoriteMovieDao.searchFavorites(query)
        }
    }

    /**
     * Add a movie to favorites using Movie model
     * Converts Movie to FavoriteMovie entity
     */
    suspend fun addToFavorites(movie: Movie) {
        withContext(Dispatchers.IO) {
            val favoriteMovie = FavoriteMovie(
                id = movie.id,
                title = movie.title,
                titleEn = movie.title, // Always save English title
                titleHe = movie.title, // For now, save the same title in Hebrew
                overview = movie.overview,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount,
                popularity = movie.popularity
            )
            favoriteMovieDao.insertFavorite(favoriteMovie)
        }
    }

    /**
     * Add a movie to favorites using MovieDetails model
     * Converts MovieDetails to FavoriteMovie entity
     */
    suspend fun addToFavorites(movieDetails: MovieDetails) {
        withContext(Dispatchers.IO) {
            // Convert genres list to comma-separated string for storage
            val genresString = movieDetails.genres.joinToString(",") { it.name }
            
            val favoriteMovie = FavoriteMovie(
                id = movieDetails.id,
                title = movieDetails.title,
                titleEn = movieDetails.title, // Always save English title
                titleHe = movieDetails.title, // For now, save the same title in Hebrew
                overview = movieDetails.overview,
                posterPath = movieDetails.posterPath,
                backdropPath = movieDetails.backdropPath,
                releaseDate = movieDetails.releaseDate,
                voteAverage = movieDetails.voteAverage,
                voteCount = movieDetails.voteCount,
                popularity = movieDetails.popularity,
                genres = genresString  // Store genres as comma-separated string
            )
            favoriteMovieDao.insertFavorite(favoriteMovie)
        }
    }

    /**
     * Add a movie to favorites using FavoriteMovie entity directly
     * Useful for undo functionality
     */
    suspend fun addToFavorites(favoriteMovie: FavoriteMovie) {
        withContext(Dispatchers.IO) {
            favoriteMovieDao.insertFavorite(favoriteMovie)
        }
    }
    
    /**
     * Remove a movie from favorites using FavoriteMovie entity
     */
    suspend fun removeFromFavorites(movie: FavoriteMovie) {
        withContext(Dispatchers.IO) {
            favoriteMovieDao.deleteFavorite(movie)
        }
    }

    /**
     * Remove a movie from favorites using movie ID
     */
    suspend fun removeFromFavorites(movieId: Int) {
        withContext(Dispatchers.IO) {
            favoriteMovieDao.deleteFavoriteById(movieId)
        }
    }

    /**
     * Check if a movie is currently in favorites
     * Returns true if movie exists in favorites table
     */
    suspend fun isFavorite(movieId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            favoriteMovieDao.isFavorite(movieId)
        }
    }

    /**
     * Get total count of favorite movies
     */
    suspend fun getFavoritesCount(): Int {
        return withContext(Dispatchers.IO) {
            favoriteMovieDao.getFavoritesCount()
        }
    }


    /**
     * Update favorites for current language - simple and clean
     */
    suspend fun updateFavoritesForCurrentLanguage() {
        withContext(Dispatchers.IO) {
            val allMovies = favoriteMovieDao.getAllFavoritesSync()
            val currentLanguage = java.util.Locale.getDefault().language
            val isHebrew = currentLanguage == "iw" || currentLanguage == "he"
            
            allMovies.forEach { movie ->
                try {
                    val apiKey = com.example.popiflix.BuildConfig.TMDB_API_KEY
                    val movieDetails = movieRepository.getMovieDetails(movie.id, apiKey)
                    val genresString = movieDetails.genres.joinToString(",") { it.name }
                    
                    val updatedMovie = movie.copy(
                        title = movieDetails.title,
                        titleEn = if (isHebrew) movie.titleEn else movieDetails.title,
                        titleHe = if (isHebrew) movieDetails.title else movie.titleHe,
                        genres = genresString
                    )
                    favoriteMovieDao.updateFavorite(updatedMovie)
                } catch (e: Exception) {
                    // Keep original data if translation fails
                }
            }
        }
    }
}